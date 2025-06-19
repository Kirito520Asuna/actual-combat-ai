package com.actual.combat.auth.shiro.config.bean;

import com.actual.combat.auth.shiro.config.ShiroConfig;
import com.actual.combat.auth.shiro.service.impl.SimpleLoginShiroService;
import com.actual.combat.auth.shiro.service.impl.SimpleUserDetailsShiroService;
import com.actual.combat.basic.core.abs.auth.service.AbsAuthService;
import com.actual.combat.basic.core.abs.auth.service.AbstractLoginService;
import com.actual.combat.basic.core.abs.auth.service.AbstractUserDetailsService;
import com.actual.combat.basic.core.abs.bean.AbstractShiroBean;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
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
@AutoConfigureAfter(BeanBeforeShiroConfig.class)
@Configuration
public class BeanShiroConfig implements AbstractShiroBean {
    @PostConstruct
    public void init() {
        log().info("==> Shiro <== class:{}", getAClassName());
    }

    @Bean
    @ConditionalOnBean(AbsAuthService.class)
    @ConditionalOnExpression("${config.auth.shiro.enable:true}")
    public ShiroConfig shiroConfig() {
        return new ShiroConfig();
    }

    @Bean
    @ConditionalOnBean(ShiroConfig.class)
    @ConditionalOnMissingBean(AbstractLoginService.class)
    public AbstractLoginService authLoginService() {
        return new SimpleLoginShiroService();
    }

    @Bean
    @ConditionalOnBean(ShiroConfig.class)
    @ConditionalOnMissingBean(AbstractUserDetailsService.class)
    public AbstractUserDetailsService authUserDetailsService() {
        return new SimpleUserDetailsShiroService();
    }
}
