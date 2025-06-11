package com.actual_combat.base.core.abs.api;


import com.actual_combat.base.core.abs.api.core.AbsApiSign;
import com.actual_combat.base.core.abs.filter.AbsCommonFilter;
import com.actual_combat.base.core.abs.order.FilterOrderConstants;
import jakarta.annotation.PostConstruct;

/**
 * @Author yan
 * @Date 2025/3/10 3:31:21
 * @Description
 */
public interface AbsApiFiler extends AbsApiSign, AbsCommonFilter {
    @Override
    default int getOrder() {
        return FilterOrderConstants.ApiOrder;
    }

    @Override
    @PostConstruct
    default void init() {
       log().debug("[Bean]-[ApiFiler]-[init]::[{}]",getAClassName());
    }
}
