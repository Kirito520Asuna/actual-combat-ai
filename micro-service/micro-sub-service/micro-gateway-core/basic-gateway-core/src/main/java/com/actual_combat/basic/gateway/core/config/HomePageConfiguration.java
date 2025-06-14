package com.actual_combat.basic.gateway.core.config;

import com.actual_combat.basic.gateway.core.abs.HomePage;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * @Author yan
 * @Date 2025/6/14 14:47:02
 * @Description
 */
@Profile(value = {"dev", "test"})
@ConditionalOnMissingBean(HomePage.class)
@Configuration(proxyBeanMethods = false)
public class HomePageConfiguration implements HomePage {

    @Override
    @PostConstruct
    public void init() {
        HomePage.super.init();
    }

    @Override
    public String fetchPath() {
        return GatewayConfig.fetchWebPrefix();
    }

    @Bean
    @Override
    public RouterFunction<ServerResponse> homeRoute(@Value("classpath:/static/index-${spring.profiles.active}.html")final Resource index) {
        return HomePage.super.homeRoute(index);
    }

    @Bean
    @Override
    public RouterFunction<ServerResponse> staticResourceLocator() {
        return HomePage.super.staticResourceLocator();
    }
}
