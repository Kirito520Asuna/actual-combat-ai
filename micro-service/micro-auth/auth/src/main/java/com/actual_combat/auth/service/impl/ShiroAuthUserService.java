package com.actual_combat.auth.service.impl;

import com.actual_combat.auth.service.AuthUserService;
import com.actual_combat.auth.shiro.utils.AuthShiroContextUtil;

/**
 * @Author yan
 * @Date 2025/6/14 01:35:23
 * @Description
 */
public class ShiroAuthUserService implements AuthUserService {
    @Override
    public String getUserId() {
        return AuthShiroContextUtil.getUserIdNoThrow();
    }
}
