package com.actual.combat.basic.core.abs.api.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.actual.combat.basic.core.config.api.ApiConfig;
import com.actual.combat.basic.core.pojo.api.SaltInfo;

import java.util.Collection;


/**
 * @Author yan
 * @Date 2024/5/20 0020 15:35
 * @Description
 */
public interface AbstractApiSaltService {
    /**
     * 获取saltList
     * @return
     */
    default Collection<SaltInfo> getSaltList() {
        ApiConfig apiConfig = SpringUtil.getBean(ApiConfig.class);
        String apiSalt = apiConfig.getApiSalt();
        SaltInfo saltInfo = new SaltInfo().setSalt(apiSalt).setServiceName("通用");
        return CollUtil.newArrayList(saltInfo);
    }
}
