package com.actual.combat.auth.shiro.config.bean;

import com.actual.combat.auth.shiro.config.ShiroConfig;
import com.actual.combat.basic.core.abs.auth.service.AbsAuthService;
import com.actual.combat.basic.core.abs.bean.AbstractBean;
import com.actual.combat.basic.core.abs.bean.AbstractShiroBean;
import com.actual.combat.basic.core.config.bean.BeanConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author yan
 * @Date 2025/6/13 23:34:39
 * @Description
 */
@Slf4j
@AutoConfigureAfter({BeanBeforeShiroConfig.class, BeanConfig.class})
@Configuration
public class BeanShiroConfig implements AbstractShiroBean {


    @Bean
    @ConditionalOnBean(AbsAuthService.class)
    @ConditionalOnExpression("${config.auth.shiro.enable:true}")
    public ShiroConfig shiroConfig() {
        return new ShiroConfig();
    }
}
