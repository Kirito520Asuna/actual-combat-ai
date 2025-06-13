package com.actual_combat.auth.security.abs;

import cn.hutool.extra.spring.SpringUtil;
import com.actual_combat.auth.security.auth.JwtFilter;
import com.actual_combat.base.core.abs.auth.config.AbsAuthorizationConfig;
import com.actual_combat.base.exceptions.ErrorInfo;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @Author yan
 * @Date 2024/10/28 下午12:44:53
 * @Description
 */
public interface AbsSecurityConfig extends AbsAuthorizationConfig {
    /**
     * @param http
     */
    default void addFilterBeforeList(HttpSecurity http) {
        JwtFilter jwtFilter = SpringUtil.getBean(JwtFilter.class);
        if (jwtFilter == null) {
            String errorJson = ErrorInfo.builder()
                    .errorLanguage(ErrorInfo.ErrorLanguage.US_EN)
                    .errorMessage("JwtFilter is null, please make sure it's a Spring Bean")
                    .build()
                    .addErrorMap(ErrorInfo.ErrorLanguage.ZH_CN, "JwtFilter为空，请确保它是Spring Bean")
                    .toJson();
            throw new IllegalStateException(errorJson);
        }
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
