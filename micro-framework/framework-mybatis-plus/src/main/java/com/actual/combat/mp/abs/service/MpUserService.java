package com.actual.combat.mp.abs.service;

import com.actual.combat.database.core.service.UserCoreService;

/**
 * @Author yan
 * @Date 2025/3/6 23:23:53
 * @Description
 */
public interface MpUserService extends UserCoreService {
    @Override
    default String getUserId() {
        return null;
    }
}
