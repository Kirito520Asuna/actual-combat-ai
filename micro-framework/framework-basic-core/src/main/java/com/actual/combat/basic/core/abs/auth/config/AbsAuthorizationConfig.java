package com.actual.combat.basic.core.abs.auth.config;

import com.actual_combat.aop.abs.bean.AbsBean;
import jakarta.annotation.PostConstruct;


/**
 * @Author yan
 * @Date 2025/3/10 4:04:43
 * @Description
 */
public interface AbsAuthorizationConfig extends AbsBean {
    @Override
    @PostConstruct
    default void init() {
        log().debug("[Auth]-[Config]-[init]::[{}]",getAClassName());
    }
}
