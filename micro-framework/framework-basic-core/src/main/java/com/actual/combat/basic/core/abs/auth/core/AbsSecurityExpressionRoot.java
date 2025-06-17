package com.actual.combat.basic.core.abs.auth.core;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.actual_combat.aop.abs.bean.AbsBean;
import com.actual.combat.basic.core.config.auth.AuthorizationConfig;
import com.actual.combat.basic.core.constant.Roles;
import com.actual_combat.basic.utils.object.ObjectUtils;
import jakarta.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @Author yan
 * @Date 2024/9/27 上午2:12:25
 * @Description (归于模板)
 */
public interface AbsSecurityExpressionRoot extends AbsBean {
    @Override
    @PostConstruct
    default void init() {
        log().debug("[init]-[SecurityExpressionRoot]::[{}]", getAClassName());
    }

    /**
     * @param key
     * @return
     */
    default boolean isAdmin(String key) {
        AuthorizationConfig config = SpringUtil.getBean(AuthorizationConfig.class);
        return config.isAdmin(key);
    }

    default boolean isAdmin(List<String> keys) {
        keys = ObjectUtils.defaultIfEmpty(keys, new ArrayList<>());
        boolean isAdmin = false;

        for (String key : keys) {
            isAdmin = isAdmin || isAdmin(key);
            if (isAdmin) {
                break;
            }
        }
        return isAdmin;
    }

    /**
     * 自定义认证
     *
     * @param authority
     * @return
     */
    default boolean hasAuthority(String authority) {
        boolean hasAuthority = false;
        try {
            hasAuthority = isAdmin(getRoles()) || getAuthorityList().contains(authority);
        } catch (Exception e) {
            log().error("class:{},error:{} ", getAClassName(), e.getMessage());
        }
        return hasAuthority;
    }

    default boolean hasRole(String role) {
        boolean hasRole = false;

        if (!role.startsWith(Roles.roles)) {
            role = new StringBuffer(Roles.roles).append(role).toString();
        }

        try {
            hasRole = isAdmin(role) || getRoles().contains(role);
        } catch (Exception e) {
            log().error("class:{},error:{} ", getAClassName(), e.getMessage());
        }
        return hasRole;
    }

    default List<String> getAuthorityList() {
        return getAnyRoles().stream()
                .filter(roleOne -> roleOne.startsWith(Roles.perms))
                .map(o -> o.replace(Roles.perms, ""))
                .collect(Collectors.toList());
    }

    default List<String> getRoles() {
        return getAnyRoles().stream()
                .filter(roleOne -> roleOne.startsWith(Roles.roles))
                .map(o -> o.replace(Roles.roles, ""))
                .collect(Collectors.toList());
    }

    default List<String> getAnyRoles() {
        return CollUtil.newArrayList();
    }
}
