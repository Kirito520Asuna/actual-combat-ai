package com.actual.combat.auth.security.config.bean;

import com.actual.combat.auth.security.abs.AbsSecurityConfig;
import com.actual.combat.auth.security.abs.SimpleSecurityConfig;
import com.actual.combat.auth.security.auth.SecurityExpressionRoot;
import com.actual.combat.auth.security.config.SecurityConfig;
import com.actual.combat.auth.security.service.impl.SimpleAuthSecurityService;
import com.actual.combat.auth.security.service.impl.SimpleLoginSecurityService;
import com.actual.combat.auth.security.service.impl.SimpleUserDetailsSecurityService;
import com.actual.combat.basic.core.abs.auth.core.AbsSecurityExpressionRoot;
import com.actual.combat.basic.core.abs.auth.service.AbsAuthService;
import com.actual.combat.basic.core.abs.auth.service.AbstractLoginService;
import com.actual.combat.basic.core.abs.auth.service.AbstractUserDetailsService;
import com.actual.combat.basic.core.abs.auth.service.SimpleUserDetailsService;
import com.actual.combat.basic.core.abs.bean.AbstractBean;
import com.actual.combat.basic.core.abs.bean.AbstractSecurityBean;
import com.actual.combat.basic.core.config.bean.BeanConfig;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @Author yan
 * @Date 2025/6/11 19:12:34
 * @Description
 */
@Slf4j
@AutoConfigureAfter(BeanBeforeSecurityConfig.class)
@Configuration
public class BeanSecurityConfig implements AbstractSecurityBean {

    @PostConstruct
    public void init() {
        log().info("==> Security <== class:{}",getAClassName());
    }

    @Bean
    @ConditionalOnMissingBean(AbsSecurityConfig.class)
    public AbsSecurityConfig authSecurityConfig() {
        return new SimpleSecurityConfig();
    }

    @Bean
    @ConditionalOnExpression("${config.auth.security.enable:true}")
    public SecurityConfig securityConfig() {
        return new SecurityConfig();
    }

    @Bean("auth")
    @ConditionalOnBean(SecurityConfig.class)
    @ConditionalOnMissingBean(AbsSecurityExpressionRoot.class)
    public AbsSecurityExpressionRoot securityExpressionRoot(){
        return new SecurityExpressionRoot();
    }

}
