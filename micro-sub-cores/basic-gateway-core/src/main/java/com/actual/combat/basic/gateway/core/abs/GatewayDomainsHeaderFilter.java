package com.actual.combat.basic.gateway.core.abs;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.actual.combat.aop.abs.bean.AbsBean;
import com.actual.combat.aop.utils.gateway.GatewayConstants;
import com.actual.combat.basic.core.constant.ConfigConstant;
import com.actual.combat.basic.utils.object.ObjectUtils;
import com.google.common.collect.Maps;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @Author yan
 * @Date 2025/6/14 13:18:44
 * @Description
 */
public interface GatewayDomainsHeaderFilter extends GlobalFilter,AbsBean, Ordered {
    @Override
    default int getOrder() {
        return GatewayFilterOrder.GATEWAY_DOMAINS_ORDER;
    }

    @Override
    default Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log().debug("[Order:{}]==>GatewayDomains<==", getOrder());
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpRequest.Builder mutate = request.mutate();

        Environment env = SpringUtil.getBean(Environment.class);
        Map<String, String> map = fetchDomains(env);
        map.entrySet().stream().forEach(entry -> {
            String value = entry.getValue();
            String key = entry.getKey();
            if (ObjectUtils.equals(HttpHeaders.ACCEPT_ENCODING, key)) {
                value = "";
            }
            mutate.header(key, value);
        });

        ServerHttpRequest build = mutate.build();

        ServerWebExchange webExchange = exchange.mutate().request(build).build();
        return chain.filter(webExchange);
    }

    default String fetchWebPrefix() {
        return StrUtil.EMPTY;
    }

    default Map<String, String> fetchDomains(Environment env) {
        Map<String, String> map = Maps.newLinkedHashMap();

        String domainsName = env.getProperty(ConfigConstant.GATEWAY_DOMAINS_NAME_CONFIG, "");
        String gatewayDomainsFull = env.getProperty(ConfigConstant.GATEWAY_DOMAINS_FULL_PATH_CONFIG, "");
        String prefix = fetchWebPrefix();

        if (StrUtil.isBlank(domainsName)) {
            log().warn("未配置域名 如需配置请设置==>:{}", ConfigConstant.GATEWAY_DOMAINS_NAME_CONFIG);
        } else if (StrUtil.isBlank(gatewayDomainsFull)) {
            gatewayDomainsFull = domainsName + prefix;
        }
        if (StrUtil.isBlank(gatewayDomainsFull)) {
            log().warn("未配置全域名路径 如需配置请设置==>:{}", ConfigConstant.GATEWAY_DOMAINS_FULL_PATH_CONFIG);
        }

        if (StrUtil.isNotBlank(prefix)) {
            map.put(GatewayConstants.GATEWAY_PREFIX, prefix);
        }

        if (StrUtil.isNotBlank(prefix)) {
            map.put(GatewayConstants.GATEWAY_DOMAINS_NAME, domainsName);
        }

        if (StrUtil.isNotBlank(prefix)) {
            map.put(GatewayConstants.GATEWAY_DOMAINS_ALL_PATHS, gatewayDomainsFull);
        }

        return map;
    }
}
