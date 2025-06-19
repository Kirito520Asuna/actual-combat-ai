package com.actual.combat.auth.shiro.config.bean;

import com.actual.combat.auth.shiro.config.ShiroConfig;
import com.actual.combat.auth.shiro.service.impl.SimpleAuthShiroService;
import com.actual.combat.auth.shiro.service.impl.SimpleLoginShiroService;
import com.actual.combat.auth.shiro.service.impl.SimpleUserDetailsShiroService;
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
@AutoConfigureBefore(BeanShiroConfig.class)
public class BeanBeforeShiroConfig extends BeanConfig{


    @PostConstruct
    public void init() {
        log().info("==> Shiro <== class:{}", getAClassName());
    }

    @Bean
    @Override
    @ConditionalOnMissingBean(AbsAuthService.class)
    public AbsAuthService authService() {
        return new SimpleAuthShiroService();
    }

    @Bean
    //@ConditionalOnBean(ShiroConfig.class)
    @ConditionalOnMissingBean(AbstractLoginService.class)
    public AbstractLoginService authLoginService() {
        return new SimpleLoginShiroService();
    }

    @Bean
    //@ConditionalOnBean(ShiroConfig.class)
    @ConditionalOnMissingBean(AbstractUserDetailsService.class)
    public AbstractUserDetailsService authUserDetailsService() {
        return new SimpleUserDetailsShiroService();
    }

}
