package com.actual.combat.auth.security.abs;

import com.actual.combat.basic.core.abs.auth.AbsAuthFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * @Author yan
 * @Date 2025/6/14 01:08:03
 * @Description
 */
public interface AuthSecurityFilter extends AbsAuthFilter, AbsAuthorizationSecurity {
    @Override
    default void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        checkToken(request, response);
        //放行
        filterChain.doFilter(request, response);
    }
}
