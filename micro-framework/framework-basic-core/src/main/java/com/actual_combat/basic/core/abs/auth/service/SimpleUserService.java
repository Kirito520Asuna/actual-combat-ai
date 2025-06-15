package com.actual_combat.basic.core.abs.auth.service;

import com.actual_combat.basic.core.pojo.auth.UserInfo;
import com.actual_combat.basic.exceptions.ErrorInfo;
import com.actual_combat.basic.exceptions.GlobalConfigException;

/**
 * @Author yan
 * @Date 2025/6/12 23:39:14
 * @Description
 */
public class SimpleUserService implements AbstractUserService {

    static final String errorJson = ErrorInfo.builder()
            .errorMessage("未配置用户服务")
            .build()
            .addErrorMap(ErrorInfo.ErrorLanguage.US_EN, "No user service configured")
            .toJson();

    @Override
    public UserInfo getOneByUserName(String userName) {
        throw new GlobalConfigException(errorJson);
    }
}
