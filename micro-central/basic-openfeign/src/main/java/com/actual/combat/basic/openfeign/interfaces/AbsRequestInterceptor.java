package com.actual.combat.basic.openfeign.interfaces;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.actual.combat.aop.abs.bean.AbsBean;
import com.actual.combat.basic.enums.Openfeign;
import com.actual.combat.basic.utils.api.ApiUtil;
import com.actual.combat.basic.utils.api.SingleSignature;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.actual.combat.basic.openfeign.factory.AbsEnum;
import feign.Request;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
// 抽象类，不能直接实例化

/**
 * @Author yan
 * @Date 2024/5/14 0014 12:25
 * @Description
 */
public interface AbsRequestInterceptor extends RequestInterceptor, AbsBean {
    List<Request.HttpMethod> GET_OR_DELETE_HTTP_METHOD = CollUtil.newArrayList(Request.HttpMethod.GET, Request.HttpMethod.DELETE);
    List<Request.HttpMethod> POST_OR_PUT_HTTP_METHOD = CollUtil.newArrayList(Request.HttpMethod.POST, Request.HttpMethod.PUT);

    default AbsEnum getAbstractEnum() {
        return AbsEnum.DEFAULT;
    }

    default AbsOpenFeignClientConfiguration getAbstractOpenFeignClientConfiguration() {
        Class<? extends AbsOpenFeignClientConfiguration> aClass = AbsOpenFeignClientConfiguration.OPEN_MAP.get(getAbstractEnum());
        return SpringUtil.getBean(aClass);
    }

    @Override
    @PostConstruct
    default void init() {
        AbsBean.super.init();
    }

    @SneakyThrows
    @Override
    default void apply(RequestTemplate requestTemplate) {
        Collection<String> collection = requestTemplate.headers().get(Openfeign.OPENFEIGN.getHeader());
        //防止多次调用
        if (CollUtil.isEmpty(collection)) {
            AbsOpenFeignClientConfiguration openFeignClientConfiguration = getAbstractOpenFeignClientConfiguration();

            log().debug("openFeignClientConfiguration:{}", openFeignClientConfiguration.getAClass());

            String SALT = openFeignClientConfiguration.getApiSalt();
            String SIGN = openFeignClientConfiguration.getSignAsName();
            String TIMESTAMP = openFeignClientConfiguration.getTimestampAsName();

            Request request = requestTemplate.request();
            byte[] body = requestTemplate.body();
            String json = StrUtil.EMPTY;
            boolean bodyNorEmpty = ObjectUtil.isNotEmpty(body);
            if (bodyNorEmpty) {
                json = new String(body);
            }
            boolean requestPostOrPut = POST_OR_PUT_HTTP_METHOD.contains(request.httpMethod());
            boolean requestGetOrDelete = GET_OR_DELETE_HTTP_METHOD.contains(request.httpMethod());
            if (requestGetOrDelete || requestPostOrPut) {
                try {
                    MultiValueMap<String, String> queryParams = UriComponentsBuilder.fromUri(URI.create(request.url())).build().getQueryParams();
                    TreeMap<String, String> queryParamsTree = Maps.newTreeMap();
                    queryParams.forEach((key, strings) -> {
                        // 如果没有值，则设置为空字符串
                        List<String> list = CollUtil.isEmpty(strings) ?
                                CollUtil.newArrayList(StrUtil.EMPTY) : CollUtil.newArrayList(strings);
                        try {
                            queryParamsTree.put(URLDecoder.decode(key, StandardCharsets.UTF_8.name()),
                                    URLDecoder.decode(list.stream().findFirst().orElse(StrUtil.EMPTY), StandardCharsets.UTF_8.name()));
                        } catch (UnsupportedEncodingException e) {
                            queryParamsTree.put(key, list.stream().findFirst().orElse(StrUtil.EMPTY));
                        }
                    });


                    String originalUrl = openFeignClientConfiguration.doReconstructURL(requestTemplate.feignTarget(), URI.create(request.url()));
                    StringBuffer buffer = new StringBuffer().append(originalUrl);
                    queryParamsTree.put(TIMESTAMP, String.valueOf(System.currentTimeMillis()));
                    buffer.append("?");
                    Map<String, String[]> map = Maps.newLinkedHashMap();
                    Map<String, Object> requestBody = Maps.newLinkedHashMap();
                    queryParamsTree.keySet().stream().filter(o -> !ObjectUtil.equals(o, TIMESTAMP))
                            .forEach(key -> {
                                map.put(key, new String[]{queryParamsTree.get(key)});
                                //buffer.append(key).append("=").append(queryParamsTree.get(key)).append("&");
                            });
                    if (requestPostOrPut && StrUtil.isBlank(json)) {
                        // 获取请求体
                        requestBody.putAll(getRequestBody(requestTemplate));
                    } else if (JSONUtil.isTypeJSON(json)) {
                        //Map<String, Object> bean = JSONUtil.toBean(json, JSONConfig.create().setIgnoreNullValue(true), Map.class);
                        Map<String, Object> bean = JSON.parseObject(json, Map.class);
                        if (ObjectUtil.isNotEmpty(bean)) {
                            requestBody.putAll(bean);
                            Map<String, Object> readValue = JSON.parseObject(json, Map.class);
                            readValue.entrySet().stream().forEach(o -> {
                                String empty = null;
                                if (o.getValue() instanceof String) {
                                    empty = StrUtil.EMPTY;
                                }
                                Object value = (o.getValue() == null || "null".equals(o.getValue())) ? o.getValue() : empty;
                                requestBody.put(o.getKey(), value);
                            });

                        }
                    }

                    String sign = StrUtil.EMPTY;
                    String url = buffer.toString();
                    if (url.endsWith("?") || url.endsWith("&")) {
                        url = url.substring(0, url.length() - 1);
                    }
                    //url = request.url();
                    List<String> exCollection = CollUtil.newArrayList(SIGN, TIMESTAMP);
                    sign = generalSign(SALT, request.httpMethod().name(), url, map, requestBody, exCollection);
                    queryParamsTree.put(SIGN, sign);

                    Map<String, Collection<String>> queries = Maps.newLinkedHashMap();
                    Map<String, Collection<String>> requestHeaders = Maps.newLinkedHashMap();
                    queryParamsTree.forEach((key, value) -> {
                        if (exCollection.contains(key)) {
                            requestHeaders.put(key, Collections.singleton(value));
                        } else {
                            queries.put(key, Collections.singleton(value));
                        }
                    });
                    requestHeaders.put(Openfeign.OPENFEIGN.getHeader(), Collections.singleton(requestTemplate.feignTarget().name()));
                    requestTemplate.queries(queries);
                    Map<String, Collection<String>> headers = requestTemplate.headers();
                    Map<String, Collection<String>> headersMap = Maps.newLinkedHashMap();

                    if (CollUtil.isNotEmpty(headers)) {
                        headers.entrySet()
                                .stream()
                                .filter(o -> exCollection.contains(o.getKey()))
                                .forEach(o -> headersMap.put(o.getKey(), o.getValue()));
                    }
                    if (CollUtil.isEmpty(headersMap)) {
                        requestTemplate.headers(requestHeaders);
                    }
                    Request.Body requestedBody = requestTemplate.requestBody();
                    String bodyStr = requestedBody.asString();

                    log().debug("requestBody ==> {}", bodyStr);
                    if (JSONUtil.isTypeJSON(bodyStr)) {
                        JSONObject object = JSONUtil.parseObj(bodyStr);
                        log().debug("JSONObject ==> {}", object);
                        requestTemplate = requestTemplate.body(JSON.toJSONString(object));
                        log().debug("requestTemplate.requestBody ==> {}", requestTemplate.requestBody().asString());
                    }
                    log().debug("requestTemplate ==> {}", requestTemplate);
                } catch (Exception e) {
                    log().error("{}:error", getAClass().getName(), e);
                    throw e;
                }
            }
        }
    }

