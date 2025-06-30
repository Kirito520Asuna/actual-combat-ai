package com.minimalism.mybatis.abs.config;

import org.mybatis.spring.annotation.MapperScan;

/**
 * @Author yan
 * @Date 2025/5/19 14:57:20
 * @Description
 */

@MapperScan(basePackages = {"com.minimalism.**.dao","com.minimalism.**.**.dao","com.minimalism.**.mapper","com.minimalism.**.**.mapper"})
public interface AbsMybatisConfig {
    /**
     * 3.4.0之前的版本用这个
     *
     * @return
     */

    default Object paginationInterceptor() {
//        3.4.0之前的版本重写这个方法实列
//        public PaginationInterceptor paginationInterceptor(){
//            return  new PaginationInterceptor();
//        }
        return new Object();
    }

}
