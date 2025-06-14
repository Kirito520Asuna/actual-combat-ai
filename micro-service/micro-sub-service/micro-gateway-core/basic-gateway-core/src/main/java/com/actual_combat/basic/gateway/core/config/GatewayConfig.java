package com.actual_combat.basic.gateway.core.config;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

/**
 * @Author yan
 * @Date 2025/6/14 14:08:15
 * @Description
 */
@Configuration
@ConditionalOnMissingBean(GatewayConfig.class)
public class GatewayConfig {
    public static String fetchWebPrefix() {
        String prefix = SpringUtil.getBean(Environment.class).getProperty("server.servlet.context-path", "");
        return prefix;
    }

    /**
     * 过滤 server.servlet.context-path 属性配置的项目路径，防止对后续路由策略产生影响，因为 gateway 网关不支持 servlet
     */
    @Bean
    @Order(-1)
    public WebFilter webFilter() {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getRawPath();
            String prefix = fetchWebPrefix();
            path = prefix != null && !"".equals(prefix)
                    && path.startsWith(prefix)
                    && !path.startsWith(prefix + "/home")
                    && !path.startsWith(prefix + "/api-path")
                    && !path.startsWith(prefix + "/doc.html")
                    && !path.startsWith(prefix + "/swagger-ui.html")
                    ? path.replaceFirst(prefix, "") : path;

            ServerHttpRequest newRequest = request.mutate().path(path).build();
            ServerWebExchange webExchange = exchange.mutate().request(newRequest).build();
            Mono<Void> filter = chain.filter(webExchange);
            return filter;
        };
    }
}
