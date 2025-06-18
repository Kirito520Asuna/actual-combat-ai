package com.actual.combat.basic.gateway.core.abs;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.actual_combat.aop.abs.bean.AbsBean;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author yan
 * @Date 2025/6/14 14:57:30
 * @Description
 */
public interface GatewayDistinctResponseHeaderFilter extends GlobalFilter, Ordered, AbsBean {
    @Override
    default Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log().debug("[Order:{}]==>DistinctResponse<==", getOrder());
        //ServerWebExchange mutatedExchange = getServerWebExchange(exchange);
        return chain.filter(exchange).then(distinctResponseHeader(exchange));
    }

    @Override
    default int getOrder() {
        return GatewayFilterOrder.DISTINCT_RESPONSE_ORDER;
    }

    /**
     * 去重
     *
     * @param exchange
     * @return
     */
    default Mono distinctResponseHeader(ServerWebExchange exchange) {
        return Mono.fromRunnable(() -> {
            HttpHeaders originalHeaders = exchange.getResponse().getHeaders();

            // 新的 header 集合，用于存放合并后的结果
            Map<String, List<String>> mergedHeaders = new LinkedHashMap<>();
            // 记录 key 的规范化映射：小写 -> 原始 key（第一次出现的大小写形式）
            Map<String, String> keyMapping = new HashMap<>();

            // 遍历所有 header 进行处理
            for (Map.Entry<String, List<String>> entry : originalHeaders.entrySet()) {
                String key = entry.getKey();
                List<String> values = entry.getValue();
                if (StrUtil.equalsIgnoreCase("vary", key)) {
                    // 对于 "vary" header 保持原样
                    mergedHeaders.put(key, values);
                    continue;
                }

                String canonicalKey = key.toLowerCase();
                // 对 Access-Control-Allow-Methods 采用特殊处理：取长度最长的一条
                if (StrUtil.equalsIgnoreCase(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, canonicalKey)) {
                    if (keyMapping.containsKey(canonicalKey)) {
                        String storedKey = keyMapping.get(canonicalKey);
                        List<String> existValues = mergedHeaders.get(storedKey);
                        // 合并已有的值与当前值，选择长度最长的字符串
                        List<String> allValues = new ArrayList<>();
                        allValues.addAll(existValues);
                        allValues.addAll(values);
                        log().debug("Access-Control-Allow-Methods:existValues:{}", JSONUtil.toJsonStr(existValues));
                        log().debug("Access-Control-Allow-Methods:allValues:{}", JSONUtil.toJsonStr(allValues));
                        String longest = allValues.stream().map(StrUtil::trim).max(Comparator.comparingInt(String::length)).orElse("");
                        mergedHeaders.put(storedKey, Collections.singletonList(longest));
                    } else {
                        keyMapping.put(canonicalKey, key);
                        String longest = values.stream().max(Comparator.comparingInt(String::length)).orElse("");
                        mergedHeaders.put(key, Collections.singletonList(longest));
                    }
                    continue; // 已处理特殊 key，跳过后续通用逻辑
                }

                if (keyMapping.containsKey(canonicalKey)) {
                    // 已存在相同规范 key，则合并值
                    String storedKey = keyMapping.get(canonicalKey);
                    List<String> existValues = mergedHeaders.get(storedKey);
                    existValues.addAll(values);
                    mergedHeaders.put(storedKey, existValues.stream().distinct().collect(Collectors.toList()));
                } else {
                    // 第一次遇到该规范 key，记录映射，并存入去重后的值
                    keyMapping.put(canonicalKey, key);
                    mergedHeaders.put(key, values.stream().distinct().collect(Collectors.toList()));
                }
            }

            // 删除原始 header 中那些重复的 key（只清除非 canonical 的 key，且不影响 vary）
            originalHeaders.keySet().removeIf(key -> {
                if (StrUtil.equalsIgnoreCase("vary", key)) {
                    return false;
                }
                String canonicalKey = key.toLowerCase();
                // 如果当前 key 不是第一次出现的原始 key，则移除
                return keyMapping.containsKey(canonicalKey) && !key.equals(keyMapping.get(canonicalKey));
            });

            // 将合并后的值写回 canonical key
            mergedHeaders.forEach((key, values) -> {
                // 对 vary header 或者其他尚未被删除的 header更新值
                originalHeaders.put(key, values);
            });

        });
    }
}
