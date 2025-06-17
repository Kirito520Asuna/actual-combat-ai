package com.actual.combat.basic.core.filter;

import com.actual.combat.basic.core.abs.api.AbsApiFiler;
import com.actual.combat.basic.core.pojo.http.CachedBodyHttpServletRequest;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * @Author yan
 * @Date 2025/6/12 16:33:45
 * @Description
 */
@Slf4j
public class ApiFilter extends OncePerRequestFilter implements AbsApiFiler {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 从包装器读取请求体
        CachedBodyHttpServletRequest cachedBodyHttpServletRequest = new CachedBodyHttpServletRequest(request);
        checkApi(request, cachedBodyHttpServletRequest);
        //放行
        filterChain.doFilter(cachedBodyHttpServletRequest, response);
    }
}
