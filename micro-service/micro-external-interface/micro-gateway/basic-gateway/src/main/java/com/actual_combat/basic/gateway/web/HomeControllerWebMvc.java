package com.actual_combat.basic.gateway.web;

import com.actual_combat.base.result.Result;
import com.actual_combat.basic.gateway.core.abs.HomeMvc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @Author yan
 * @Date 2025/6/14 16:17:04
 * @Description
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@Profile(value = {"dev", "test"})
@RequestMapping(value = "${server.servlet.context-path}")
public class HomeControllerWebMvc implements HomeMvc {

    private final RouteLocator routeLocator;

    @Override
    public RouteLocator fetchRouteLocator() {
        return routeLocator;
    }

    @GetMapping(value = "/api-path")
    public Result<?> homePage() {
        log().debug("homePage");
        return Result.ok(fetchHomePage(routeLocator));
    }

}
