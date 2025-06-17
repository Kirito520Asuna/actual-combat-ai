package com.actual.combat.auth.config;

import com.actual_combat.aop.abs.bean.AbsBean;
import com.actual.combat.auth.security.abs.AbsAuthorizationSecurity;
import com.actual.combat.auth.service.AuthUserService;
import com.actual.combat.auth.service.impl.SecurityAuthUserService;
import com.actual.combat.auth.service.impl.ShiroAuthUserService;
import com.actual.combat.auth.service.impl.SimpleAuthUserService;
import com.actual.combat.auth.service.impl.mp.AuthUserMpService;
import com.actual.combat.auth.shiro.abs.AbsAuthorizationShiro;
import com.actual.combat.basic.core.abs.bean.AbstractAuthBean;
import com.actual.combat.basic.core.abs.bean.AbstractBean;
import com.actual.combat.basic.core.abs.bean.AbstractSecurityBean;
import com.actual.combat.basic.core.abs.bean.AbstractShiroBean;
import com.actual.combat.mp.abs.handler.AbsEntityHandler;
import com.actual.combat.mp.abs.service.MpUserService;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
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
@AutoConfigureBefore({AbstractSecurityBean.class,AbstractShiroBean.class,AbstractBean.class})
public class BeanAuthConfig implements AbsBean, AbstractAuthBean {

    @Bean
    @ConditionalOnBean(AbsAuthorizationShiro.class)
    @ConditionalOnMissingBean(AuthUserService.class)
    public AuthUserService shiroAuthUserService() {
        return new ShiroAuthUserService();
    }

    @Bean
    @ConditionalOnBean(AbsAuthorizationSecurity.class)
    @ConditionalOnMissingBean(AuthUserService.class)
    public AuthUserService securityAuthUserService() {
        return new SecurityAuthUserService();
    }

    @Bean
    @ConditionalOnMissingBean({AbsAuthorizationShiro.class, AbsAuthorizationSecurity.class, AuthUserService.class})
    public AuthUserService authUserService() {
        return new SimpleAuthUserService();
    }

    @Bean
    @ConditionalOnBean(AbsEntityHandler.class)
    @ConditionalOnMissingBean(MpUserService.class)
    public MpUserService authUserMpService() {
        return new AuthUserMpService();
    }
}
