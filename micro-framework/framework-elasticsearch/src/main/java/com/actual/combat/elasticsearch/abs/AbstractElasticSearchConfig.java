package com.actual.combat.elasticsearch.abs;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.erhlc.RestClients;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * @Author yan
 * @Date 2025/6/17 23:15:36
 * @Description
 */
public interface AbstractElasticSearchConfig {
    /**
     *
     * @param username
     * @param password
     * @param host
     * @param port
     * @param uris
     * @param hostList
     * @return
     */

    default RestHighLevelClient buildRestHighLevelClient(String username, String password, String host, int port, List<String> uris, List<HttpHost> hostList) {
        RestHighLevelClient rest;
        if (ObjectUtils.isEmpty(username) && ObjectUtils.isEmpty(password)) {
            String addr = new StringBuilder(host)
                    .append(":")
                    .append(port)
                    .toString();
            List<String> addrList = CollUtil.newArrayList();
            if (CollUtil.isNotEmpty(uris)) {
                addrList.addAll(uris);
            } else {
                addrList.add(addr);
            }
            //无账号密码
            ClientConfiguration.MaybeSecureClientConfigurationBuilder builder = ClientConfiguration.builder()
                    .connectedTo(addrList.toArray(new String[addrList.size()]));
            if (ObjectUtil.isNotEmpty(username) && ObjectUtil.isNotEmpty(password)) {
                builder.withBasicAuth(username, password);
            }
            ClientConfiguration clientConfiguration = builder.build();
            rest = RestClients.create(clientConfiguration).rest();
        } else {
            //有账号密码
            HttpHost httpHost = new HttpHost(host, port);
            List<HttpHost> httpHostList = CollUtil.newArrayList();
            if (CollUtil.isNotEmpty(uris)) {
                httpHostList.addAll(hostList);
            } else {
                httpHostList.add(httpHost);
            }
            RestClientBuilder builder = RestClient.builder(httpHostList.toArray(new HttpHost[httpHostList.size()]));

            if (ObjectUtil.isNotEmpty(username) && ObjectUtil.isNotEmpty(password)) {
                CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
                builder.setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
            }
            rest = new RestHighLevelClient(builder);
        }
        return rest;
    }

}
