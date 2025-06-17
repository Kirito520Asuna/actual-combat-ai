package com.actual.combat.basic.core.abs.auth.config;

/**
 * @Author yan
 * @Date 2025/3/10 17:58:56
 * @Description
 */
public interface AbsAuthShiroConfig extends AbsAuthorizationConfig {
    @Override
    default void init() {
        log().debug("[AuthShiro]-[init]::[{}]",getAClassName());
    }
}
