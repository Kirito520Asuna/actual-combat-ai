package com.actual.combat.redis.service;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.spring.SpringUtil;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBucket;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Collection;
import java.util.Set;
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

    /*[RedisTemplate]===========================================================================================*/
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
            key = cacheName + ":" + key;
        } else {
            value = redisTemplate.opsForValue().get(key);
        }

        Logger log = log();
        if (value != null) {
            log.debug("[RT]缓存命中,key:{},value:{}", key, value);
        } else {
            log.error("[RT]缓存未命中,key:{}", key);
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
        Logger log = log();
        if (t != null) {
            log.debug("[RT]缓存命中,key:{},value:{}", key, t);
        } else {
            log.error("[RT]缓存未命中,key:{}", key);
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
        Logger log = log();
        if (random) {
            try {
                String[] split = randomRange.split("~");
                timeout = RandomUtil.randomLong(Long.parseLong(split[0]), Long.parseLong(split[1]));
            } catch (Exception e) {
                log.error("随机数生成失败:{}", e);
                throw e;
            }
            redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
        } else if (isHash) {
            redisTemplate.opsForHash().put(cacheName, key, value);
            if (timeout > 0) {
                redisTemplate.expire(cacheName, timeout, timeUnit);
            }
            key = cacheName + ":" + key;
        } else if (timeout < 1) {
            redisTemplate.opsForValue().set(key, value);
        } else {
            redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
        }
        log.debug("[RT]缓存数据成功,key:{},value:{},timeout:{}", key, value, timeout);
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
        boolean del = false;
        Logger log = log();
        if (isHash) {
            del = redisTemplate.opsForHash().delete(cacheName, key) > 0;
            key = cacheName + ":" + key;
            log.debug("[RT]删除hash缓存成功,key:{}", key);
        } else {
            del = redisTemplate.delete(key);
            log.debug("[RT]删除缓存成功,key:{}", key);
        }
        return del;
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
        Logger log = log();
        if (del) {
            log.debug("[RT]删除集合缓存成功,key:{}", collection);
        } else {
            log.error("[RT]删除集合缓存失败,key:{}", collection);
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

    /**
     * add Set key value
     *
     * @param key
     * @param values
     * @return
     */
    default boolean addToSet(Object key, Object... values) {
        // 使用 opsForSet() 获取 Set 操作对象
        boolean add = getRedisTemplate().opsForSet().add(key, values) > 0;
        Logger log = log();
        if (add) {
            log.debug("[RT]添加Set缓存成功,key:{},values:{}", key, values);
        }else {
            log.error("[RT]添加Set缓存失败,key:{},values:{}", key, values);
        }
        return add;
    }

    /**
     * 获取Set
     *
     * @param key
     * @return
     */
    default Set<Object> getSetMembers(Object key) {
        // 获取 Set 中的所有成员
        Set<Object> members = getRedisTemplate().opsForSet().members(key);
        Logger log = log();
        log.debug("[RT]缓存Set命中,key:{},value:{}", key, members);
        return members;
    }

    /**
     * 从Set中移除指定元素
     *
     * @param key
     * @param value
     * @return
     */
    default boolean removeFromSet(Object key, Object value) {
        // 从 Set 中移除指定元素
        boolean del = getRedisTemplate().opsForSet().remove(key, value) > 0;
        Logger log = log();
        if (del) {
            log.debug("[RT]删除Set缓存成功,key:{}", key, value);
        } else {
            log.error("[RT]删除Set缓存失败,key:{}", key, value);
        }
        return del;
    }

    /**
     * 获取Set的大小
     *
     * @param key
     * @return
     */
    default Long getSetSize(String key) {
        // 获取 Set 的大小
        Long size = getRedisTemplate().opsForSet().size(key);
        Logger log = log();
        if (size != null) {
            log.debug("[RT]缓存Set命中,key:{},size:{}", key, size);
        } else {
            log.error("[RT]缓存Set未命中");
        }
        return size;
    }

    /*[RedissonClient]===========================================================================================*/

    default RedissonClient getRedissonClient() {
        return SpringUtil.getBean(RedissonClient.class);
    }

    /**
     * 获取redisson的set
     *
     * @param key
     * @return
     */
    default RSet<String> getSetByRS(String key) {
        RSet<String> value = getRedissonClient().getSet(key);
        Logger log = log();
        if (value != null) {
            log.debug("[RS]缓存命中,key:{},value:{}", key, value);
        } else {
            log.error("[RS]缓存未命中,key:{}", key);
        }
        return value;
    }

    /**
     * 获取redisson的bucket
     *
     * @param key
     * @return
     */
    default <V> RBucket<V> getBucketByRS(String key) {
        RBucket<V> value = getRedissonClient().getBucket(key);
        Logger log = log();
        if (value != null) {
            log.debug("[RS]缓存命中Bucket,key:{},value:{}", key, value);
        } else {
            log.error("[RS]缓存未命中Bucket,key:{}", key);
        }
        return value;
    }

    /**
     * 获取redisson的RAtomicLong
     *
     * @param key
     * @return
     */
    default RAtomicLong getAtomicLongByRS(String key) {
        RAtomicLong atomicLong = getRedissonClient().getAtomicLong(key);
        Logger log = log();
        if (atomicLong != null) {
            log.debug("[RS]缓存命中AtomicLong,key:{},value:{}", key, atomicLong);
        } else {
            log.error("[RS]缓存未命中AtomicLong,key:{}", key);
        }
        return atomicLong;
    }
}

