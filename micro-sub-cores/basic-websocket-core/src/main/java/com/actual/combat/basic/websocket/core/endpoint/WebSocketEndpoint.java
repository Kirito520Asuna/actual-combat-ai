package com.actual.combat.basic.websocket.core.endpoint;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.actual.combat.aop.abs.bean.AbsBean;
import com.actual.combat.basic.utils.bean.CustomBeanUtils;
import com.actual.combat.basic.utils.object.ObjectUtils;
import com.actual.combat.basic.websocket.core.constant.WebSocket;
import com.actual.combat.basic.websocket.core.domain.Message;
import com.actual.combat.basic.websocket.core.service.MessageService;
import com.actual.combat.nacos.utils.NaCosUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/ws/{userId}")
@Component
public class WebSocketEndpoint implements AbsBean {


    // 本地存储当前实例的在线用户（线程安全）
    public static final Set<String> LOCAL_USER_IDS = ConcurrentHashMap.newKeySet();

    // 本地会话存储（线程安全）
    public static final ConcurrentHashMap<String, Session> SESSION_MAP = new ConcurrentHashMap<>();

    // 静态依赖注入
    @Resource
    private RedisTemplate<String, String> redisTemplate = getRedisTemplate();


    // 当前实例ID（格式：ip:port）


    // --- 生命周期管理 ---
    @PostConstruct
    public void init() {
        redisTemplate = getRedisTemplate();
    }

    @PreDestroy
    public void destroy() {
        // 清理Redis中当前实例的所有数据
        redisTemplate.delete(WebSocket.WS_INSTANCE + getInstanceId());
        LOCAL_USER_IDS.forEach(userId ->
                redisTemplate.delete(WebSocket.WS_USER + userId)
        );
    }

    public static String getInstanceId() {
        return NaCosUtils.getInstanceId();
    }
    public static RedisTemplate getRedisTemplate() {
        return SpringUtil.getBean(RedisTemplate.class);
    }

    // --- WebSocket事件处理 ---
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        String sessionId = session.getId();
        String instanceId = getInstanceId();
        String userKey = WebSocket.WS_USER + userId;
        String instanceKey = WebSocket.WS_INSTANCE + instanceId;

        log().debug("WebSocket连接已建立，实例ID：{}，用户ID：{}", instanceId, userId);
        // 存储会话和用户信息
        SESSION_MAP.put(sessionId, session);
        LOCAL_USER_IDS.add(userId);

        // 更新Redis映射（用户->实例）
        redisTemplate.opsForValue().set(userKey, instanceId);
        // 存储实例的会话信息（实例->会话ID:用户ID）
        redisTemplate.opsForHash().put(instanceKey, sessionId, userId);
    }

    @OnClose
    public void onClose(Session session, @PathParam("userId") String userId) {
        String sessionId = session.getId();
        String instanceId = getInstanceId();
        String userKey = WebSocket.WS_USER + userId;
        String instanceKey = WebSocket.WS_INSTANCE + instanceId;

        log().debug("WebSocket连接已关闭，实例ID：{}，用户ID：{}", instanceId, userId);
        // 清理本地存储
        SESSION_MAP.remove(sessionId);
        LOCAL_USER_IDS.remove(userId);

        // 清理Redis数据
        redisTemplate.opsForHash().delete(instanceKey, sessionId);
        redisTemplate.delete(userKey);
    }

    @OnMessage
    public void onMessage(String message, Session session, @PathParam("userId") String senderId) {
        MessageService messageService = SpringUtil.getBean(MessageService.class);
        log().debug("收到消息: {}", message);
        boolean isTypeJSON = JSONUtil.isTypeJSON(message);
        if (isTypeJSON) {
            try {
                Map<String,Object> map = JSONUtil.toBean(message, Map.class);
                Message msg = new Message();
                CustomBeanUtils.copyPropertiesIgnoreNull(map,msg);
                String instanceId = getInstanceId();
                msg.setSendInstanceId(instanceId);
                messageService.onMessage(msg);
            } catch (Exception e) {
                // 异常处理逻辑
                log().error("处理消息时发生异常: {}", e.getMessage());
            }
        }
    }

    // 获取当前实例在线用户列表
    public static Set<String> getLocalOnlineUsers() {
        return Collections.unmodifiableSet(LOCAL_USER_IDS);
    }

    // --- 辅助方法 ---
    public static Session findSessionByUserId(String userId) {
        String key = WebSocket.WS_INSTANCE + getInstanceId();

        RedisTemplate<String, String> redisTemplate = getRedisTemplate();
        return SESSION_MAP.values().stream()
                .filter(session -> {
                    String sessionUserId = (String) redisTemplate.opsForHash()
                            .get(key, session.getId());
                    return ObjectUtils.equals(userId, sessionUserId);
                })
                .findFirst()
                .orElse(null);
    }
}