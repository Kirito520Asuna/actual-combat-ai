package com.actual_combat.auth.security.auth;

import com.actual_combat.auth.security.abs.AuthSecurityFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * @Author yan
 * @Date 2025/6/12 23:08:19
 * @Description
 */
public class JwtAuthSecurityFilter extends OncePerRequestFilter implements AuthSecurityFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        checkToken(request, response);
        //放行
        filterChain.doFilter(request, response);
    }


}
