package com.actual.combat.auth.shiro.service;

import com.actual.combat.auth.shiro.auth.AuthShiroInterceptor;
import com.actual.combat.auth.shiro.auth.JwtAuthShiroFilter;
import com.actual.combat.auth.shiro.utils.EncodePasswordUtils;
import com.actual.combat.basic.core.abs.auth.AbsAuthFilter;
import com.actual.combat.basic.core.abs.auth.AbsAuthInterceptor;
import com.actual.combat.basic.core.abs.auth.service.AbsAuthService;

/**
 * @Author yan
 * @Date 2025/6/14 00:35:18
 * @Description
 */
public interface AuthShiroService extends AbsAuthService {
    @Override
    default AbsAuthInterceptor getAuthInterceptor() {
        return new AuthShiroInterceptor();
    }

    @Override
    default AbsAuthFilter getAuthFiler() {
        return new JwtAuthShiroFilter();
    }

    @Override
    default boolean matchPassword(String plainPassword, String encodedPassword) {
        return EncodePasswordUtils.matchPassword(plainPassword, encodedPassword);
    }

    @Override
    default String encodePassword(String plainPassword) {
        return EncodePasswordUtils.encodePassword(plainPassword);
    }
}
