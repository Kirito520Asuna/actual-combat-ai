package com.actual_combat.basic.core.filter;

import com.actual_combat.basic.core.abs.auth.AbsAuthFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * @Author yan
 * @Date 2025/6/12 17:05:24
 * @Description
 */
public class SimpleAuthFilter extends OncePerRequestFilter implements AbsAuthFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        checkToken(request, response);
        filterChain.doFilter(request, response);
    }
}