    default Map<String, Object> getRequestBody(RequestTemplate requestTemplate) {
        Map<String, Object> requestBodyMap = Maps.newLinkedHashMap();
        if (requestTemplate.requestBody().length() > 0) {
            try {
                String requestBody = new String(requestTemplate.requestBody().asBytes());
                // 将请求体转换为 Map
                requestBodyMap.putAll(SpringUtil.getBean(ObjectMapper.class).readValue(requestBody, HashMap.class));
                log().debug("[one]Request Body as Map:  {}", requestBodyMap);
                requestBodyMap.keySet().stream().forEach(key -> {
                    Object v = requestBodyMap.get(key);
                    if (ObjectUtil.isNotEmpty(v)) {
                        requestBodyMap.put(key, v);
                    }
                });
                log().debug("[two]Request Body as Map:  {}", requestBodyMap);
            } catch (IOException e) {
                log().error("{}:error", getAClass().getName(), e);
                e.printStackTrace();
            }
        }
        return requestBodyMap;
    }

    /**
     * @param salt         签名密钥
     * @param method       HTTP方法（GET、POST等）
     * @param url          请求的URL
     * @param params       请求参数的Map
     * @param body         请求body的Map
     * @param exCollection
     * @return 生成的签名字符串
     */
    @SneakyThrows
    default String generalSign(String salt, String method, String url, Map<String, String[]> params, Map<String, Object> body, Collection<String> exCollection) {
        return ApiUtil.generalSign(new SingleSignature()
                .setSalt(salt)
                .setMethod(method)
                .setUrl(url)
                .setParams(params)
                .setBody(body)
                .setExCollection(exCollection));
    }

}
