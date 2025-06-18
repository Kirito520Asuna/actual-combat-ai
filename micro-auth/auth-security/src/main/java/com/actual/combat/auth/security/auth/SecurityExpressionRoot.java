package com.actual.combat.auth.security.auth;

import cn.hutool.extra.spring.SpringUtil;
import com.actual.combat.auth.security.config.SecurityAnnotationConfig;
import com.actual.combat.auth.security.utils.AuthSecurityContextUtil;
import com.actual.combat.basic.core.abs.auth.core.AbsSecurityExpressionRoot;
import com.actual.combat.basic.exceptions.ErrorInfo;

import java.util.List;

/**
 * @Author yan
 * @Date 2025/6/13 22:26:33
 * @Description
 */
public class SecurityExpressionRoot implements AbsSecurityExpressionRoot {
    final String errorJson = ErrorInfo.builder().errorMessage("未开启权限认证").build()
            .addErrorMap(ErrorInfo.ErrorLanguage.US_EN, "No permission authentication is enabled")
            .toJson();

    public boolean enable() {
        try {
            boolean enable = SpringUtil.getBean(SecurityAnnotationConfig.class).isEnable();
            return enable;
        } catch (Exception e) {
            String errorJson = ErrorInfo.builder().errorMessage("不存在 SecurityAnnotationConfig Bean").build()
                    .addErrorMap(ErrorInfo.ErrorLanguage.US_EN, "Does not exist SecurityAnnotationConfig Bean")
                    .toJson();
            log().warn(errorJson);
            return true;
        }
    }

    /**
     * 自定义认证
     *
     * @param authority
     * @return
     */
    @Override
    public boolean hasAuthority(String authority) {
        if (!enable()) {
            log().warn(errorJson);
            return true;
        }
        return AbsSecurityExpressionRoot.super.hasAuthority(authority);
    }

    @Override
    public boolean hasRole(String role) {
        if (!enable()) {
            log().warn(errorJson);
            return true;
        }
        return AbsSecurityExpressionRoot.super.hasRole(role);
    }

    @Override
    public List<String> getAnyRoles() {
        return AuthSecurityContextUtil.getAnyRoles();
    }


}
