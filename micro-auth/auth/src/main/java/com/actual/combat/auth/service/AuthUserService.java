package com.actual.combat.auth.service;

import cn.hutool.extra.spring.SpringUtil;
import com.actual_combat.aop.abs.bean.AbsBean;
import com.actual.combat.basic.core.abs.auth.service.AbstractUserService;
import com.actual.combat.basic.core.config.auth.AuthorizationConfig;
import com.actual.combat.basic.core.constant.Roles;
import com.actual.combat.basic.core.pojo.auth.User;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author yan
 * @Date 2025/6/14 01:28:51
 * @Description
 */
public interface AuthUserService extends AbsBean {
    @Override
    default void init() {
        log().debug("[Auth]-[init] {}", getAClassName());
    }

    default boolean isAdmin(String userId) {
        boolean isAdmin = false;
        try {
            User user = SpringUtil.getBean(AbstractUserService.class).getOneRedis(userId);
            List<String> roles = user.getRoles().stream().filter(o -> o.startsWith(Roles.roles)).collect(Collectors.toList());
            isAdmin = SpringUtil.getBean(AuthorizationConfig.class).isAdmin(roles);
        } catch (Exception e) {
            log().warn("isAdmin:{}", isAdmin);
            log().error("class:{},error:{}", getAClassName(), e.getMessage());
        }
        return isAdmin;
    }

    default boolean isAdmin() {
        String userId = getUserId();
        return isAdmin(userId);
    }

    String getUserId();
}
