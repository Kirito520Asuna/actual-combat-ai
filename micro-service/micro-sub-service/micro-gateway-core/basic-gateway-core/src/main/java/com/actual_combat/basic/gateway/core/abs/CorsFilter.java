package com.actual_combat.basic.gateway.core.abs;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import com.actual_combat.aop.abs.bean.AbsBean;
import com.actual_combat.base.core.properties.cors.CorsProperties;
import com.actual_combat.base.utils.object.ObjectUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @Author yan
 * @Date 2025/6/14 15:11:48
 * @Description
 */
public interface CorsFilter extends GlobalFilter, AbsBean, Ordered {
    @Override
    default int getOrder() {
        return GatewayFilterOrder.CORS_HEADER_ORDER;
    }

    default Mono<Void> cors(ServerWebExchange exchange) {
        HttpHeaders headers = exchange.getResponse().getHeaders();

        String allowedOrigin = null;
        String allowedHeader = null;
        Boolean allowCredentials = null;
        Long maxAge = null;
        String allowedMethods = null;

        try {
            CorsProperties cors = SpringUtil.getBean(CorsProperties.class);
            log().debug("CorsProperties:{}", JSONUtil.toJsonStr(cors, JSONConfig.create().setIgnoreNullValue(false)));
            allowedOrigin = cors.getAllowedOrigin();
            allowedHeader = cors.getAllowedHeader();
            allowCredentials = cors.getAllowCredentials();
            maxAge = cors.getMaxAge();
            allowedMethods = cors.getAllowedMethods();
        } catch (Exception e) {
            log().warn("CorsProperties is null");
        }
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, ObjectUtils.defaultIfEmpty(allowedOrigin, "*"));
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, String.valueOf(ObjectUtils.defaultIfEmpty(allowCredentials, true)));

        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, StrUtil.trim(ObjectUtils.defaultIfEmpty(allowedMethods, "GET,POST,PUT,DELETE,OPTIONS,PATCH,HEAD")));
        headers.add(HttpHeaders.ACCESS_CONTROL_MAX_AGE, String.valueOf(ObjectUtils.defaultIfEmpty(maxAge, Long.valueOf(360000l))));
        //headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "Content-Type, Authorization, X-Requested-With");
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, ObjectUtils.defaultIfEmpty(allowedHeader, "*"));

        // OPTIONS请求提前返回
        if (ObjectUtils.equals(exchange.getRequest().getMethod(), HttpMethod.OPTIONS)) {
            exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.OK);
            log().debug("==>Cors::{}<==", HttpMethod.OPTIONS);
            return exchange.getResponse().setComplete();
        }
        return null;
    }

    @Override
    default Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log().debug("[Order:{}]==>Cors<==", getOrder());
        Mono<Void> cors = cors(exchange);
        if (cors != null) {
            return cors;
        }
        return chain.filter(exchange);
    }
}
