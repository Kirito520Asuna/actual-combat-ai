package com.actual_combat.basic.core.abs.auth.service;

/**
 * @Author yan
 * @Date 2025/6/13 17:53:44
 * @Description
 */
public interface AbstractUserDetailsService <T> {

    /**
     * 根据用户名获取用户信息
     * @param username
     * @return
     */
    T loadUserByUsername(String username);

    /**
     * 获取当前登录用户
     * @return
     */
    T getLoginUser(String userId);

    /**
     * 登出/注销
     */
    void logout(String id);
}
