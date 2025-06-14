package com.actual_combat.basic.gateway.core.config.bean;

import com.actual_combat.base.core.abs.bean.AbstractBean;
import com.actual_combat.base.core.abs.bean.AbstractGatewayBean;
import com.actual_combat.base.core.constant.ConfigConstant;
import com.actual_combat.base.core.properties.cors.CorsGatewayProperties;
import com.actual_combat.base.core.properties.cors.CorsProperties;
import com.actual_combat.basic.gateway.core.abs.CorsFilter;
import com.actual_combat.basic.gateway.core.abs.GatewayDistinctResponseHeaderFilter;
import com.actual_combat.basic.gateway.core.abs.GatewayDomainsHeaderFilter;
import com.actual_combat.basic.gateway.core.abs.GatewayHttpsToHttpFilter;
import com.actual_combat.basic.gateway.core.filter.SimpleCorsHeaderFilter;
import com.actual_combat.basic.gateway.core.filter.SimpleDistinctResponseHeaderFilter;
import com.actual_combat.basic.gateway.core.filter.SimpleDomainsHeaderFilter;
import com.actual_combat.basic.gateway.core.filter.SimpleHttpsToHttpFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author yan
 * @Date 2025/6/14 14:11:17
 * @Description
 */
@ConditionalOnMissingBean(AbstractGatewayBean.class)
@Configuration
public class BeanGatewayConfig implements AbstractGatewayBean {

    @Bean
    @ConditionalOnMissingBean(GatewayDomainsHeaderFilter.class)
    public GatewayDomainsHeaderFilter gatewayDomainsHeaderFilter() {
        return new SimpleDomainsHeaderFilter();
    }

    @Bean
    @ConditionalOnMissingBean(GatewayDistinctResponseHeaderFilter.class)
    public GatewayDistinctResponseHeaderFilter gatewayDistinctResponseHeaderFilter() {
        return new SimpleDistinctResponseHeaderFilter();
    }

    @Bean
    @ConditionalOnExpression("${" + ConfigConstant.GATEWAY_ENABLED_CONFIG + ": true}")
    @ConditionalOnMissingBean(CorsProperties.class)
    public CorsProperties corsProperties() {
        return new CorsProperties();
    }

    @Bean
    @ConditionalOnMissingBean(CorsGatewayProperties.class)
    public CorsGatewayProperties corsGatewayProperties() {
        return new CorsGatewayProperties();
    }

    @Bean
    @ConditionalOnBean(CorsProperties.class)
    @ConditionalOnMissingBean(CorsFilter.class)
    public CorsFilter corsFilter() {
        return new SimpleCorsHeaderFilter();
    }

    @Bean
    @ConditionalOnExpression("${" + ConfigConstant.GATEWAY_CORS_HTTPS_TO_HTTP_FILTER_PROP_CONFIG + ":false}")
    @ConditionalOnMissingBean(GatewayHttpsToHttpFilter.class)
    public GatewayHttpsToHttpFilter httpsToHttpFilter() {
        return new SimpleHttpsToHttpFilter();
    }

}
