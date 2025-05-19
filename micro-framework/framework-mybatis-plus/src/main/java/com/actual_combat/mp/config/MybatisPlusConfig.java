package com.actual_combat.mp.config;

import com.actual_combat.mp.abs.config.AbsMybatisPlusConfig;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 */
@Configuration
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
     * @author
     */
    @Bean @Override
    public PaginationInnerInterceptor paginationInnerInterceptor() {
        return AbsMybatisPlusConfig.super.paginationInnerInterceptor();
    }

    /**
     * 3.4.0之后提供的拦截器的配置方式
     *
     * @return
     */
    @Bean @Override
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        return AbsMybatisPlusConfig.super.mybatisPlusInterceptor();
    }

    /**
     * 防止全表更新与删除插件
     * @return
     */
    @Bean
    @Override
    public MybatisPlusInterceptor blockAttackInnerInterceptor() {
        return AbsMybatisPlusConfig.super.blockAttackInnerInterceptor();
    }

    /**
     * 乐观锁支持
     * @return
     */
    @Bean @Override
    public MybatisPlusInterceptor optimisticLockerInterceptor() {
        return AbsMybatisPlusConfig.super.optimisticLockerInterceptor();
    }
}
