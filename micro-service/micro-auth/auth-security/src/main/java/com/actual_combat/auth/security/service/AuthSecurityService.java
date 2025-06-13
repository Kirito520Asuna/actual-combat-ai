package com.actual_combat.auth.security.service;

import com.actual_combat.auth.security.auth.AuthSecurityInterceptor;
import com.actual_combat.base.core.abs.auth.AbsAuthFiler;
import com.actual_combat.base.core.abs.auth.AbsAuthInterceptor;
import com.actual_combat.base.core.abs.auth.service.AbsAuthService;

/**
 * @Author yan
 * @Date 2025/6/12 23:27:01
 * @Description
 */
public interface AuthSecurityService extends AbsAuthService {
    @Override
    default AbsAuthInterceptor getAuthInterceptor() {
        return new AuthSecurityInterceptor();
    }

    @Override
    default AbsAuthFiler getAuthFiler() {
        return AbsAuthService.super.getAuthFiler();
    }
}
