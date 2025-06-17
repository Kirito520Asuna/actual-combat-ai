package com.actual.combat.basic.core.abs.auth.config;

/**
 * @Author yan
 * @Date 2025/3/10 17:59:35
 * @Description
 */
public interface AbsAuthSecurityConfig extends AbsAuthorizationConfig {
    @Override
    default void init() {
        log().debug("[AuthSecurity]-[init]::[{}]",getAClassName());
    }
}
