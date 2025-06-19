package com.actual.combat.basic.file.core.message.listener;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.actual.combat.basic.core.constant.file.FileConstant;
import com.google.common.collect.Maps;
import com.actual.combat.basic.file.core.config.FileUploadConfig;
import com.actual.combat.basic.file.core.service.FileService;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Author yan
 * @Date 2025/2/5 13:12:46
 * @Description
 */
@Component
public class FileRedisMessageListener implements MessageListener {
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel());
        String msg = new String(message.getBody());
        if (JSONUtil.isTypeJSON(msg)) {
            String fileName = null;
            String folder = null;
            String identifier = null;
            Integer chunkNumber = null;
            SpringUtil.getBean(FileService.class).delByteByLocal(fileName, folder, identifier, chunkNumber);
        }
    }

    public static void sendRedisMessage(String message) {
        RedisTemplate bean = SpringUtil.getBean(RedisTemplate.class);
        bean.convertAndSend(FileConstant.FILE_REDIS_MSG + FileUploadConfig.getInstanceId(), message);
    }

    public static Map<String, Class<? extends MessageListener>> getListeners() {
        Map<String, Class<? extends MessageListener>> listeners = Maps.newLinkedHashMap();
        listeners.put(FileConstant.FILE_REDIS_MSG + "*", FileRedisMessageListener.class);
        return listeners;
    }
}
