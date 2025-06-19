package com.actual.combat.basic.core.abs.api;


import com.actual.combat.basic.core.abs.api.core.AbsApiSign;
import com.actual.combat.basic.core.abs.filter.AbsCommonFilter;
import com.actual.combat.basic.core.abs.order.FilterOrderConstants;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.*;

import java.io.IOException;

/**
 * @Author yan
 * @Date 2025/3/10 3:31:21
 * @Description
 */
public interface AbsApiFiler extends AbsApiSign, AbsCommonFilter {
    @Override
    default int getOrder() {
        return FilterOrderConstants.ApiOrder;
    }

    @Override
    @PostConstruct
    default void init() {
       log().debug("[Bean]-[ApiFiler]-[init]::[{}]",getAClassName());
    }

    default void executeLog() {
        log().debug("class:{}, execute",getAClassName());
    }

    @Override
    default void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        executeLog();
        AbsCommonFilter.super.doFilter(servletRequest, servletResponse, filterChain);
    }
}
