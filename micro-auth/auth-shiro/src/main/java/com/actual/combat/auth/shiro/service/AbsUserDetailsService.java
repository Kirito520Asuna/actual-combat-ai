package com.actual.combat.auth.shiro.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.actual.combat.auth.shiro.pojo.UserBase;
import com.actual.combat.auth.shiro.utils.AuthShiroContextUtil;
import com.actual.combat.auth.shiro.utils.EncodePasswordUtils;
import com.actual.combat.basic.core.abs.auth.service.AbstractUserDetailsService;
import com.actual.combat.basic.core.abs.auth.service.AbstractUserService;
import com.actual.combat.basic.core.constant.Redis;
import com.actual.combat.basic.core.pojo.auth.UserInfo;
import com.actual.combat.basic.exceptions.GlobalCustomException;
import com.actual.combat.redis.aop.redis.RedisCacheEvict;
import com.actual.combat.redis.aop.redis.RedisCachePut;
import com.actual.combat.redis.aop.redis.RedisCacheable;
import lombok.SneakyThrows;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

/**
 * @Author yan
 * @Date 2024/10/12 上午2:19:29
 * @Description
 */
public interface AbsUserDetailsService extends AbstractUserDetailsService<UserBase> {
    default UserBase loadUserByUsername(String username) {
        AbstractUserService userService = SpringUtil.getBean(AbstractUserService.class);
        UserInfo userInfo = userService.getOneByUserName(username);

        if (userInfo == null) {
            throw new GlobalCustomException("用户名不存在");
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
    //@Cacheable(key = "'" + Redis.login_user + ":' + #userId")
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
    //@CacheEvict(key = "'" + Redis.login_user + ":' + #id")
    @RedisCacheEvict(cacheName = Redis.login_user, requestAsName = "rq", condition = "#rq.id!=null", key = "#rq.id")
    default void logout(String id) {
        AuthShiroContextUtil.logout();
    }

    default UserBase loginAndSetUsernameUserIdMap(UserBase user) {
        UserBase login = SpringUtil.getBean(AbsUserDetailsService.class).login(user);
        RedisTemplate redisTemplate = SpringUtil.getBean(RedisTemplate.class);
        redisTemplate.opsForHash().put(Redis.login_username_userId_map, login.getUsername(), login.getUser().getId());
        return login;
    }

    @RedisCachePut(cacheName = Redis.login_user, responseAsName = "rp", condition = "#rp.user.id!=null", key = "#rq.user.id")
    default UserBase login(UserBase user) {
        String username = user.getUsername();
        String password = user.getPassword();

        UserBase userByUsername = loadUserByUsername(username);
        String userId = userByUsername.getUser().getId();
        String userByUsernamePassword = userByUsername.getPassword();
        if (!EncodePasswordUtils.matchPassword(password, userByUsernamePassword)) {
            throw new GlobalCustomException("密码错误");
        }
        login(userId, username, userByUsernamePassword);
        return userByUsername;
    }

    @SneakyThrows
    default void login(String userId, String username, String password) {
        AuthShiroContextUtil.login(userId, username, password);
    }
}
