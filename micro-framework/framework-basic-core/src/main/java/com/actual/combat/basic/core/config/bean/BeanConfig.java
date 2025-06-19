package com.actual.combat.basic.core.config.bean;

import cn.hutool.extra.spring.SpringUtil;
import com.actual.combat.aop.abs.bean.AbsBean;
import com.actual.combat.basic.core.abs.api.AbsApiFiler;
import com.actual.combat.basic.core.abs.api.AbsApiInterceptor;
import com.actual.combat.basic.core.abs.auth.AbsAuthFilter;
import com.actual.combat.basic.core.abs.auth.AbsAuthInterceptor;
import com.actual.combat.basic.core.abs.auth.service.*;
import com.actual.combat.basic.core.abs.bean.AbstractBean;
import com.actual.combat.basic.core.abs.bean.AbstractGatewayBean;
import com.actual.combat.basic.core.config.api.ApiConfig;
import com.actual.combat.basic.core.config.jwt.JwtConfig;
import com.actual.combat.basic.core.constant.ExpressionConstants;
import com.actual.combat.basic.core.filter.ApiFilter;
import com.actual.combat.basic.core.filter.CorsRequestFilter;
import com.actual.combat.basic.core.interceptor.ApiInterceptor;
import com.actual.combat.basic.core.properties.cors.CorsProperties;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;


/**
 * @Author yan
 * @Date 2025/6/11 19:12:34
 * @Description
 */
@Slf4j
@AutoConfiguration
@ConditionalOnMissingBean({AbstractBean.class, AbstractGatewayBean.class})
public class BeanConfig implements AbstractBean {
    @Override
    @PostConstruct
    public void init() {
        AbstractBean.super.init();
    }

    @Bean
    @ConditionalOnExpression("${config.api.enable:true}")
    @ConditionalOnMissingBean(ApiConfig.class)
    public ApiConfig apiConfig() {
        log.debug("ApiConfig已配置");
        return new ApiConfig();
    }

    @Bean
    @ConditionalOnExpression("${config.jwt.enable:true}")
    @ConditionalOnMissingBean(JwtConfig.class)
    public JwtConfig jwtConfig() {
        log.debug("JwtConfig已配置");
        return new JwtConfig();
    }
    @Bean
    @ConditionalOnMissingBean(CorsProperties.class)
    public CorsProperties corsProperties() {
        log.debug("CorsProperties已配置");
        return new CorsProperties();
    }

    @Bean
    @ConditionalOnBean(CorsProperties.class)
    @ConditionalOnMissingBean(CorsRequestFilter.class)
    public CorsRequestFilter corsRequestFilter() {
        log.debug("CorsRequestFilter已配置");
        return new CorsRequestFilter();
    }

    @Bean
    @ConditionalOnExpression(ExpressionConstants.filterExpression)
    @ConditionalOnMissingBean({BeanFilter.class})
    public BeanFilter beanFilter() {
        return new BeanFilter();
    }

    @Bean
    @ConditionalOnMissingBean({BeanFilter.class,BeanInterceptor.class})
    public BeanInterceptor beanInterceptor() {
        return new BeanInterceptor();
    }

    @Bean
    @ConditionalOnBean(BeanFilter.class)
    @ConditionalOnMissingBean({AbsApiInterceptor.class,AbsApiFiler.class})
    public AbsApiFiler apiFiler() {
        return new ApiFilter();
    }

    @Bean
    @ConditionalOnBean(BeanInterceptor.class)
    @ConditionalOnMissingBean({AbsApiFiler.class,AbsApiInterceptor.class})
    public AbsApiInterceptor apiInterceptor() {
        return new ApiInterceptor();
    }

    @Bean
    @ConditionalOnMissingBean({AbsAuthService.class})
    public AbsAuthService authService() {
        return new SimpleAuthService();
    }

    @Bean
    @ConditionalOnMissingBean({AbstractLoginService.class})
    public AbstractLoginService authLoginService() {
        return new SimpleLoginService();
    }

    @Bean
    @ConditionalOnMissingBean({AbstractUserService.class})
    public AbstractUserService authUserService() {
        return new SimpleUserService();
    }

    @Bean
    @ConditionalOnMissingBean(AbstractUserDetailsService.class)
    public AbstractUserDetailsService authUserDetailsService(){
        return new SimpleUserDetailsService();
    }

    @Bean
    @ConditionalOnBean(BeanFilter.class)
    @ConditionalOnMissingBean({AbsAuthInterceptor.class, AbsAuthFilter.class})
    public AbsAuthFilter authFiler() {
        AbsAuthService auth = SpringUtil.getBean(AbsAuthService.class);
        AbsAuthFilter authFiler = auth.getAuthFiler();
        return authFiler;
    }

    @Bean
    @ConditionalOnBean(BeanInterceptor.class)
    @ConditionalOnMissingBean({AbsAuthFilter.class,AbsAuthInterceptor.class})
    public AbsAuthInterceptor authInterceptor() {
        AbsAuthService auth = SpringUtil.getBean(AbsAuthService.class);
        AbsAuthInterceptor authInterceptor = auth.getAuthInterceptor();
        return authInterceptor;
    }
}
