package com.actual_combat.base.core.config;

import com.actual_combat.base.core.config.api.ApiConfig;
import com.actual_combat.base.core.config.jwt.JwtConfig;
import com.actual_combat.base.core.filter.CorsRequestFilter;
import com.actual_combat.base.core.properties.cors.CorsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;


/**
 * @Author yan
 * @Date 2025/6/11 19:12:34
 * @Description
 */
@Slf4j
@Configurable
public class BeanConfig {
    @Bean
    @ConditionalOnExpression("${config.api.enable:true}")
    public ApiConfig apiConfig() {
        log.debug("ApiConfig已配置");
        return new ApiConfig();
    }

    @Bean
    @ConditionalOnExpression("${config.jwt.enable:true}")
    public JwtConfig jwtConfig() {
        log.debug("JwtConfig已配置");
        return new JwtConfig();
    }

    @Bean
    @ConditionalOnBean(CorsProperties.class)
    public CorsRequestFilter corsRequestFilter() {
        return new CorsRequestFilter();
    }
}
