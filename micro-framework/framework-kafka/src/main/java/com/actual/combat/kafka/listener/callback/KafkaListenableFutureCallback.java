package com.actual.combat.kafka.listener.callback;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFutureCallback;

/**
 * @Author yan
 * @Date 2025/3/21 10:48:08
 * @Description
 */
@Slf4j
public class KafkaListenableFutureCallback implements ListenableFutureCallback<SendResult<String, String>> {
    @Override
    public void onSuccess(SendResult<String, String> result) {
        ProducerRecord<String, String> producerRecord = result.getProducerRecord();
        RecordMetadata recordMetadata = result.getRecordMetadata();
        Integer partition = producerRecord.partition();
        String key = producerRecord.key();
        String value = producerRecord.value();
        long offset = recordMetadata.offset();
        String topic = recordMetadata.topic();
        // 处理成功发送后的逻辑
        log.debug("""
                Kafka message sent ok：==>\n{Topic:{}\nPartition:{}\nOffset:{}\nKey:{}\nValue:{}\n}<==
                """,topic,partition,offset,key,value);
    }

    @Override
    public void onFailure(Throwable ex) {
        log.error(ex.getMessage(), ex);
    }
}
