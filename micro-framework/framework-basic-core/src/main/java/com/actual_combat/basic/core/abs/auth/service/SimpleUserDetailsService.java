package com.actual_combat.basic.core.abs.auth.service;


import com.actual_combat.basic.exceptions.ErrorInfo;
import com.actual_combat.basic.exceptions.GlobalConfigException;

/**
 * @Author yan
 * @Date 2025/6/12 23:00:47
 * @Description
 */
public class SimpleUserDetailsService implements AbstractUserDetailsService {

    static final String errorJson = ErrorInfo.builder()
            .errorMessage("未配置用户信息加载器")
            .build()
            .addErrorMap(ErrorInfo.ErrorLanguage.US_EN, "User information loader not configured")
            .toJson();

    @Override
    public Object loadUserByUsername(String username) {
        throw new GlobalConfigException(errorJson);
    }

    @Override
    public Object getLoginUser(String userId) {
        throw new GlobalConfigException(errorJson);
    }

    @Override
    public void logout(String id) {
        throw new GlobalConfigException(errorJson);
    }
}
