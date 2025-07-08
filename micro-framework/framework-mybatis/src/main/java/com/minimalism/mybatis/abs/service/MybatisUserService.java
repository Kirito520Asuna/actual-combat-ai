package com.minimalism.mybatis.abs.service;

import com.actual.combat.database.core.service.UserCoreService;

/**
 * @Author yan
 * @Date 2025/3/6 23:23:53
 * @Description
 */
public interface MybatisUserService extends UserCoreService {
    @Override
    default String getUserId() {
        return null;
    }
}
