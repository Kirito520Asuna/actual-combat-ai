package com.actual.combat.elasticsearch.service.impl;

import com.actual.combat.elasticsearch.service.ElasticsearchService;
import jakarta.annotation.Resource;
import org.springframework.data.elasticsearch.client.erhlc.ElasticsearchRestTemplate;



/**
 * @Author yan
 * @Date 2024/7/26 0026 16:35:27
 * @Description
 */

public class SimpleElasticsearchService implements ElasticsearchService {
    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Override
    public ElasticsearchRestTemplate getElasticsearchRestTemplate() {
        return elasticsearchRestTemplate;
    }
}
