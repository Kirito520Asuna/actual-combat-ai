package com.actual_combat.redis.service;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.spring.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * @Author yan
 * @Date 2025/5/19 15:52:42
 * @Description
 */
public interface RedisService {
    default Logger log() {
        Logger logger = LoggerFactory.getLogger(this.getClass());
        return logger;
    }

    default RedisTemplate getRedisTemplate() {
        return SpringUtil.getBean(RedisTemplate.class);
    }

    /**
     * 获取缓存数据
     *
     * @param key key
     * @return
     */
    default Object get(String key) {
        return get("", false, key);
    }

    /**
     * 获取缓存数据
     *
     * @param cacheName 缓存名称
     * @param isHash    是否hash
     * @param key       key
     * @return
     */
    default Object get(String cacheName, boolean isHash, String key) {
        RedisTemplate redisTemplate = getRedisTemplate();
        Object value;
        if (isHash) {
            value = redisTemplate.opsForHash().get(cacheName, key);
        } else {
            value = redisTemplate.opsForValue().get(key);
        }

        if (value != null) {
            log().debug("缓存命中，key:{},value:{}", key, value);
        } else {
            log().error("缓存未命中，key:{}", key);
        }
        return value;
    }

    /**
     * 获得缓存的基本对象（泛型）。
     *
     * @param key 缓存键值
     * @return 缓存键值对应的数据
     */
    default <T> T getGenerics(String key) {
        RedisTemplate redisTemplate = getRedisTemplate();
        ValueOperations<String, T> operation = redisTemplate.opsForValue();
        T t = operation.get(key);
        if (t != null) {
            log().debug("缓存命中，key:{},value:{}", key, t);
        } else {
            log().error("缓存未命中，key:{}", key);
        }
        return t;
    }

    /**
     * 缓存数据
     *
     * @param key
     * @param value
     * @return
     */
    default boolean save(String key, Object value) {
        return save(false, null, key, value, -1, null);
    }

    /**
     * 缓存数据
     *
     * @param key
     * @param value
     * @param timout
     * @param timeUnit
     * @return
     */
    default boolean save(String key, Object value, long timout, TimeUnit timeUnit) {
        return save(false, null, key, value, timout, timeUnit);
    }

    /**
     * @param isHash
     * @param cacheName
     * @param key
     * @param value
     * @return
     */
    default boolean save(boolean isHash, String cacheName, String key, Object value) {
        return save(isHash, cacheName, key, value, -1, null);
    }

    /**
     * @param isHash
     * @param cacheName
     * @param key
     * @param value
     * @param timout
     * @param timeUnit
     * @return
     */
    default boolean save(boolean isHash, String cacheName, String key, Object value, long timout, TimeUnit timeUnit) {
        return save(false, null, cacheName, isHash, key, value, timout, timeUnit);
    }

    /**
     * 缓存数据
     *
     * @param random      是否随机
     * @param randomRange 随机范围 0~1
     * @param cacheName   缓存名称
     * @param isHash      是否hash
     * @param key         key
     * @param value       value
     * @param timeout     过期时间(>0时生效)
     * @param timeUnit    时间单位
     * @return
     */
    default boolean save(boolean random, String randomRange, String cacheName, boolean isHash, String key, Object value, long timeout, TimeUnit timeUnit) {
        RedisTemplate redisTemplate = getRedisTemplate();
        if (random) {
            try {
                String[] split = randomRange.split("~");
                timeout = RandomUtil.randomLong(Long.parseLong(split[0]), Long.parseLong(split[1]));
            } catch (Exception e) {
                log().error("随机数生成失败:{}", e);
                throw e;
            }
            redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
        } else if (isHash) {
            redisTemplate.opsForHash().put(cacheName, key, value);
            if (timeout > 0) {
                redisTemplate.expire(cacheName, timeout, timeUnit);
            }
        } else if (timeout < 1) {
            redisTemplate.opsForValue().set(key, value);
        } else {
            redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
        }
        log().debug("redis缓存数据成功，key:{},value:{},timeout:{}", key, value, timeout);
        return true;
    }

    /**
     * 删除数据
     *
     * @param key
     * @return
     */
    default boolean del(String key) {
        return del(null, false, key);
    }

    /**
     * 删除数据
     *
     * @param cacheName 缓存名称
     * @param isHash    是否hash
     * @param key       key
     * @return
     */
    default boolean del(String cacheName, boolean isHash, String key) {
        RedisTemplate redisTemplate = getRedisTemplate();
        if (isHash) {
            redisTemplate.opsForHash().delete(cacheName, key);
            log().debug("删除hash缓存成功，key:{}", key);
        } else {
            redisTemplate.delete(key);
            log().debug("删除缓存成功，key:{}", key);
        }
        return true;
    }

    /**
     * 删除集合对象
     *
     * @param collection 多个对象
     * @return
     */
    default boolean delList(Collection collection) {
        RedisTemplate redisTemplate = getRedisTemplate();
        boolean del = redisTemplate.delete(collection) > 0;
        if (del) {
            log().debug("删除集合缓存成功，key:{}", collection);
        } else {
            log().error("删除集合缓存失败，key:{}", collection);
        }
        return del;
    }

    /**
     * 获得缓存的基本对象列表
     *
     * @param pattern 字符串前缀
     * @return 对象列表
     */
    default Collection<String> keys(String pattern) {
        RedisTemplate redisTemplate = getRedisTemplate();
        return redisTemplate.keys(pattern);
    }
}
