package com.actual_combat.basic.core.abs.api;

import com.actual_combat.basic.core.abs.api.core.AbsApiSign;
import com.actual_combat.basic.core.abs.handler.AbsInterceptor;
import com.actual_combat.basic.core.pojo.http.CachedBodyHttpServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;


/**
 * @Author yan
 * @Date 2024/10/27 下午10:32:32
 * @Description
 */
public interface AbsApiInterceptor extends AbsInterceptor, AbsApiSign {
    @SneakyThrows
    @Override
    default boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        CachedBodyHttpServletRequest cachedBodyHttpServletRequest = new CachedBodyHttpServletRequest(request);
        checkApi(request, cachedBodyHttpServletRequest);
        return AbsInterceptor.super.preHandle(request, response, handler);
    }
}
