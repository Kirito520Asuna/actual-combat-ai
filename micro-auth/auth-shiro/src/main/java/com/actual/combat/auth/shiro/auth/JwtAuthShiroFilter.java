package com.actual.combat.auth.shiro.auth;

import com.actual.combat.auth.shiro.abs.AuthShiroFilter;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;


/**
 * @Author yan
 * @Date 2025/6/14 00:27:07
 * @Description
 */
public class JwtAuthShiroFilter extends AuthenticatingFilter implements AuthShiroFilter {

    //生成自定义token
    @Override
    public AuthenticationToken createToken(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        return AuthShiroFilter.super.createToken(servletRequest, servletResponse);
    }

    // 判断是否允许访问
    @Override
    public boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        return AuthShiroFilter.super.onAccessDenied(request, response);
    }

    // 尝试登录
    @Override
    public boolean executeLogin(ServletRequest servletRequest, ServletResponse servletResponse) {
        return AuthShiroFilter.super.executeLogin(servletRequest, servletResponse);
    }
}
