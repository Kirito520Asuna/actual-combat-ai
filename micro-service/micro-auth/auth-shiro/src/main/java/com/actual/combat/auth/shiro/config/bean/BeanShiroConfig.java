package com.actual.combat.auth.shiro.config.bean;

import com.actual.combat.auth.shiro.config.ShiroConfig;
import com.actual.combat.auth.shiro.service.impl.SimpleAuthShiroService;
import com.actual.combat.basic.core.abs.auth.service.AbsAuthService;
import com.actual.combat.basic.core.abs.bean.AbstractBean;
import com.actual.combat.basic.core.abs.bean.AbstractShiroBean;
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
@AutoConfigureBefore(AbstractBean.class)
@Configuration
public class BeanShiroConfig implements AbstractShiroBean {

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
