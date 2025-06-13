package com.actual_combat.auth.security.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.actual_combat.auth.security.pojo.UserBase;
import com.actual_combat.base.core.abs.auth.service.AbstractUserDetailsService;
import com.actual_combat.base.core.abs.auth.service.AbstractUserService;
import com.actual_combat.base.core.constant.Redis;
import com.actual_combat.base.core.pojo.auth.UserInfo;
import com.actual_combat.redis.aop.redis.RedisCacheEvict;
import com.actual_combat.redis.aop.redis.RedisCacheable;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

/**
 * @Author yan
 * @Date 2025/6/12 22:57:52
 * @Description
 */
public interface AbsUserDetailsService extends UserDetailsService, AbstractUserDetailsService<UserBase> {

    @Override
    default UserBase loadUserByUsername(String username) throws UsernameNotFoundException {
        AbstractUserService userService = SpringUtil.getBean(AbstractUserService.class);
        UserInfo userInfo = userService.getOneByUserName(username);

        if (userInfo == null) {
            throw new UsernameNotFoundException("用户名不存在");
        }
        //roles权限 --可扩展权限系统
        List<String> roles = userService.getRolesById(Long.parseLong(userInfo.getId()));
        if (CollUtil.isEmpty(roles)) {
            roles = null;
        }
        UserBase user = new UserBase(userInfo, roles);
        return user;
    }

    /**
     * 获取登录用户获取 用户信息 需重写
     *
     * @param userId
     * @return
     */
    @RedisCacheable(cacheName = Redis.login_user, requestAsName = "rq",
            condition = "#rq.userId!=null", throwException = false,
            key = "#rq.userId", classType = UserBase.class)
    default UserBase getLoginUser(String userId) {
        return null;
    }


    /**
     * 退出登录 可重写
     *
     * @param id
     */
    @RedisCacheEvict(cacheName = Redis.login_user, requestAsName = "rq", condition = "#rq.id!=null", key = "#rq.id")
    default void logout(String id) {
    }

}
