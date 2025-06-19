package com.actual.combat.basic.file.core.config;


import cn.hutool.extra.spring.SpringUtil;
import com.google.common.collect.Maps;
import com.actual.combat.basic.file.core.message.listener.FileRedisMessageListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.util.Map;

/**
 * @Author yan
 * @Date 2025/2/5 13:02:50
 * @Description
 */
@ConditionalOnBean(MessageListener.class)
@Configuration
public class FileRedisMessageConfig {

    @Bean
    @ConditionalOnBean(RedisConnectionFactory.class)
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        Map<String, Class<? extends MessageListener>> listeners = Maps.newLinkedHashMap(FileRedisMessageListener.getListeners());

        listeners.entrySet().forEach(entry -> {
            String pattern = entry.getKey();
            Class<? extends MessageListener> entryValue = entry.getValue();
            MessageListener messageListener = SpringUtil.getBean(entryValue);
            container.addMessageListener(messageListener, new PatternTopic(pattern));
        });

        return container;
    }


}