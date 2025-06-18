package com.actual.combat.basic.gateway.core.filter;

import com.actual.combat.basic.gateway.core.abs.GatewayDomainsHeaderFilter;
import com.actual.combat.basic.gateway.core.config.GatewayConfig;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @Author yan
 * @Date 2025/6/14 14:06:39
 * @Description
 */
public class SimpleDomainsHeaderFilter implements GatewayDomainsHeaderFilter {
    @Override
    public String fetchWebPrefix() {
        return GatewayConfig.fetchWebPrefix();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return GatewayDomainsHeaderFilter.super.filter(exchange, chain);
    }
}
