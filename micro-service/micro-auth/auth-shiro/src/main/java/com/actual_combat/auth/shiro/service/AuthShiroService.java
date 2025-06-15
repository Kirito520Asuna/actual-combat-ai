package com.actual_combat.auth.shiro.service;

import com.actual_combat.auth.shiro.auth.AuthShiroInterceptor;
import com.actual_combat.auth.shiro.auth.JwtAuthShiroFilter;
import com.actual_combat.basic.core.abs.auth.AbsAuthFilter;
import com.actual_combat.basic.core.abs.auth.AbsAuthInterceptor;
import com.actual_combat.basic.core.abs.auth.service.AbsAuthService;

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
}
