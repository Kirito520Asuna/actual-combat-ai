package com.actual.combat.mp.config;

import com.actual.combat.database.core.service.UserCoreService;
import com.actual.combat.mp.abs.config.AbsMybatisPlusConfig;
import com.actual.combat.mp.abs.handler.AbsEntityHandler;
import com.actual.combat.mp.abs.service.DataScopeService;
import com.actual.combat.mp.abs.service.MpUserService;
import com.actual.combat.mp.abs.service.impl.DataScopeDefaultServiceImpl;
import com.actual.combat.mp.abs.service.impl.MpUserServiceImpl;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 */
@Configuration
@ConditionalOnMissingBean(AbsMybatisPlusConfig.class)
public class MybatisPlusConfig implements AbsMybatisPlusConfig {
    /**
     * 3.4.0之前的版本用这个
     * @return
     */
   /* @Bean
    public PaginationInterceptor paginationInterceptor(){
        return  new PaginationInterceptor();
    }*/

    /**
     * 分页插件 3.5.X
     *
     * @author
     */
    @Bean
    @Override
    public PaginationInnerInterceptor paginationInnerInterceptor() {
        return AbsMybatisPlusConfig.super.paginationInnerInterceptor();
    }

    /**
     * 3.4.0之后提供的拦截器的配置方式
     *
     * @return
     */
    @Bean
    @Override
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        return AbsMybatisPlusConfig.super.mybatisPlusInterceptor();
    }

    /**
     * 防止全表更新与删除插件
     *
     * @return
     */
    @Bean
    @Override
    public MybatisPlusInterceptor blockAttackInnerInterceptor() {
        return AbsMybatisPlusConfig.super.blockAttackInnerInterceptor();
    }

    /**
     * 乐观锁支持
     *
     * @return
     */
    @Bean
    @Override
    public MybatisPlusInterceptor optimisticLockerInterceptor() {
        return AbsMybatisPlusConfig.super.optimisticLockerInterceptor();
    }

    @Bean
    @ConditionalOnBean(AbsEntityHandler.class)
    @ConditionalOnMissingBean(UserCoreService.class)
    public UserCoreService mpUserService() {
        return new MpUserServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(DataScopeService.class)
    public DataScopeService dataScopeService() {
        return new DataScopeDefaultServiceImpl();
    }
}
