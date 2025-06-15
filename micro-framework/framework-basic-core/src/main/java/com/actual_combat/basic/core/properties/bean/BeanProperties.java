package com.actual_combat.basic.core.properties.bean;


import com.actual_combat.aop.abs.bean.AbsBean;
import jakarta.annotation.PostConstruct;

/**
 * @Author yan
 * @Date 2025/3/10 19:52:45
 * @Description
 */
public interface BeanProperties extends AbsBean {
    @Override
    @PostConstruct
    default void init() {
        log().debug("[init]-[Properties]-[Config]::[{}]",getAClassName());
    }
}
