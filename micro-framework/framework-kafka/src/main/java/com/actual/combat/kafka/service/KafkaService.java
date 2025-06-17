package com.actual.combat.kafka.service;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

/**
 * @Author yan
 * @Date 2025/3/14 23:39:05
 * @Description
 */
public interface KafkaService {

    /**
     * 获取kafka模板
     * @return
     */
    default KafkaTemplate getTemplate() {
        return SpringUtil.getBean(KafkaTemplate.class);
    }

    /**
     * 获取回调函数
     * @return
     */
    ListenableFutureCallback fetchListenableFutureCallback();

    /**
     * 发送消息
     * @param topic
     * @param json
     */
    default void send(String topic, String json) {
        ListenableFuture future = getTemplate().send(topic, json);
        future.addCallback(fetchListenableFutureCallback());
    }
}
