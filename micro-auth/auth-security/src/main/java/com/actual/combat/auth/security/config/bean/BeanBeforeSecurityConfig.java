package com.actual.combat.auth.security.config.bean;

import com.actual.combat.auth.security.config.SecurityConfig;
import com.actual.combat.auth.security.service.impl.SimpleAuthSecurityService;
import com.actual.combat.auth.security.service.impl.SimpleLoginSecurityService;
import com.actual.combat.auth.security.service.impl.SimpleUserDetailsSecurityService;
import com.actual.combat.basic.core.abs.auth.service.AbsAuthService;
import com.actual.combat.basic.core.abs.auth.service.AbstractLoginService;
import com.actual.combat.basic.core.abs.auth.service.AbstractUserDetailsService;
import com.actual.combat.basic.core.config.bean.BeanConfig;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author yan
 * @Date 2025/6/18 18:00:45
 * @Description
 */
@Slf4j
@Configuration
@AutoConfigureBefore(BeanSecurityConfig.class)
public class BeanBeforeSecurityConfig extends BeanConfig {

    @PostConstruct
    public void init() {
        log().info("==> Security <== class:{}", getAClassName());
    }

    @Bean
    @Override
    @ConditionalOnMissingBean(AbsAuthService.class)
    public AbsAuthService authService() {
        return new SimpleAuthSecurityService();
    }


    @Bean
    //@ConditionalOnBean(SecurityConfig.class)
    @ConditionalOnMissingBean(AbstractLoginService.class)
    public AbstractLoginService authLoginService() {
        return new SimpleLoginSecurityService();
    }

    @Bean
    //@ConditionalOnBean(SecurityConfig.class)
    @ConditionalOnMissingBean(AbstractUserDetailsService.class)
    public AbstractUserDetailsService authUserDetailsService() {
        return new SimpleUserDetailsSecurityService();
    }


}
