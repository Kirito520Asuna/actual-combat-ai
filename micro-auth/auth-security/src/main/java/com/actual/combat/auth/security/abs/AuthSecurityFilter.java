package com.actual.combat.auth.security.abs;

import cn.hutool.extra.spring.SpringUtil;
import com.actual.combat.basic.core.abs.auth.AbsAuthFilter;
import com.actual.combat.basic.core.config.jwt.JwtConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;

/**
 * @Author yan
 * @Date 2025/6/14 01:08:03
 * @Description
 */
public interface AuthSecurityFilter extends AbsAuthFilter, AbsAuthorizationSecurity {
    default String[] fetchProtectedPaths() {
        JwtConfig jwtConfig = new JwtConfig();
        try {
            jwtConfig = SpringUtil.getBean(JwtConfig.class);
        } catch (Exception e) {
            log().warn("class:{},error:{}",getAClassName(),e.getMessage());
        }
        String[] protectedPaths = jwtConfig.getJwtPath().split(",");
        return protectedPaths;
    }

    default AntPathMatcher fetchPathMatcher() {
        return new AntPathMatcher();
    }
    @Override
    default void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        executeLog();
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String requestPath = request.getServletPath();
        // 检查是否为受保护路径
        boolean isProtectedPath = false;
        for (String path : fetchProtectedPaths()) {
            if (fetchPathMatcher().match(path, requestPath)) {
                isProtectedPath = true;
                break;
            }
        }
        if (isProtectedPath) {
            checkToken(request, response);
        }
        //放行
        filterChain.doFilter(request, response);
    }
}
