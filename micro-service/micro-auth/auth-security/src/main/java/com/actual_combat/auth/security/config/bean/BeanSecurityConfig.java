package com.actual_combat.auth.security.config.bean;

import com.actual_combat.auth.security.abs.AbsSecurityConfig;
import com.actual_combat.auth.security.abs.SimpleSecurityConfig;
import com.actual_combat.auth.security.auth.SecurityExpressionRoot;
import com.actual_combat.auth.security.config.SecurityConfig;
import com.actual_combat.auth.security.service.impl.SimpleAuthSecurityService;
import com.actual_combat.auth.security.service.impl.SimpleLoginSecurityService;
import com.actual_combat.basic.core.abs.auth.core.AbsSecurityExpressionRoot;
import com.actual_combat.basic.core.abs.auth.service.AbsAuthService;
import com.actual_combat.basic.core.abs.auth.service.AbstractLoginService;
import com.actual_combat.basic.core.abs.auth.service.AbstractUserDetailsService;
import com.actual_combat.basic.core.abs.auth.service.SimpleUserDetailsService;
import com.actual_combat.base.core.abs.auth.service.*;
import com.actual_combat.basic.core.abs.bean.AbstractBean;
import com.actual_combat.basic.core.abs.bean.AbstractSecurityBean;
import lombok.extern.slf4j.Slf4j;
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
@AutoConfigureBefore(AbstractBean.class)
@Configuration
public class BeanSecurityConfig implements AbstractSecurityBean {

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

    @Bean
    @ConditionalOnBean(SecurityConfig.class)
    @ConditionalOnMissingBean(AbsSecurityExpressionRoot.class)
    public AbsSecurityExpressionRoot securityExpressionRoot(){
        return new SecurityExpressionRoot();
    }

    @Bean
    @ConditionalOnBean(SecurityConfig.class)
    @ConditionalOnMissingBean(AbsAuthService.class)
    public AbsAuthService authService() {
        return new SimpleAuthSecurityService();
    }

    @Bean
    @ConditionalOnBean(SecurityConfig.class)
    @ConditionalOnMissingBean(AbstractLoginService.class)
    public AbstractLoginService authLoginService() {
        return new SimpleLoginSecurityService();
    }

    @Bean
    @ConditionalOnBean(SecurityConfig.class)
    @ConditionalOnMissingBean(AbstractUserDetailsService.class)
    public AbstractUserDetailsService authUserDetailsService() {
        return new SimpleUserDetailsService();
    }

}
