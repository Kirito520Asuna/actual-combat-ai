package com.actual.combat.auth.service.impl.mp;

import cn.hutool.extra.spring.SpringUtil;
import com.actual_combat.aop.abs.bean.AbsBean;
import com.actual.combat.mp.abs.service.MpUserService;

/**
 * @Author yan
 * @Date 2025/6/14 01:47:10
 * @Description
 */
public class AuthUserMpService implements MpUserService, AbsBean {
    @Override
    public String getUserId() {
        return SpringUtil.getBean(AuthUserMpService.class).getUserId();
    }
}
