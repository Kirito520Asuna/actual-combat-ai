package com.actual.combat.basic.dict.core.utils;

import cn.hutool.extra.spring.SpringUtil;
import com.actual.combat.basic.core.constant.cache.CacheConstants;
import com.actual.combat.basic.utils.object.ObjectUtils;
import com.actual.combat.dict.domain.SysDictData;
import com.actual.combat.redis.service.RedisService;
import com.alibaba.fastjson2.JSONArray;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 字典工具类
 *
 * @author yan
 */
public class DictUtils {
    /**
     * 设置cache key
     *
     * @param configKey 参数键
     * @return 缓存键key
     */
    public static String getCacheKey(String configKey) {
        return CacheConstants.SYS_DICT_KEY + configKey;
    }

    /**
     * 设置字典缓存
     *
     * @param key       参数键
     * @param dictDatas 字典数据列表
     */
    public static void setDictCache(String key, List<SysDictData> dictDatas) {
        SpringUtil.getBean(RedisService.class).save(getCacheKey(key), dictDatas);
    }

    /**
     * 获取字典缓存
     *
     * @param key 参数键
     * @return dictDatas 字典数据列表
     *///
    public static List<SysDictData> getDictCache(String key) {
        JSONArray arrayCache = new JSONArray((ArrayList) SpringUtil.getBean(RedisService.class).getGenerics(getCacheKey(key)));
        if (ObjectUtils.isNotNull(arrayCache)) {
            return arrayCache.toList(SysDictData.class);
        }
        return null;
    }

    /**
     * 删除指定字典缓存
     *
     * @param key 字典键
     */
    public static void removeDictCache(String key) {
        SpringUtil.getBean(RedisService.class).del(getCacheKey(key));
    }

    /**
     * 清空字典缓存
     */
    public static void clearDictCache() {
        Collection<String> keys = SpringUtil.getBean(RedisService.class).keys(CacheConstants.SYS_DICT_KEY + "*");
        SpringUtil.getBean(RedisService.class).delList(keys);
    }
}
