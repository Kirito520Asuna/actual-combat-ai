package com.actual_combat.auth.shiro.auth;

import com.actual_combat.auth.shiro.abs.AbsAuthorizationShiro;
import com.actual_combat.base.core.abs.auth.AbsAuthInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @Author yan
 * @Date 2025/6/14 00:27:48
 * @Description
 */
public class AuthShiroInterceptor implements AbsAuthInterceptor, AbsAuthorizationShiro {
    @Override
    public boolean checkToken(HttpServletRequest request, HttpServletResponse response) {
        return AbsAuthorizationShiro.super.checkToken(request, response);
    }
}
