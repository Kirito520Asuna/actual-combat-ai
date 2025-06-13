package com.actual_combat.auth.security.abs;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.actual_combat.auth.security.pojo.UserBase;
import com.actual_combat.auth.security.service.AbsUserDetailsService;
import com.actual_combat.auth.security.utils.SecurityContextUtil;
import com.actual_combat.base.core.abs.auth.core.AbsAuthorization;
import com.actual_combat.base.core.config.jwt.JwtConfig;
import com.actual_combat.base.core.pojo.auth.UserInfo;
import com.actual_combat.base.enums.ApiCode;
import com.actual_combat.base.exceptions.GlobalCustomException;
import com.actual_combat.base.utils.jwt.JwtUtils;
import com.actual_combat.base.utils.object.ObjectUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Map;

/**
 * @Author yan
 * @Date 2024/10/14 上午2:06:15
 * @Description
 */
public interface AbsAuthorizationSecurity extends AbsAuthorization {
    @Override
    default String getTokenBySubstring(String token) {
        return AbsAuthorization.super.getTokenBySubstring(token);
    }

    @Override
    default Map<String, String> pushTwoToken(String userId, String tokenName, String refreshTokenName, HttpServletResponse response) {
       return AbsAuthorization.super.pushTwoToken(userId, tokenName, refreshTokenName, response);
    }

    @Override
    default String getUserIdByRefreshToken(String secret, String tokenName, String refreshTokenName, HttpServletRequest request, HttpServletResponse response) {
        return AbsAuthorization.super.getUserIdByRefreshToken(secret, tokenName, refreshTokenName, request, response);
    }

    @Override
    default String getUserIdByToken(boolean enableTwoToken, String secret, String tokenName, String refreshTokenName, HttpServletRequest request, HttpServletResponse response) {
        return AbsAuthorization.super.getUserIdByToken(enableTwoToken, secret, tokenName, refreshTokenName, request, response);
    }

    /**
     * 通过用户ID生成认证信息
     *
     * @param userId
     * @return
     */
    default UsernamePasswordAuthenticationToken generateUsernamePasswordAuthenticationToken(String userId) {
        AbsUserDetailsService userService = SpringUtil.getBean(AbsUserDetailsService.class);
        UserBase user = userService.getLoginUser(userId);
        if (ObjectUtil.isEmpty(user)) {
            throw new GlobalCustomException(ApiCode.UNAUTHORIZED);
        }
        log().info("用户信息：{}", user);
        //获取权限信息 使用数据库查询的信息
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        //String password = user.getPassword();
        // 构建UsernamePasswordAuthenticationToken,这里密码为null，是因为提供了正确的JWT,实现自动登录
        UserInfo userInfo = user.getUser();
        if (ObjectUtil.isEmpty(userInfo)) {
            throw new GlobalCustomException(ApiCode.UNAUTHORIZED);
        }
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userInfo.getId(), null, authorities);
        return authenticationToken;
    }

    default boolean checkToken(HttpServletRequest request, HttpServletResponse response){
        JwtUtils bean = SpringUtil.getBean(JwtUtils.class);
        JwtConfig jwtConfig = SpringUtil.getBean(JwtConfig.class);
        log().info("jwtConfig=>{}", jwtConfig);
        String tokenName = ObjectUtils.defaultIfEmpty(jwtConfig.getTokenName(), JwtUtils.HEADER_AS_TOKEN);
        String userId = null;
        //获取token
        String token = request.getHeader(tokenName);
        if (StrUtil.isEmpty(token)) {
            token = request.getHeader(HttpHeaders.AUTHORIZATION);
        }
        if (StringUtils.hasText(token)) {
            //解析token
            String secret = bean.getSecret();
            boolean enableTwoToken = ObjectUtils.defaultIfEmpty(jwtConfig.getEnableTwoToken(), false);
            String refreshTokenName = ObjectUtils.defaultIfEmpty(jwtConfig.getRefreshTokenName(), JwtUtils.REFRESH_TOKEN_KEY);
            userId = getUserIdByToken(enableTwoToken, secret, tokenName, refreshTokenName, request, response);
        }

        if (ObjectUtil.isNotEmpty(userId)) {
            //存入SecurityContextHolder
            UsernamePasswordAuthenticationToken authenticationToken = generateUsernamePasswordAuthenticationToken(userId);
            SecurityContextUtil.getContext().setAuthentication(authenticationToken);
            log().info("Authentication=>{},userId=>{},anyRoles=>{};"
                    , SecurityContextUtil.getAuthentication()
                    , SecurityContextUtil.getUserId()
                    , SecurityContextUtil.getAnyRoles()
            );
        }
        return true;
    }


    default void checkTokenLogin(HttpServletRequest request, HttpServletResponse response) {
        String userIdNoThrow = SecurityContextUtil.getUserIdNoThrow();
        if (ObjectUtil.isEmpty(userIdNoThrow)) {
            checkToken(request, response);
            SecurityContextUtil.getUserId();
        }
    }
}
