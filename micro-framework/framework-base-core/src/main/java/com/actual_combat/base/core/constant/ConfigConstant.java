package com.actual_combat.base.core.constant;

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

    String FULL_PATH="full-path";

    String DOMAINS = "domains";
    String CORS= "cors";
    String GATEWAY= "gateway";

    String AUTH_CONFIG = CONFIG + "." + AUTH;
    String AUTH_SECURITY_CONFIG = AUTH_CONFIG + "." + SECURITY;
    String AUTH_SHIRO_CONFIG = AUTH_CONFIG + "." + SHIRO;
    String AUTH_SECURITY_ANNOTATION_CONFIG = AUTH_SECURITY_CONFIG + "." + ANNOTATION;
    String AUTH_SHIRO_ANNOTATION_CONFIG = AUTH_SHIRO_CONFIG + "." + ANNOTATION;

    String GATEWAY_CONFIG = CONFIG + "." + GATEWAY;
    String GATEWAY_DOMAINS_CONFIG = GATEWAY_CONFIG + "." + DOMAINS;
    String GATEWAY_DOMAINS_NAME_CONFIG = GATEWAY_DOMAINS_CONFIG + "." + NAME;
    String GATEWAY_DOMAINS_FULL_PATH_CONFIG = GATEWAY_DOMAINS_CONFIG + "." + FULL_PATH;

    String GATEWAY_CORS_CONFIG = GATEWAY_CONFIG + "." + CORS;
}
