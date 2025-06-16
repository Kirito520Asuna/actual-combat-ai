package com.actual_combat.kafka.service.Impl;

import com.actual_combat.kafka.listener.callback.KafkaListenableFutureCallback;
import com.actual_combat.kafka.service.KafkaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

/**
 * @Author yan
 * @Date 2025/3/14 23:41:38
 * @Description
 */
@Slf4j
public class KafkaServiceImpl implements KafkaService {
    @Override
    public ListenableFutureCallback fetchListenableFutureCallback() {
        return new KafkaListenableFutureCallback();
    }

    @Override
    public void send(String topic, String json) {
        ListenableFuture future = getTemplate().send(topic, json);
        future.addCallback(fetchListenableFutureCallback());
    }
}
