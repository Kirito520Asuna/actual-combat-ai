package com.actual_combat.basic.openfeign.interfaces;

import com.actual_combat.aop.abs.bean.AbsBean;
import com.google.common.collect.Maps;
import com.actual_combat.basic.openfeign.factory.AbsEnum;
import feign.Target;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.net.URI;
import java.util.Map;

/**
 * @author yan
 * @date 2024/5/21 6:58
 */
public interface AbsOpenFeignClientConfiguration extends AbsBean {
    Map<AbsEnum,Class<? extends AbsOpenFeignClientConfiguration>> OPEN_MAP = Maps.newConcurrentMap();

    /**
     * Feign url 重构
     *
     * @param feignTarget
     * @param originalUri
     * @return
     */
    default String doReconstructURL(Target<?> feignTarget, URI originalUri) {
        DefaultUriBuilderFactory defaultUriBuilderFactory = new DefaultUriBuilderFactory();
        defaultUriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);
        return defaultUriBuilderFactory.uriString(new StringBuffer(feignTarget.url()).append(originalUri).toString()).replaceQuery(null).build().toString();
    }

    default AbsEnum getAbstractEnum(){
        return AbsEnum.DEFAULT;
    }

    default String getApiSalt(){
        return getAbstractEnum().getApiSalt().getSalt();
    }
    default String getSignAsName(){
        return getAbstractEnum().getApiSalt().getSignAsName();
    }

    default String getTimestampAsName(){
        return getAbstractEnum().getApiSalt().getTimestampAsName();
    }

}
