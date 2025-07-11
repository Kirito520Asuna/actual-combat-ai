package com.actual.combat.basic.core.abs.filter;

import com.actual.combat.aop.abs.bean.AbsBean;
import com.actual.combat.basic.core.abs.order.FilterOrderConstants;
import jakarta.servlet.*;
import org.springframework.core.Ordered;

import java.io.IOException;

/**
 * @Author yan
 * @Date 2025/6/11 19:39:13
 * @Description
 */
public interface AbsCommonFilter extends Ordered, Filter, AbsBean {
    @Override
    default int getOrder() {
        return FilterOrderConstants.BaseOrder;
    }

    @Override
    default void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
        AbsBean.super.init();
    }

    @Override
    default void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    default void destroy() {
        Filter.super.destroy();
        AbsBean.super.destroy();
    }
}
