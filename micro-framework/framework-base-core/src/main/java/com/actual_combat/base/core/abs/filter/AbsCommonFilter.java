package com.actual_combat.base.core.abs.filter;

import com.actual_combat.base.core.abs.order.FilterOrderConstants;
import org.springframework.core.Ordered;

/**
 * @Author yan
 * @Date 2025/6/11 19:39:13
 * @Description
 */
public interface AbsCommonFilter extends Ordered {
    @Override
    default int getOrder() {
        return FilterOrderConstants.BaseOrder;
    }
}
