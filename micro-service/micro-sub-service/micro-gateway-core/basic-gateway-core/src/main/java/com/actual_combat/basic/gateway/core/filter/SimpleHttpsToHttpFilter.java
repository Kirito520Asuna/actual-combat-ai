package com.actual_combat.basic.gateway.core.filter;

import com.actual_combat.basic.gateway.core.abs.GatewayHttpsToHttpFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @Author yan
 * @Date 2025/6/14 15:30:47
 * @Description
 */
public class SimpleHttpsToHttpFilter implements GatewayHttpsToHttpFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return GatewayHttpsToHttpFilter.super.filter(exchange, chain);
    }
}
