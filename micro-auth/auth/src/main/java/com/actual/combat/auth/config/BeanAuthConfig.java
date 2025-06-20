package com.actual.combat.auth.config;

import com.actual.combat.aop.abs.bean.AbsBean;
import com.actual.combat.auth.service.AuthUserService;
import com.actual.combat.auth.service.impl.SecurityAuthUserService;
import com.actual.combat.auth.service.impl.ShiroAuthUserService;
import com.actual.combat.auth.service.impl.SimpleAuthUserService;
import com.actual.combat.auth.service.impl.mp.AuthUserMpService;
import com.actual.combat.auth.shiro.abs.AbsAuthorizationShiro;
import com.actual.combat.basic.core.abs.api.service.AbstractApiSaltService;
import com.actual.combat.basic.core.abs.api.service.SimpleApiSaltService;
import com.actual.combat.basic.core.abs.auth.config.AbsAuthSecurityConfig;
import com.actual.combat.basic.core.abs.auth.config.AbsAuthShiroConfig;
import com.actual.combat.basic.core.abs.auth.core.AbsSecurityAuth;
import com.actual.combat.basic.core.abs.auth.core.AbsShiroAuth;
import com.actual.combat.basic.core.abs.bean.AbstractAuthBean;
import com.actual.combat.basic.core.abs.bean.AbstractSecurityBean;
import com.actual.combat.basic.core.abs.bean.AbstractShiroBean;
import com.actual.combat.mp.abs.handler.AbsEntityHandler;
import com.actual.combat.mp.abs.service.MpUserService;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author yan
 * @Date 2025/6/14 01:37:33
 * @Description
 */
@Configuration
@AutoConfigureAfter({AbstractSecurityBean.class,AbstractShiroBean.class})
//@AutoConfigureBefore({AbstractBean.class})
public class BeanAuthConfig implements AbstractAuthBean {
    @Override
    @PostConstruct
    public void init() {
        AbstractAuthBean.super.init();
    }
    @Bean
    @ConditionalOnMissingBean(AbstractApiSaltService.class)
    public AbstractApiSaltService apiSaltService() {
        return new SimpleApiSaltService();
    }

    @Bean
    @ConditionalOnBean(AbsAuthShiroConfig.class)
    @ConditionalOnMissingBean(AuthUserService.class)
    public AuthUserService shiroAuthUserService() {
        log().debug("[AuthShiro]-[shiroAuthUserService]::[{}]",getAClassName());
        return new ShiroAuthUserService();
    }

    @Bean
    @ConditionalOnBean(AbsAuthSecurityConfig.class)
    @ConditionalOnMissingBean(AuthUserService.class)
    public AuthUserService securityAuthUserService() {
        log().debug("[AuthSecurity]-[securityAuthUserService]::[{}]",getAClassName());
        return new SecurityAuthUserService();
    }

    @Bean
    @ConditionalOnMissingBean({AbsShiroAuth.class, AbsSecurityAuth.class, AuthUserService.class})
    public AuthUserService authUserService() {
        log().warn("AuthUserService==>SimpleAuthUserService");
        return new SimpleAuthUserService();
    }

    @Bean
    @ConditionalOnBean(AbsEntityHandler.class)
    @ConditionalOnMissingBean(MpUserService.class)
    public MpUserService authUserMpService() {
        return new AuthUserMpService();
    }
}
