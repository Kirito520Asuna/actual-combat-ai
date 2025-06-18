package com.actual.combat.basic.core.abs.handler;

import cn.hutool.core.util.StrUtil;
import com.actual.combat.aop.abs.bean.AbsBean;
import com.actual.combat.aop.utils.thread.AopThreadMdcUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


/**
 * @Author yan
 * @Date 2024/5/14 0014 14:45
 * @Description
 */
public interface AbsInterceptor extends HandlerInterceptor, AbsBean {
    String TRACE_ID = AopThreadMdcUtil.TRACE_ID;

    default String fetchHttpTraceId() {
        return TRACE_ID;
    }

    @SneakyThrows
    @Override
    default boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        //可以考虑让客户端传入链路ID，但需保证一定的复杂度唯一性；如果没使用默认UUID自动生成
        String tid = StrUtil.isEmpty(request.getHeader(fetchHttpTraceId())) ? AopThreadMdcUtil.getTraceId() : request.getHeader(fetchHttpTraceId());
        AopThreadMdcUtil.setTraceId(tid);
        log().debug("preHandle by {}", getAClassName());
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    default void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) {
        AopThreadMdcUtil.removeTraceId();
        log().debug("afterCompletion by {}", getAClassName());
    }

    @Override
    default void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log().debug("postHandle by {}", getAClassName());
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }
}
