package com.actual_combat.aop.abs;

import cn.hutool.json.JSONConfig;
import org.springframework.core.Ordered;

/**
 * @Author yan
 * @Date 2025/5/18 15:38:27
 * @Description
 */
public interface AbsAop extends Ordered,AbsBean {
    JSONConfig JSON_CONFIG = JSONConfig.create().setIgnoreNullValue(false);

    @Override
    default int getOrder() {
        return 100;
    }
}
