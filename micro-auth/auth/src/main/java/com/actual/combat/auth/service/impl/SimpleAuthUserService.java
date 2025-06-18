package com.actual.combat.auth.service.impl;

import com.actual.combat.auth.service.AuthUserService;

/**
 * @Author yan
 * @Date 2025/6/14 01:33:36
 * @Description
 */
public class SimpleAuthUserService implements AuthUserService {
    @Override
    public String getUserId() {
        throw new UnsupportedOperationException("SimpleAuthUserService not support getUserId");
    }
}
