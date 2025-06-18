package com.actual.combat.basic.gateway.core.abs;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.cloud.gateway.route.RouteLocator;

import java.util.stream.Stream;

/**
 * @Author yan
 * @Date 2025/6/14 15:43:50
 * @Description
 */
public abstract class AbstractHomeMvc implements HomeMvc {
    @Override
    public RouteLocator fetchRouteLocator() {
        return SpringUtil.getBean(RouteLocator.class);
    }

    @Override
    public Stream<RoutePath> fetchHomePage() {
        return HomeMvc.super.fetchHomePage();
    }
}
