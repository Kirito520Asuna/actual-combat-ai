package com.actual.combat.basic.websocket.core.listener;

import cn.hutool.extra.spring.SpringUtil;
import com.actual.combat.basic.websocket.core.constant.WebSocket;
import com.google.common.collect.Maps;
import com.actual.combat.basic.websocket.core.service.MessageService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;

/**
 * @Author yan
 * @Date 2025/2/5 13:12:46
 * @Description
 */
@Configuration
@ConditionalOnBean(RedisTemplate.class)
public class RedisMessageListener implements MessageListener {
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel());
        String msg = new String(message.getBody());
        SpringUtil.getBean(MessageService.class).handleRedisMessage(msg);
    }

    public static void sendRedisMessage(String targetInstanceId, Object message) {
        RedisTemplate bean = SpringUtil.getBean(RedisTemplate.class);
        bean.convertAndSend(WebSocket.WS_MSG + targetInstanceId, message);
    }

    public static Map<String, Class<? extends MessageListener>> getListeners() {
        Map<String, Class<? extends MessageListener>> listeners = Maps.newLinkedHashMap();
        listeners.put(WebSocket.WS_MSG + "*", RedisMessageListener.class);
        return listeners;
    }
}
