package com.actual.combat.auth.service.impl.database;

import cn.hutool.extra.spring.SpringUtil;
import com.actual.combat.aop.abs.bean.AbsBean;
import com.actual.combat.auth.service.AuthUserService;
import com.actual.combat.database.core.service.UserCoreService;

/**
 * @Author yan
 * @Date 2025/6/14 01:47:10
 * @Description
 */
public class AuthUserCoreService implements UserCoreService, AbsBean {
    @Override
    public String getUserId() {
        return SpringUtil.getBean(AuthUserService.class).getUserId();
    }
}
