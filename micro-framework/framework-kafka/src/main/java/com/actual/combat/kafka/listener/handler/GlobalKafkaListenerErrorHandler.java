package com.actual.combat.kafka.listener.handler;

import com.actual.combat.kafka.abs.AbstractKafkaListenerErrorHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.messaging.Message;

/**
 * @Author yan
 * @Date 2025/3/14 23:31:37
 * @Description
 */
@Slf4j
public class GlobalKafkaListenerErrorHandler implements AbstractKafkaListenerErrorHandler {
    @Override
    public Object handleError(Message<?> message, ListenerExecutionFailedException e) {
        // 处理异常消息，例如记录日志、重试等
        log.error("Error handling message: {}", message.getPayload());
        return null; // 返回null表示不重试，可以根据需求返回其他值
    }
}
