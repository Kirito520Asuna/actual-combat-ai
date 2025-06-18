package com.actual.combat.auth.shiro.abs;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.actual.combat.basic.core.abs.auth.AbsAuthFilter;
import com.actual.combat.basic.core.config.jwt.JwtConfig;
import com.actual.combat.basic.utils.jwt.JwtUtils;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.springframework.util.StringUtils;

/**
 * @Author yan
 * @Date 2025/6/14 01:08:03
 * @Description
 */
public interface AuthShiroFilter extends AbsAuthFilter, AbsAuthorizationShiro {

    //生成自定义token
    default AuthenticationToken createToken(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        AuthenticationToken authenticationToken = null;
        try {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            // 从包装器读取请求体
            JwtUtils bean = SpringUtil.getBean(JwtUtils.class);
            JwtConfig jwtConfig = SpringUtil.getBean(JwtConfig.class);
            log().debug("jwtConfig=>{}", jwtConfig);
            String tokenName = ObjectUtil.isEmpty(jwtConfig.getTokenName()) ? JwtUtils.HEADER_AS_TOKEN : jwtConfig.getTokenName();
            String userId = null;
            //获取token
            String token = request.getHeader(tokenName);
            if (StringUtils.hasText(token)) {
                //解析token
                String secret = bean.getSecret();
                boolean enableTwoToken = ObjectUtil.isNotEmpty(jwtConfig.getEnableTwoToken()) && jwtConfig.getEnableTwoToken();
                String refreshTokenName = ObjectUtil.isEmpty(jwtConfig.getRefreshTokenName()) ? JwtUtils.REFRESH_TOKEN_KEY : jwtConfig.getRefreshTokenName();
                userId = getUserIdByToken(enableTwoToken, secret, tokenName, refreshTokenName, request, response);
            }

            if (ObjectUtil.isNotEmpty(userId)) {
                UsernamePasswordToken usernamePasswordToken = generateUsernamePasswordToken(userId);
                authenticationToken = usernamePasswordToken;
            }
        } finally {
            return authenticationToken;
        }
    }
    // 判断是否允许访问
    default boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        return executeLogin(request, response);
    }

    // 尝试登录
    default boolean executeLogin(ServletRequest servletRequest, ServletResponse servletResponse) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        return checkToken(request, response);
    }
}
