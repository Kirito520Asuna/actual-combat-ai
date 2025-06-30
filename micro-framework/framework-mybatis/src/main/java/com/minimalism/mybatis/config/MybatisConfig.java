package com.minimalism.mybatis.config;

import com.minimalism.mybatis.abs.config.AbsMybatisConfig;
import com.minimalism.mybatis.abs.handler.AbsEntityHandler;
import com.minimalism.mybatis.abs.service.DataScopeService;
import com.minimalism.mybatis.abs.service.MpUserService;
import com.minimalism.mybatis.abs.service.impl.DataScopeDefaultServiceImpl;
import com.minimalism.mybatis.abs.service.impl.MpUserServiceImpl;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 */
@Configuration
@ConditionalOnMissingBean(AbsMybatisConfig.class)
public class MybatisConfig implements AbsMybatisConfig {
    @Bean
    @ConditionalOnBean(AbsEntityHandler.class)
    @ConditionalOnMissingBean(MpUserService.class)
    public MpUserService mpUserService() {
        return new MpUserServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(DataScopeService.class)
    public DataScopeService dataScopeService() {
        return new DataScopeDefaultServiceImpl();
    }
}
