package com.actual.combat.basic.gateway.core.filter;

import com.actual.combat.basic.gateway.core.abs.CorsFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @Author yan
 * @Date 2025/6/14 15:20:44
 * @Description
 */
public class SimpleCorsHeaderFilter implements CorsFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return CorsFilter.super.filter(exchange, chain);
    }
}
