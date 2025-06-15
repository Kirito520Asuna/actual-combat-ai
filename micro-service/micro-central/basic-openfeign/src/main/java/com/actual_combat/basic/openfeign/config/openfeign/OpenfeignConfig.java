package com.actual_combat.basic.openfeign.config.openfeign;

import feign.Feign;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @Author yan
 * @Date 2024/9/28 下午8:38:11
 * @Description
 */
@Configuration
@EnableFeignClients(basePackages = {"com.actual_combat.basic.openfeign.factory.interfaces"})
public class OpenfeignConfig {
    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public Feign.Builder feignBuilder() {
        return Feign.builder();
    }

}
