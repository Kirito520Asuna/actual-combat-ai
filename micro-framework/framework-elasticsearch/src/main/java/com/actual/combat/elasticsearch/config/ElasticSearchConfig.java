package com.actual.combat.elasticsearch.config;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.actual.combat.elasticsearch.abs.AbstractElasticSearchConfig;
import com.actual.combat.elasticsearch.service.ElasticsearchService;
import com.actual.combat.elasticsearch.service.impl.SimpleElasticsearchService;
import lombok.Data;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.util.ObjectUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

//    spring:
//    elasticsearch:
//    rest:
//    uris: 192.168.200.128:9200,192.168.200.129:9200
//    url:
//    host:
//    port:
//    username: elastic
//    password: ppT8kmzu^3I

/**
 * ES配置类：初始化ES客户端
 */
@Data
@ConditionalOnMissingBean(AbstractElasticSearchConfig.class)
@Configuration
@EnableElasticsearchRepositories
@ConfigurationProperties(prefix = "spring.elasticsearch.rest")
public class ElasticSearchConfig implements AbstractElasticSearchConfig {
    private String host;
    private int port;
    private List<String> uris = Collections.emptyList();
    private String url;
    private String username;
    private String password;

    public String getHost() {
        String url = getUrl();
        if (ObjectUtil.isNotEmpty(url)) {
            String[] split = url.split(":");
            String host = split[0];
            return host;
        }
        return host;
    }

    public int getPort() {
        if (ObjectUtil.isNotEmpty(url)) {
            String url = getUrl();
            String[] split = url.split(":");
            String port = split[1];
            return Integer.parseInt(port);
        }
        return port;
    }

    /**
     * 解析 uris 为 HttpHost 列表
     */
    private List<HttpHost> parseHttpHosts() {
        if (uris.isEmpty()) {
            return CollUtil.newArrayList(new HttpHost("127.0.0.1", 9200));
        }
        return uris.stream()
                .map(String::trim)
                .map(uri -> {
                    String schemeHttp = "http";
                    String schemeHttps = "https";
                    boolean isHttps = uri.startsWith(schemeHttps);
                    if (isHttps) {
                        uri = uri.replace(schemeHttps + "://", "");
                    } else if (uri.startsWith(schemeHttp)) {
                        uri = uri.replace(schemeHttp + "://", "");
                    }
                    String[] parts = uri.split(":");
                    if (parts.length != 2) {
                        throw new IllegalArgumentException("Invalid URI format: " + uri);
                    }
                    String host = parts[0];
                    int port;
                    try {
                        port = Integer.parseInt(parts[1]);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid port in URI: " + uri);
                    }
                    if (isHttps) {
                        return new HttpHost(host, port, schemeHttps);
                    }
                    return new HttpHost(host, port);
                })
                .collect(Collectors.toList());
    }

    @Bean
    public RestHighLevelClient client() {
        //ElasticsearchProperties elasticsearchProperties = SpringUtil.getBean(ElasticsearchProperties.class);
        RestHighLevelClient rest;
        String defHost = "127.0.0.1";
        int defPort = 9200;
        String host = getHost();
        int port = getPort();

        host = ObjectUtils.isEmpty(host) ? defHost : host;
        port = ObjectUtils.isEmpty(port) ? defPort : port;

        List<String> uris = this.uris;
        String username = this.username;
        String password = this.password;
        List<HttpHost> hostList = StrUtil.isNotBlank(username) && StrUtil.isNotBlank(password) ? null : parseHttpHosts();
        return buildRestHighLevelClient(username, password, host, port, uris, hostList);
    }

    @Bean
    @ConditionalOnMissingBean(ElasticsearchService.class)
    public ElasticsearchService elasticsearchService() {
        return new SimpleElasticsearchService();
    }


}
