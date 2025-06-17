package com.actual.combat.elasticsearch.service;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.data.elasticsearch.client.erhlc.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.client.erhlc.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.SearchHits;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author yan
 * @Date 2024/7/26 0026 16:27:55
 * @Description
 */
public interface ElasticsearchService {
    /**
     *
     * @return
     */
    default ElasticsearchRestTemplate getElasticsearchRestTemplate(){
        return SpringUtil.getBean(ElasticsearchRestTemplate.class);
    }
    /**
     * 通用查询
     *
     * @param nativeSearchQuery
     * @param aclass
     * @param <T>
     * @return
     */
    default <T> SearchHits<T> search(NativeSearchQuery nativeSearchQuery, Class<T> aclass) {
        return getElasticsearchRestTemplate().search(nativeSearchQuery, aclass);
    }

    /**
     * @param nativeSearchQuery 搜索构造
     * @param aclass            ES实体
     * @param <T>
     * @return List
     */
    default <T> List<T> searchList(NativeSearchQuery nativeSearchQuery, Class<T> aclass) {
        return search(nativeSearchQuery, aclass).stream().map(o -> o.getContent()).collect(Collectors.toList());
    }
}
