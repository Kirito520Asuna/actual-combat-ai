package com.actual_combat.base.core.config.bean;

import cn.hutool.extra.spring.SpringUtil;
import com.actual_combat.base.core.abs.api.AbsApiFiler;
import com.actual_combat.base.core.abs.api.AbsApiInterceptor;
import com.actual_combat.base.core.abs.auth.AbsAuthFiler;
import com.actual_combat.base.core.abs.auth.AbsAuthInterceptor;
import com.actual_combat.base.core.abs.auth.service.*;
import com.actual_combat.base.core.config.api.ApiConfig;
import com.actual_combat.base.core.config.jwt.JwtConfig;
import com.actual_combat.base.core.constant.ExpressionConstants;
import com.actual_combat.base.core.filter.ApiFilter;
import com.actual_combat.base.core.filter.CorsRequestFilter;
import com.actual_combat.base.core.interceptor.ApiInterceptor;
import com.actual_combat.base.core.properties.cors.CorsProperties;
import lombok.extern.slf4j.Slf4j;

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
@Configuration
public class BeanConfig {
    @Bean
    @ConditionalOnExpression("${config.api.enable:true}")
    public ApiConfig apiConfig() {
        log.debug("ApiConfig已配置");
        return new ApiConfig();
    }

    @Bean
    @ConditionalOnExpression("${config.jwt.enable:true}")
    public JwtConfig jwtConfig() {
        log.debug("JwtConfig已配置");
        return new JwtConfig();
    }

    @Bean
    @ConditionalOnBean(CorsProperties.class)
    @ConditionalOnMissingBean(CorsRequestFilter.class)
    public CorsRequestFilter corsRequestFilter() {
        return new CorsRequestFilter();
    }

    @Bean
    @ConditionalOnExpression(ExpressionConstants.filterExpression)
    public BeanFilter beanFilter() {
        return new BeanFilter();
    }

    @Bean
    @ConditionalOnMissingBean(BeanFilter.class)
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
    @ConditionalOnMissingBean({AbsAuthInterceptor.class,AbsAuthFiler.class})
    public AbsAuthFiler authFiler() {
        AbsAuthService auth = SpringUtil.getBean(AbsAuthService.class);
        AbsAuthFiler authFiler = auth.getAuthFiler();
        return authFiler;
    }

    @Bean
    @ConditionalOnBean(BeanInterceptor.class)
    @ConditionalOnMissingBean({AbsAuthFiler.class,AbsAuthInterceptor.class})
    public AbsAuthInterceptor authInterceptor() {
        AbsAuthService auth = SpringUtil.getBean(AbsAuthService.class);
        AbsAuthInterceptor authInterceptor = auth.getAuthInterceptor();
        return authInterceptor;
    }
}
