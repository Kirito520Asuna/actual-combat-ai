package com.actual.combat.kafka.config;

import com.actual.combat.kafka.abs.AbstractKafkaListenerErrorHandler;
import com.actual.combat.kafka.listener.handler.GlobalKafkaListenerErrorHandler;
import com.actual.combat.kafka.service.Impl.KafkaServiceImpl;
import com.actual.combat.kafka.service.KafkaService;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author yan
 * @Date 2025/3/14 22:58:46
 * @Description
 */
@Configuration
@AllArgsConstructor
@ConditionalOnBean(KafkaProperties.class)
public class KafkaConfig {
    @Bean
    @ConditionalOnMissingBean(AbstractKafkaListenerErrorHandler.class)
    public AbstractKafkaListenerErrorHandler kafkaListenerErrorHandler() {
        return new GlobalKafkaListenerErrorHandler();
    }

    @Bean
    @ConditionalOnMissingBean(KafkaService.class)
    public KafkaService kafkaService() {
        return new KafkaServiceImpl();
    }
}
