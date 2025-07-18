package com.actual.combat.auth.service.impl;

import com.actual.combat.auth.security.utils.AuthSecurityContextUtil;
import com.actual.combat.auth.service.AuthUserService;

/**
 * @Author yan
 * @Date 2025/6/14 01:35:41
 * @Description
 */
public class SecurityAuthUserService implements AuthUserService {
    @Override
    public String getUserId() {
        return AuthSecurityContextUtil.getUserIdNoThrow();
    }
}
