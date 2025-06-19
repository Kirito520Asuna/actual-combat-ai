package com.actual.combat.basic.utils.http;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.actual.combat.basic.utils.object.ObjectUtils;
import com.google.common.collect.Maps;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * 基于okhttp and hutool and lombok
 * 不使用hutool 发起请求的原因 ：高并发下 发起过多会出现 发起请求会断开之前发起过的请求连接
 *
 * @author yan
 * @date 2024/4/6 7:56
 */
@Slf4j
public class HttpUtils {
    public enum HttpMethod {
        GET, POST, PUT, DELETE
    }

    /**
     * url 拼接
     *
     * @param url
     * @param params
     * @return
     */
    @SneakyThrows
    private static String urlJoin(String url, Map<String, Object> params) {
        if (ObjectUtil.isNotEmpty(params)) {
            StringBuffer append = new StringBuffer(url);
            // 检查字符串的最后一个字符是否是"?"
            if (!url.endsWith("?")) {
                append.append("?");
            }
            params.keySet().forEach(key -> {
                Object obj = params.get(key);
                if (ObjectUtil.isNotEmpty(obj)) {
                    if (obj instanceof String[]) {
                        log.info("ok");
                        String[] temp = (String[]) obj;
                        obj = temp[0];
                    }
                    append.append(key).append("=").append(obj).append("&");
                }
            });
            url = append.toString();
        }
        // 检查字符串的最后一个字符是否是"&"||"?"
        if (url.endsWith("&") || url.endsWith("?")) {
            // 使用substring方法移除最后一个字符
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }

    /**
     * @param builder
     * @param headers
     * @return
     */
    @SneakyThrows
    private static Request.Builder addHeader(Request.Builder builder, Map<String, String> headers) {

        if (ObjectUtil.isNotEmpty(headers)) {
            headers.keySet().forEach(key -> {
                String obj = headers.get(key);
                if (ObjectUtil.isNotEmpty(obj)) {
                    builder.addHeader(key, obj);
                }
            });
        }
        return builder;
    }

    @SneakyThrows
    public static Map<String, Object> objectToMap(Object o) {
        Map<String, Object> map = ObjectUtils.defaultIfEmpty(BeanUtil.beanToMap(o), null);
        return map;
    }

    @SneakyThrows
    public static Map<String, Object> jsonToMap(String json) {
        Map<String, Object> map = ObjectUtils.defaultIfEmpty(JSONUtil.toBean(json, Map.class), null);
        return map;
    }

    @SneakyThrows

    public static Map<String, String> jsonHeadersToMap(String json) {
        Map<String, String> map = ObjectUtils.defaultIfEmpty(JSONUtil.toBean(json, Map.class), null);
        return map;
    }

    @SneakyThrows
    public static <T> T get(String url, String paramsJson, Class<T> clazz) {
        String json = get(url, paramsJson);
        return JSONUtil.toBean(json, clazz);
    }

    @SneakyThrows
    public static String get(String url, String paramsJson) {
        Map<String, Object> beanToMap = jsonToMap(paramsJson);
        return get(url, beanToMap);
    }

    @SneakyThrows
    public static <T> T post(String url, String bodyJson, Class<T> clazz) {
        String json = post(url, bodyJson);
        return JSONUtil.toBean(json, clazz);
    }

    @SneakyThrows
    public static String post(String url, String bodyJson) {
        Map<String, Object> beanToMap = jsonToMap(bodyJson);
        return post(url, beanToMap);
    }

    @SneakyThrows
    public static <T> T put(String url, String bodyJson, Class<T> clazz) {
        String json = put(url, bodyJson);
        return JSONUtil.toBean(json, clazz);
    }

    @SneakyThrows
    public static String put(String url, String bodyJson) {
        Map<String, Object> beanToMap = jsonToMap(bodyJson);
        return put(url, beanToMap);
    }

    @SneakyThrows
    public static <T> T delete(String url, String paramsJson, Class<T> clazz) {
        String json = delete(url, paramsJson);
        return JSONUtil.toBean(json, clazz);
    }

    @SneakyThrows
    public static String delete(String url, String paramsJson) {
        Map<String, Object> beanToMap = jsonToMap(paramsJson);
        return delete(url, beanToMap);
    }

    @SneakyThrows
    public static <T> T get(String url, Object params, Class<T> clazz) {
        String json = get(url, params);
        return JSONUtil.toBean(json, clazz);
    }

    @SneakyThrows
    public static String get(String url, Object params) {
        Map<String, Object> beanToMap = objectToMap(params);
        return get(url, beanToMap);
    }

    @SneakyThrows
    public static <T> T post(String url, Object body, Class<T> clazz) {
        String json = post(url, body);
        return JSONUtil.toBean(json, clazz);
    }

    @SneakyThrows
    public static String post(String url, Object body) {
        Map<String, Object> beanToMap = objectToMap(body);
        return post(url, beanToMap);
    }

    @SneakyThrows
    public static <T> T put(String url, Object body, Class<T> clazz) {
        String json = put(url, body);
        return JSONUtil.toBean(json, clazz);
    }

    @SneakyThrows
    public static String put(String url, Object body) {
        Map<String, Object> beanToMap = objectToMap(body);
        return put(url, beanToMap);
    }

    @SneakyThrows
    public static <T> T delete(String url, Object params, Class<T> clazz) {
        String json = delete(url, params);
        return JSONUtil.toBean(json, clazz);
    }

    @SneakyThrows
    public static String delete(String url, Object params) {
        Map<String, Object> beanToMap = objectToMap(params);
        return delete(url, beanToMap);
    }

    @SneakyThrows
    public static <T> T get(String url, Map<String, Object> params, Class<T> clazz) {
        String json = get(url, params);
        return JSONUtil.toBean(json, clazz);
    }

    @SneakyThrows
    public static String get(String url, Map<String, Object> params) {
        return get(url, params, new HashMap<>());
    }

    @SneakyThrows
    public static <T> T post(String url, Map<String, Object> body, Class<T> clazz) {
        String json = post(url, body);
        return JSONUtil.toBean(json, clazz);
    }

    @SneakyThrows
    public static String post(String url, Map<String, Object> body) {
        return post(url, null, body, null, null);
    }

    @SneakyThrows
    public static <T> T put(String url, Map<String, Object> body, Class<T> clazz) {
        String json = put(url, body);
        return JSONUtil.toBean(json, clazz);
    }

    @SneakyThrows
    public static String put(String url, Map<String, Object> body) {
        return put(url, null, body, null, null);
    }

    @SneakyThrows
    public static <T> T delete(String url, Map<String, Object> params, Class<T> clazz) {
        String json = delete(url, params);
        return JSONUtil.toBean(json, clazz);
    }

    @SneakyThrows
    public static String delete(String url, Map<String, Object> params) {
        return delete(url, params, new HashMap<>());
    }

    @SneakyThrows
    public static <T> T get(String url, Map<String, Object> params, Map<String, String> headers, Class<T> clazz) {
        String json = get(url, params, headers);
        return JSONUtil.toBean(json, clazz);
    }

    @SneakyThrows
    public static String get(String url, Map<String, Object> params, Map<String, String> headers) {
        return get(null, url, params, headers);
    }

    @SneakyThrows
    public static <T> T get(OkHttpClient client, String url, Map<String, Object> params, Map<String, String> headers, Class<T> clazz) {
        String json = get(client, url, params, headers);
        return JSONUtil.toBean(json, clazz);
    }

    @SneakyThrows
    public static String get(OkHttpClient client, String url, Map<String, Object> params, Map<String, String> headers) {
        HttpMethod get = HttpMethod.GET;
        return sendHttpRequest(client, get, url, params, null, headers, null);
    }

    @SneakyThrows
    public static <T> T post(String url, Map<String, Object> params, Map<String, Object> body, Map<String, String> headers, String mediaType, Class<T> clazz) {
        String json = post(url, params, body, headers, mediaType);
        return JSONUtil.toBean(json, clazz);
    }

    @SneakyThrows
    public static String post(String url, Map<String, Object> params, Map<String, Object> body, Map<String, String> headers, String mediaType) {
        return post(null, url, params, body, headers, mediaType);
    }

    @SneakyThrows
    public static <T> T post(OkHttpClient client, String url, Map<String, Object> params, Map<String, Object> body, Map<String, String> headers, String mediaType, Class<T> clazz) {
        String json = post(client, url, params, body, headers, mediaType);
        return JSONUtil.toBean(json, clazz);
    }

    @SneakyThrows
    public static String post(OkHttpClient client, String url, Map<String, Object> params, Map<String, Object> body, Map<String, String> headers, String mediaType) {
        HttpMethod post = HttpMethod.POST;
        return sendHttpRequest(client, post, url, params, body, headers, mediaType);
    }

    @SneakyThrows
    public static <T> T put(String url, Map<String, Object> params, Map<String, Object> body, Map<String, String> headers, String mediaType, Class<T> clazz) {
        String json = put(url, params, body, headers, mediaType);
        return JSONUtil.toBean(json, clazz);
    }

    @SneakyThrows
    public static String put(String url, Map<String, Object> params, Map<String, Object> body, Map<String, String> headers, String mediaType) {
        return put(null, url, params, body, headers, mediaType);
    }

    @SneakyThrows
    public static <T> T put(OkHttpClient client, String url, Map<String, Object> params, Map<String, Object> body, Map<String, String> headers, String mediaType, Class<T> clazz) {
        String json = put(client, url, params, body, headers, mediaType);
        return JSONUtil.toBean(json, clazz);
    }

    @SneakyThrows
    public static String put(OkHttpClient client, String url, Map<String, Object> params, Map<String, Object> body, Map<String, String> headers, String mediaType) {
        HttpMethod put = HttpMethod.PUT;
        return sendHttpRequest(client, put, url, params, body, headers, mediaType);
    }

    @SneakyThrows
    public static <T> T delete(String url, Map<String, Object> params, Map<String, String> headers, Class<T> clazz) {
        String json = delete(url, params, headers);
        return JSONUtil.toBean(json, clazz);
    }

    @SneakyThrows
    public static String delete(String url, Map<String, Object> params, Map<String, String> headers) {
        return delete(null, url, params, headers);
    }

    @SneakyThrows
    public static <T> T delete(OkHttpClient client, String url, Map<String, Object> params, Map<String, String> headers, Class<T> clazz) {
        String json = delete(client, url, params, headers);
        return JSONUtil.toBean(json, clazz);
    }

    @SneakyThrows
    public static String delete(OkHttpClient client, String url, Map<String, Object> params, Map<String, String> headers) {
        HttpMethod delete = HttpMethod.DELETE;
        return sendHttpRequest(client, delete, url, params, null, headers, null);
    }

    @SneakyThrows
    public static <T> T sendHttpRequest(OkHttpClient client, HttpMethod httpMethod, String url, String paramsJson, String bodyJson, String headersJson, String mediaType, Class<T> clazz) {
        String json = sendHttpRequest(client, httpMethod, url, paramsJson, bodyJson, headersJson, mediaType);
        return JSONUtil.toBean(json, clazz);
    }

    @SneakyThrows
    public static String sendHttpRequest(OkHttpClient client, HttpMethod httpMethod, String url, String paramsJson, String bodyJson, String headersJson, String mediaType) {
        Map<String, Object> paramsToMap = jsonToMap(paramsJson);
        Map<String, Object> bodyToMap = jsonToMap(bodyJson);
        Map<String, String> headersToMap = jsonHeadersToMap(headersJson);
        return sendHttpRequest(client, httpMethod, url, paramsToMap, bodyToMap, headersToMap, mediaType);
    }

    @SneakyThrows
    public static <T> T sendHttpRequest(OkHttpClient client, HttpMethod httpMethod, String url, Object params, Object body, Object headers, String mediaType, Class<T> clazz) {
        String json = sendHttpRequest(client, httpMethod, url, params, body, headers, mediaType);
        return JSONUtil.toBean(json, clazz);
    }
    @SneakyThrows
    public static String sendHttpRequest(OkHttpClient client, HttpMethod httpMethod, String url, Object params, Object body, Object headers, String mediaType) {
        Map<String, Object> paramsToMap = objectToMap(params);
        Map<String, Object> bodyToMap = objectToMap(body);
        Map<String, Object> headersToMapTemp = objectToMap(headers);
        Map<String, String> headersToMap = ObjectUtil.isEmpty(headers) ? null : new LinkedHashMap<>();
        if (ObjectUtil.isNotEmpty(headers)) {
            headersToMapTemp.keySet().stream().forEach(key -> headersToMap.put(key, String.valueOf(headersToMapTemp.get(key))));
        }
        return sendHttpRequest(client, httpMethod, url, paramsToMap, bodyToMap, headersToMap, mediaType);
    }

    /**
     * 发送请求(该类最底层)
     *
     * @param httpMethod 请求方式
     * @param url        请求地址
     * @param params     请求行
     * @param body       请求体
     * @param headers    请求头
     * @param mediaType  请求格式
     * @return 响应
     */
    @SneakyThrows
    public static String sendHttpRequest(OkHttpClient client, HttpMethod httpMethod, String url, Map<String, Object> params, Map<String, Object> body, Map<String, String> headers, String mediaType) {
        Map<String, Object> sendMap = Maps.newLinkedHashMap();
        sendMap.put("httpMethod", httpMethod);
        sendMap.put("url", url);
        sendMap.put("headers", headers);
        sendMap.put("params", params);
        sendMap.put("body", body);
        log.info("[SendHttp]::[START]==>[info]:[{}]<==[END]", JSONUtil.toJsonStr(sendMap));
        url = urlJoin(url, params);
        log.info("[SendHttp]::[splicingTogether]==>url:{}<==[END]", url);


        Request.Builder builder = addHeader(new Request.Builder(), headers).url(url);
        if (ObjectUtils.equals(HttpMethod.GET, httpMethod)) {
            builder.get();
        } else if (ObjectUtils.equals(HttpMethod.POST, httpMethod) || ObjectUtils.equals(HttpMethod.PUT, httpMethod)) {
            String jsonStr = JSONUtil.toJsonStr(ObjectUtils.defaultIfEmpty(body, new LinkedHashMap<>()));
            //ObjectUtil.isEmpty(mediaType) ? "application/json" : mediaType
            RequestBody requestBody = RequestBody.create(MediaType.parse(ObjectUtils.defaultIfEmpty(mediaType, "application/json")), jsonStr);
            builder = addUpdateMethod(httpMethod, requestBody, builder);
        } else if (ObjectUtils.equals(HttpMethod.DELETE, httpMethod)) {
            builder.delete();
        } else {
            throw new Exception("请求方法不存在!");
        }
        Request request = builder.build();
        if (client == null) {
            client = new OkHttpClient();
        }
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            log.info("[SendHttp]::[Success]");
        } else {
            log.error("[SendHttp]::[Error]:[code={},message={}]", response.code(), response.message());
        }
        String responseData = response.body().string();
        return responseData;
    }

