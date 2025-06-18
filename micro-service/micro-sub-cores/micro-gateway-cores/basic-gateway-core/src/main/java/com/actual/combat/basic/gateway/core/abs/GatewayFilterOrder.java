package com.actual.combat.basic.gateway.core.abs;

public interface GatewayFilterOrder {
    int CORS_HEADER_ORDER = -100;
    int GATEWAY_DOMAINS_ORDER = 100;
    int HTTPS_TO_HTTP_ORDER = 200;
    int DISTINCT_RESPONSE_ORDER = 300;
}
