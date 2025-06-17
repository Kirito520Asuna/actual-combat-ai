package com.actual_combat.auth.shiro.abs;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import com.actual_combat.aop.utils.response.ResponseUtils;
import com.actual_combat.auth.shiro.pojo.UserBase;
import com.actual_combat.auth.shiro.utils.AuthShiroContextUtil;
import com.actual_combat.basic.core.abs.auth.core.AbsAuthorization;
import com.actual_combat.basic.core.abs.auth.service.AbstractUserDetailsService;
import com.actual_combat.basic.core.config.jwt.JwtConfig;
import com.actual_combat.basic.enums.ApiCode;
import com.actual_combat.basic.exceptions.GlobalCustomException;
import com.actual_combat.basic.result.Result;
import com.actual_combat.basic.utils.jwt.JwtUtils;
import com.actual_combat.basic.utils.object.ObjectUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * @Author yan
 * @Date 2024/10/20 上午5:01:59
 * @Description
 */
public interface AbsAuthorizationShiro extends AbsAuthorization {

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
     * 获取用户信息
     *
     * @param userId
     * @return
     */
    default UsernamePasswordToken generateUsernamePasswordToken(String userId) {
        AbstractUserDetailsService userService = SpringUtil.getBean(AbstractUserDetailsService.class);
        UserBase user = (UserBase) userService.getLoginUser(userId);
        if (ObjectUtil.isEmpty(user)) {
            throw new GlobalCustomException(ApiCode.UNAUTHORIZED);
        }
        log().debug("用户信息：{}", user);
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(user.getUsername(), user.getPassword());
        return usernamePasswordToken;
    }

    @SneakyThrows
    default boolean checkToken(HttpServletRequest request, HttpServletResponse response) {

        JwtUtils bean = SpringUtil.getBean(JwtUtils.class);
        JwtConfig jwtConfig = null;
        try {
            jwtConfig = SpringUtil.getBean(JwtConfig.class);
        } catch (Exception e) {
            log().warn("未找到JwtConfig配置");
        }
        log().debug("jwtConfig=>{}", jwtConfig);
        String tokenName = jwtConfig == null ? null : jwtConfig.getTokenName();
        tokenName = ObjectUtils.defaultIfEmpty(tokenName, JwtUtils.HEADER_AS_TOKEN);
        String userId = null;
        //获取token
        String token = request.getHeader(tokenName);

        if (StrUtil.isEmpty(token)) {
            token = request.getHeader(HttpHeaders.AUTHORIZATION);
        }
        boolean executeLogin = false;
        try {
            if (StringUtils.hasText(token)) {
                //解析token
                String secret = bean.getSecret();
                boolean enableTwoToken = jwtConfig == null ? false : jwtConfig.getEnableTwoToken();
                String refreshTokenName = jwtConfig == null ? null : jwtConfig.getRefreshTokenName();

                enableTwoToken = ObjectUtils.defaultIfEmpty(enableTwoToken, false);
                refreshTokenName = ObjectUtil.defaultIfEmpty(refreshTokenName, JwtUtils.REFRESH_TOKEN_KEY);
                userId = getUserIdByToken(enableTwoToken, secret, tokenName, refreshTokenName, request, response);
            }

            if (ObjectUtil.isNotEmpty(userId)) {
                //存入SecurityContextHolder
                UsernamePasswordToken usernamePasswordToken = generateUsernamePasswordToken(userId);
                AuthShiroContextUtil.login(userId, usernamePasswordToken);
                log().debug("Subject:{},Principal:{},UserId:{}"
                        , AuthShiroContextUtil.getSubject()
                        , AuthShiroContextUtil.getPrincipal()
                        , AuthShiroContextUtil.getUserId()
                );
                executeLogin = true;
            } else {
                boolean isOpenFilter = ObjectUtils.defaultIfEmpty(jwtConfig.getOpenFilter(), true);
                if (isOpenFilter) {
                    String contentType = "application/json;charset=UTF-8";
                    Result result = Result.result(ApiCode.UNAUTHORIZED);
                    String json = JSONUtil.toJsonStr(result, JSONConfig.create().setIgnoreNullValue(false));
                    ResponseUtils.responsePush(response, contentType, json);
                    log().error("未授权");
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return executeLogin;
    }

    default void checkTokenLogin(HttpServletRequest request, HttpServletResponse response) {
        String userIdNoThrow = AuthShiroContextUtil.getUserIdNoThrow();
        if (ObjectUtil.isEmpty(userIdNoThrow)) {
            checkToken(request, response);
            AuthShiroContextUtil.getUserId();
        }
    }
}
