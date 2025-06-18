package com.actual.combat.basic.gateway.core.filter;

import com.actual.combat.basic.gateway.core.abs.GatewayDistinctResponseHeaderFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @Author yan
 * @Date 2025/6/14 15:00:47
 * @Description
 */
public class SimpleDistinctResponseHeaderFilter implements GatewayDistinctResponseHeaderFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return GatewayDistinctResponseHeaderFilter.super.filter(exchange, chain);
    }
}
