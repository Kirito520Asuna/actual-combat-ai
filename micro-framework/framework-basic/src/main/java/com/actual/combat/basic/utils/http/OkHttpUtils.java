package com.actual.combat.basic.utils.http;

import lombok.extern.slf4j.Slf4j;



/**
 * 基于okhttp and hutool and lombok
 * 不使用hutool 发起请求的原因 ：高并发下 发起过多会出现 发起请求会断开之前发起过的请求连接
 *
 * @author yan
 * @date 2024/4/6 7:56
 */
@Slf4j
public class OkHttpUtils extends HttpUtils {
/*
    @SneakyThrows
    public static void main(String[] args) {
        long currentTimeMillis = System.currentTimeMillis();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime inclusive = DateUtils.longToLocalDateTime(currentTimeMillis).minusMonths(20);
        Duration between = Duration.between(inclusive, now);
        long a = between.toMinutes();
        long abs = Math.abs(a);
        //currentTimeMillis = DateUtils.LocalDateTimeTolong(inclusive);
        System.err.println(abs);
        String apiSalt = "API_SALT";
        String timestamp = "timestamp";
        String sign = "sign";
        String url = "http://127.0.0.1:9500/im/api/user/getUser";
        String method = "GET";
        Map<String, String[]> map1 = Maps.newLinkedHashMap();
        map1.put("userId", new String[]{"5"});
        Map<String, Object> map = Maps.newLinkedHashMap();
        map.putAll(map1);

        SingleSignature singleSignature = new SingleSignature()
                .setSalt(apiSalt)
                .setMethod(method)
                .setUrl(url)
                .setParams(map1)
                .setBody(Maps.newLinkedHashMap())
                .setExCollection(CollUtil.newArrayList(sign, timestamp));

        String generalSign = ApiUtil.generalSign(singleSignature);
        Map<String, String> hashMap = Maps.newLinkedHashMap();
        hashMap.put(sign, generalSign);
        hashMap.put(timestamp, currentTimeMillis + "");
        String s = get(url, map, hashMap);
        System.err.println(s);
    }*/
}
