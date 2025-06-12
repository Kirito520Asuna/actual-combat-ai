package com.actual_combat.base.core.interceptor;

import com.actual_combat.base.core.abs.auth.AbsAuthInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @Author yan
 * @Date 2025/6/12 16:56:50
 * @Description
 */
public class SimpleAuthInterceptor implements AbsAuthInterceptor {
    @Override
    public void checkLogin(HttpServletRequest request, HttpServletResponse response) {
        log().warn("未实现授权登陆");
    }
}
