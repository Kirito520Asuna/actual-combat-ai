package com.actual_combat.easy.elasticsearch.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import lombok.SneakyThrows;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Author yan
 * @Date 2024/8/13 0013 15:41:41
 * @Description
 */
public interface EasyElasticsearchService {
    /**
     * 获取RestHighLevelClient
     *
     * @return
     */
    default RestHighLevelClient getRestHighLevelClient() {
        return SpringUtil.getBean(RestHighLevelClient.class);
    }

    /**
     * 构建查询请求
     * @param indexName
     * @return
     */
    default SearchRequest getSearchRequest(String indexName) {
        return new SearchRequest(indexName);
    }

    /**
     * 构建查询请求
     * @param indexName
     * @param boolQueryBuilder
     * @param searchSourceBuilder
     * @return
     */
    default SearchRequest builderSearchRequest(String indexName, BoolQueryBuilder boolQueryBuilder, SearchSourceBuilder searchSourceBuilder) {
        SearchRequest searchRequest = getSearchRequest(indexName);
        if (boolQueryBuilder == null) {
            boolQueryBuilder = QueryBuilders.boolQuery();
        }
        if (searchSourceBuilder == null) {
            searchSourceBuilder = new SearchSourceBuilder();
        }
        searchSourceBuilder.query(boolQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        return searchRequest;
    }

    /**
     * 获取列表
     *
     * @param searchRequest
     * @param clazz
     * @return
     * @param <T>
     */
    @SneakyThrows
    default <T> List<T> getList(SearchRequest searchRequest,Class<T> clazz) {
        List<T> list = CollUtil.newArrayList();
        SearchResponse searchResponse = getRestHighLevelClient().search(searchRequest, RequestOptions.DEFAULT);
        try {
            SearchHit[] searchHits = Optional.ofNullable(searchResponse).map(SearchResponse::getHits).map(SearchHits::getHits).get();

            List<T> documents = Arrays.stream(searchHits)
                    .map(hit -> JSONUtil.toBean(hit.getSourceAsString(), clazz))
                    .collect(Collectors.toList());
            list.addAll(documents);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
