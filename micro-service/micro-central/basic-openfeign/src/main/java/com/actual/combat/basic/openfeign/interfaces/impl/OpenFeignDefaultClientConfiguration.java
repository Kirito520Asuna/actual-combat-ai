package com.actual.combat.basic.openfeign.interfaces.impl;

import com.actual.combat.basic.openfeign.factory.AbsEnum;
import com.actual.combat.basic.openfeign.interfaces.AbsOpenFeignClientConfiguration;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;


/**
 * @Author yan
 * @Date 2024/5/14 0014 10:04
 * @Description
 */
@Getter
@Slf4j
@SuppressWarnings("SpringFacetCodeInspection")
@Component
public class OpenFeignDefaultClientConfiguration implements AbsOpenFeignClientConfiguration {

    @Override
    public AbsEnum getAbstractEnum() {
        return AbsEnum.DEFAULT;
    }

    @Bean("OpenFeignDefaultRequestInterceptor")
    public OpenFeignDefaultRequestInterceptor feignRequestInterceptor() {
        return new OpenFeignDefaultRequestInterceptor();
    }

    @Override
    @PostConstruct
    public void init() {
        AbsOpenFeignClientConfiguration.super.init();
        OPEN_MAP.put(AbsEnum.DEFAULT, getClass());
        log().debug("openMap:{}", OPEN_MAP);
    }

}
