package com.actual_combat.basic.core.abs.auth;

import com.actual_combat.basic.core.abs.handler.AbsInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;


/**
 * @Author yan
 * @Date 2024/10/27 下午10:26:58
 * @Description
 */
public interface AbsAuthInterceptor extends AbsInterceptor {
    @SneakyThrows
    @Override
    default boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        checkLogin(request, response);
        return AbsInterceptor.super.preHandle(request, response, handler);
    }

    /**
     * 检查登陆
     */
    default void checkLogin(HttpServletRequest request, HttpServletResponse response) {
        log().warn("[AuthInterceptor]-[checkLogin]::[{}]", "未实现");
    }
}
