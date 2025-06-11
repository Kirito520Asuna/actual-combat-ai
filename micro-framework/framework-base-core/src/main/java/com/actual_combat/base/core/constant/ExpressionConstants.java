package com.actual_combat.base.core.constant;

/**
 * @Author yan
 * @Date 2024/11/12 上午12:01:50
 * @Description
 */
public class ExpressionConstants {
    /**
     * Expression表达式
     */
    public static final String addExpression = " &&! ";
    public static final String TRUE = "true";
    public static final String FALSE = "false";
    public static final String colon = ":";
    public static final String spot = ".";
    public static final String prefix = "${";
    public static final String suffix = "}";
    public static final String authorizationShiro = "authorization.shiro.enable";
    public static final String authorizationSecurity = "authorization.security.enable";
    public static final String openFilter = "common.openFilter";
    public static final String openInterceptor = "common.openInterceptor";
    public static final String prefixAndSuffix = "${%s}";

    public static String formatPrefixAndSuffix(Object key) {
        return String.format(prefixAndSuffix, key);
    }

    /**
     * Interceptor 和 Filter 配置
     */

    //"${common.jwt.openFilter:true}"

    public static final String filterExpression = prefix + openFilter + colon + TRUE + suffix;
    //"${common.jwt.openInterceptor:false}"
    public static final String interceptorExpression = prefix + openInterceptor + colon + FALSE + suffix;
    //$"{common.jwt.openFilter:true} &&! ${common.jwt.openInterceptor:false}}"
    public static final String filterAllExpression = filterExpression + addExpression + interceptorExpression;
    //"${common.jwt.openInterceptor:false} &&! ${common.jwt.openFilter:true}"
    public static final String interceptorAllExpression = interceptorExpression + addExpression + filterExpression;

    /**
     * Config 和 Filter 配置
     */
    public static final String allowedOriginPatternsExpression = "${cors.allowedOriginPatterns:*}";
    //"${cors.config:true}"
    public static final String corsConfigExpression = "${cors.config:true}";
    //"${cors.filter:true}"
    public static final String corsFilte = "cors.filter";
    public static final String corsFilterExpression = prefix + corsFilte + colon + TRUE + suffix;
    //"${cors.config:true} &&! ${cors.filter:true}"
    public static final String corsConfigAllExpression = corsConfigExpression + addExpression + corsFilterExpression;
    /**
     * authorization
     */


    //"${authorization.shiro.enable:false}"
    public static final String authorizationShiroExpression = prefix + authorizationShiro + colon + FALSE + suffix;
    //"${authorization.security.enable:true}"
    public static final String authorizationSecurityExpression = prefix + authorizationSecurity + colon + TRUE + suffix;
    //"${authorization.shiro.enable:false} && !${authorization.security.enable:true}"
    public static final String authorizationShiroAllExpression = authorizationShiroExpression + addExpression + authorizationSecurityExpression;
    //"${authorization.security.enable:true} && !${authorization.shiro.enable:false}"
    public static final String authorizationSecurityAllExpression = authorizationSecurityExpression + addExpression + authorizationShiroExpression;
    //"!(${authorization.shiro.enable:false} || ${authorization.security.enable:true})"
    public static final String authorizationShiroOrSecurityNoAllExpression = "!(" + authorizationShiroExpression + "||" + authorizationSecurityExpression + ")";
    /**
     * api
     */
    public static final String springApplicationNameExpression = "${spring.application.name: }";
    public static final String apiSaltExpression = "${salt.api:API_SALT}";
    public static final String signAsNameExpression = "${asName.sign:sign}";
    public static final String timestampAsNameExpression = "${asName.timestamp:timestamp}";
    public static final String apiPathExpression = "${api.path:/api/}";
    public static final String ipWhitelistExpression = "${ip.whitelist:127.0.0.1}";
    public static final String ipBlackListExpression = "${ip.blacklist: }";
    public static final String contextPathExpression = "${server.servlet.context-path: }";
    public static final String signEnableExpression = "${sign.enable:true}";
    public static final String signMultipleEnableExpression = "${sign.multiple.enable:false}";
    public static final String signTimeOutExpression = "${sign.timeOut:10}";
    public static final String serverPortExpression = "${server.port:8080}";
    /**
     *
     */
    public static final String globalCors = "spring.cloud.gateway.globalcors";
    public static final String corsConfigurations = "cors-configurations";
    public static final String corsAllowedOriginPatterns = "allowedOriginPatterns";
    public static final String corsAllowedOrigins = "allowedOrigins";
    public static final String corsAllowedMethods = "allowedMethods";
    public static final String corsAllowedHeaders = "allowedHeaders";
    public static final String corsAllowCredentials = "allowCredentials";
    public static final String corsMaxAge = "maxAge";
    public static final String corsAllPath = "'[/**]'";
    public static final String corsConfigurationsAllowedOriginPatterns =
            prefix + globalCors + corsConfigurations + spot + corsAllPath + spot
                    + corsAllowedOriginPatterns + suffix;
    public static final String corsConfigurationsAllowedOrigins =
            prefix + globalCors + corsConfigurations + spot + corsAllPath + spot
                    + corsAllowedOrigins + suffix;
    public static final String corsConfigurationsAllowedMethods =
            prefix + globalCors + corsConfigurations + spot + corsAllPath + spot
                    + corsAllowedMethods + suffix;
    public static final String corsConfigurationsAllowedHeaders =
            prefix + globalCors + corsConfigurations + spot + corsAllPath + spot
                    + corsAllowedHeaders + suffix;
    public static final String corsConfigurationsAllowCredentials =
            prefix + globalCors + corsConfigurations + spot + corsAllPath + spot
                    + corsAllowCredentials + suffix;
    public static final String corsConfigurationsMaxAge =
            prefix + globalCors + corsConfigurations + spot + corsAllPath + spot
                    + corsMaxAge + suffix;


}
