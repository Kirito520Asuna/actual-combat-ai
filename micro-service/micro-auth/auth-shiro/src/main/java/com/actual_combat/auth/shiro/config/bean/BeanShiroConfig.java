package com.actual_combat.auth.shiro.config.bean;

import com.actual_combat.auth.shiro.config.ShiroConfig;
import com.actual_combat.auth.shiro.service.impl.SimpleAuthShiroService;
import com.actual_combat.base.core.abs.auth.service.AbsAuthService;
import com.actual_combat.base.core.config.bean.BeanConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author yan
 * @Date 2025/6/13 23:34:39
 * @Description
 */
@Slf4j
@AutoConfigureBefore(BeanConfig.class)
@Configuration
public class BeanShiroConfig {

    @Bean
    @ConditionalOnExpression("${config.auth.shiro.enable:true}")
    public ShiroConfig shiroConfig() {
        return new ShiroConfig();
    }

    @Bean
    @ConditionalOnBean(ShiroConfig.class)
    @ConditionalOnMissingBean(AbsAuthService.class)
    public AbsAuthService authService() {
        return new SimpleAuthShiroService();
    }
}
