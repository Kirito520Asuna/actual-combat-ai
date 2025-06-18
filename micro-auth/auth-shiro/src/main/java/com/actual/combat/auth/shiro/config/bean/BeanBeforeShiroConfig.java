package com.actual.combat.auth.shiro.config.bean;

import com.actual.combat.auth.shiro.service.impl.SimpleAuthShiroService;
import com.actual.combat.basic.core.abs.auth.service.AbsAuthService;
import com.actual.combat.basic.core.config.bean.BeanConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
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

    //@Bean
    //@ConditionalOnExpression("${config.api.enable:true}")
    //@ConditionalOnMissingBean(ApiConfig.class)
    //public ApiConfig apiConfig() {
    //    log.debug("ApiConfig已配置");
    //    return new ApiConfig();
    //}
    //
    //@Bean
    //@ConditionalOnExpression("${config.jwt.enable:true}")
    //@ConditionalOnMissingBean(JwtConfig.class)
    //public JwtConfig jwtConfig() {
    //    log.debug("JwtConfig已配置");
    //    return new JwtConfig();
    //}

    @Bean
    @Override
    @ConditionalOnMissingBean(AbsAuthService.class)
    public AbsAuthService authService() {
        return new SimpleAuthShiroService();
    }

}