    @SneakyThrows
    public static String sendHttpRequest(OkHttpClient client, HttpMethod httpMethod, String url, Map<String, Object> params, RequestBody requestBody, Map<String, String> headers) {
        if (!CollUtil.newArrayList(HttpMethod.PUT, HttpMethod.POST).contains(httpMethod)) {
            log.error("请求方法{}不支持!,请使用PUT|POST", httpMethod);
            throw new Exception("请求方法不支持!");
        }

        Map<String, Object> sendMap = Maps.newLinkedHashMap();
        sendMap.put("httpMethod", httpMethod);
        sendMap.put("url", url);
        sendMap.put("headers", headers);
        sendMap.put("params", params);
        sendMap.put("body", requestBody.toString());
        log.info("[SendHttp]::[START]==>[info]:[{}]<==[END]", JSONUtil.toJsonStr(sendMap));
        url = urlJoin(url, params);
        log.info("[SendHttp]::[splicingTogether]==>url:{}<==[END]", url);

        Request.Builder builder = addHeader(new Request.Builder(), headers).url(url);
        builder = addUpdateMethod(httpMethod, requestBody, builder);
        Request request = builder.build();
        if (client == null) {
            client = new OkHttpClient();
        }
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            log.info("[SendHttp]::[Success]");
        } else {
            log.error("[SendHttp]::[Error]:[code={},message={}]", response.code(), response.message());
        }
        String responseData = response.body().string();
        return responseData;
    }

    private static Request.Builder addUpdateMethod(HttpMethod httpMethod, RequestBody requestBody, Request.Builder builder) {
        if (ObjectUtil.isNotEmpty(requestBody)) {
            switch (httpMethod) {
                case PUT:
                    builder.put(requestBody);
                    break;
                case POST:
                    builder.post(requestBody);
                    break;
            }
        }
        return builder;
    }
}
