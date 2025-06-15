package com.actual_combat.basic.core.abs.auth.service;

import com.actual_combat.basic.exceptions.ErrorInfo;
import com.actual_combat.basic.exceptions.GlobalConfigException;

/**
 * @Author yan
 * @Date 2025/6/12 23:37:06
 * @Description
 */
public class SimpleLoginService implements AbstractLoginService {

    static final String errorJson = ErrorInfo.builder()
            .errorMessage("未配置登录服务")
            .build()
            .addErrorMap(ErrorInfo.ErrorLanguage.US_EN, "No login service configured")
            .toJson();

    @Override
    public String getCurrentUserId() {
        throw new GlobalConfigException(errorJson);
    }

    @Override
    public void logout(String id) {
        throw new GlobalConfigException(errorJson);
    }
}
