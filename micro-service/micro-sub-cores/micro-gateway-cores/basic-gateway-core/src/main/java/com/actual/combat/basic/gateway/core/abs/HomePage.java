package com.actual.combat.basic.gateway.core.abs;

import com.actual_combat.aop.abs.bean.AbsBean;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * @Author yan
 * @Date 2025/6/14 14:42:59
 * @Description
 */
public interface HomePage extends WebFluxConfigurer, AbsBean {

    @Override
    @PostConstruct
    default void init() {
        log().debug("init HomePage==>{}", getAClassName());
    }

    /**
     * 首页
     *
     * @return
     */
    default String home() {
        return "/home";
    }

    /**
     * 首页路径
     *
     * @return
     */
    String fetchPath();

    /**
     * 首页
     *
     * @param index
     * @return
     */
    default RouterFunction<ServerResponse> homeRoute(@Value("classpath:/static/index-${spring.profiles.active}.html") final Resource index) {
        String path = fetchPath() + home();
        log().debug("path:{}", path);
        return RouterFunctions.route(RequestPredicates.GET(path), request -> ServerResponse.ok().contentType(MediaType.TEXT_HTML).bodyValue(index));
    }

    /**
     * 静态资源
     *
     * @return
     */
    default RouterFunction<ServerResponse> staticResourceLocator() {
        return RouterFunctions.resources(fetchPath() + "/assert/**", new ClassPathResource("/static/**"));
    }
}
