package com.actual.combat.basic.core.constant;

/**
 * @author yan
 * @date 2023/8/6 1:07
 */
public interface Redis {
    /**
     * 存储空间
     */
    String redis_cache = "redisCache";
    /**
     * 登录redis键值 key 前缀
     */
    String login_user = "login_user";
    /**
     * 登录用户名与userId的映射
     */
    String login_username_userId_map = "login_username_userId_map";
    /**
     * 不设置过期时间
     */
    String cache_manager = "cacheManager";
    /**
     * 默认设置过期时间
     */
    String default_cache_manager = "cacheDefaultManager";
}
