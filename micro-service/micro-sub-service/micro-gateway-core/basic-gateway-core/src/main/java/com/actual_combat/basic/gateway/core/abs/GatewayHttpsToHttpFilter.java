package com.actual_combat.basic.gateway.core.abs;

import com.actual_combat.aop.abs.bean.AbsBean;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * @Author yan
 * @Date 2025/6/14 15:26:07
 * @Description
 */
public interface GatewayHttpsToHttpFilter extends GlobalFilter, Ordered, AbsBean {

    @Override
    default int getOrder() {
        return GatewayFilterOrder.HTTPS_TO_HTTP_ORDER;
    }

    default void httpsToHttp(URI originalUri, ServerHttpRequest.Builder mutate, String forwardedUri) {
        if (forwardedUri != null && forwardedUri.startsWith("https")) {
            try {
                URI mutatedUri = new URI("http",
                        originalUri.getUserInfo(),
                        originalUri.getHost(),
                        originalUri.getPort(),
                        originalUri.getPath(),
                        originalUri.getQuery(),
                        originalUri.getFragment());
                mutate.uri(mutatedUri);
            } catch (Exception e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }
    }

    @Override
    default Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log().debug("{}@Https-to-Http&Filter&Order==>[{}]", getAClassName(), getOrder());
        URI originalUri = exchange.getRequest().getURI();
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpRequest.Builder mutate = request.mutate();
        String forwardedUri = request.getURI().toString();
        httpsToHttp(originalUri, mutate, forwardedUri);
        ServerHttpRequest build = mutate.build();
        ServerWebExchange webExchange = exchange.mutate().request(build).build();
        return chain.filter(webExchange);
    }
}
