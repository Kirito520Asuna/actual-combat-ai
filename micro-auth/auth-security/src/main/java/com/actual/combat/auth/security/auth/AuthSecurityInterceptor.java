package com.actual.combat.auth.security.auth;

import com.actual.combat.auth.security.abs.AbsAuthorizationSecurity;
import com.actual.combat.basic.core.abs.auth.AbsAuthInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @Author yan
 * @Date 2025/6/13 19:58:19
 * @Description
 */
public class AuthSecurityInterceptor implements AbsAuthInterceptor, AbsAuthorizationSecurity {
    @Override
    public void checkLogin(HttpServletRequest request, HttpServletResponse response) {
        AbsAuthorizationSecurity.super.checkTokenLogin(request, response);
    }
}
