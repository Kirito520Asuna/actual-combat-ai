package com.actual.combat.basic.gateway.core.abs;

import com.actual_combat.aop.abs.bean.AbsBean;
import com.actual_combat.basic.result.Result;
import lombok.Data;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * @Author yan
 * @Date 2025/6/14 14:49:31
 * @Description
 */
public interface HomeMvc extends AbsBean {

    @Data
    class RoutePath {
        // 路径
        private String path;
        // 摘要
        private String summary;
        // 描述
        private String description;
    }

    // 获取路由
    RouteLocator fetchRouteLocator();

    //获取首页
    default Stream<RoutePath> fetchHomePage(RouteLocator routeLocator) {
        log().debug("fetchHomePage");
        Flux<Route> routes = routeLocator.getRoutes();
        List<RoutePath> routePaths = new ArrayList<>();
        routes.subscribe(route -> {
            RoutePath routePath = new RoutePath();
            String needParse = route.getPredicate().toString();
            String service = needParse.substring(needParse.indexOf("/"), needParse.lastIndexOf("/"));
            //String path = route.getMetadata().get("document-uri") == null ? service.concat("/swagger-ui/index.html") : service.concat((String) route.getMetadata().get("document-uri"));
            String path = route.getMetadata().get("document-uri") == null ? service.concat("/swagger-ui.html") : service.concat((String) route.getMetadata().get("document-uri"));
            Object summary = route.getMetadata().get("summary") == null ? "" : route.getMetadata().get("summary");
            Object description = route.getMetadata().get("description") == null ? "" : route.getMetadata().get("description");
            routePath.setPath(path);
            routePath.setSummary(summary.toString());
            routePath.setDescription(description.toString());
            routePaths.add(routePath);
        });
        return routePaths.stream().filter(routePath -> !routePath.getSummary().isEmpty());

    }

    // 获取首页
    default Stream<RoutePath> fetchHomePage() {
        return fetchHomePage(fetchRouteLocator());
    }

    //处理路由
    default Result<?> homePage() {
        return Result.ok(fetchHomePage());
    }

    //异步处理路由
    default Mono<Result<?>> homePageMono() {
        return Mono.just(homePage());
    }
}
