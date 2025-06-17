package com.actual.combat.basic.core.constant;

/**
 * @Author yan
 * @Date 2025/6/13 22:59:23
 * @Description
 */
public interface ConfigConstant {
    String CONFIG = "config";
    String AUTH = "auth";
    String SECURITY = "security";
    String SHIRO = "shiro";
    String ANNOTATION = "annotation";
    String NAME = "name";
    String PROP = "prop";

    String FULL_PATH = "full-path";

    String DOMAINS = "domains";
    String CORS = "cors";
    String GATEWAY = "gateway";
    String FILTER = "filter";

    String ENABLED = "enabled";
    String DEFAULT_FILTER = "default-filter";
    String WEB_FILTER = "web-filter";
    String DISTINCT_HEADERS_FILTER = "distinct-headers-filter";
    String HTTPS_TO_HTTP_FILTER = "https-to-http-filter";

    String AUTH_CONFIG = CONFIG + "." + AUTH;
    String AUTH_SECURITY_CONFIG = AUTH_CONFIG + "." + SECURITY;
    String AUTH_SHIRO_CONFIG = AUTH_CONFIG + "." + SHIRO;
    String AUTH_SECURITY_ANNOTATION_CONFIG = AUTH_SECURITY_CONFIG + "." + ANNOTATION;
    String AUTH_SHIRO_ANNOTATION_CONFIG = AUTH_SHIRO_CONFIG + "." + ANNOTATION;

    String GATEWAY_CONFIG = CONFIG + "." + GATEWAY;
    String GATEWAY_ENABLED_CONFIG = GATEWAY_CONFIG + "." + ENABLED;

    String GATEWAY_DOMAINS_CONFIG = GATEWAY_CONFIG + "." + DOMAINS;
    String GATEWAY_DOMAINS_NAME_CONFIG = GATEWAY_DOMAINS_CONFIG + "." + NAME;
    String GATEWAY_DOMAINS_FULL_PATH_CONFIG = GATEWAY_DOMAINS_CONFIG + "." + FULL_PATH;

    String GATEWAY_CORS_CONFIG = GATEWAY_CONFIG + "." + CORS;
    String GATEWAY_CORS_PROP_CONFIG = GATEWAY_CORS_CONFIG + "." + PROP;
    String GATEWAY_CORS_ENABLED_CONFIG = GATEWAY_CORS_CONFIG + "." + ENABLED;
    String GATEWAY_CORS_DEFAULT_FILTER_PROP_CONFIG = GATEWAY_CORS_PROP_CONFIG + "." + DEFAULT_FILTER;
    String GATEWAY_CORS_WEB_FILTER_PROP_CONFIG = GATEWAY_CORS_PROP_CONFIG + "." + WEB_FILTER;
    String GATEWAY_CORS_DISTINCT_HEADERS_FILTER_PROP_CONFIG = GATEWAY_CORS_PROP_CONFIG + "." + DISTINCT_HEADERS_FILTER;
    String GATEWAY_CORS_HTTPS_TO_HTTP_FILTER_PROP_CONFIG = GATEWAY_CORS_PROP_CONFIG + "." + HTTPS_TO_HTTP_FILTER;

    String CORS_CONFIG = CONFIG + "." + CORS;
    String CORS_PROP_CONFIG = CORS_CONFIG + "." + PROP;
    String CORS_ENABLED_CONFIG = CORS_CONFIG + "." + ENABLED;
}
