package com.actual_combat.dynamic_datasource.abs.Impl;

import com.actual_combat.dynamic_datasource.abs.AbsDynamicDataSource;
import com.actual_combat.dynamic_datasource.config.DynamicDataSourceConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

/**
 * @Author yan
 * @Date 2025/5/18 16:20:37
 * @Description
 */

@Service
@ConditionalOnMissingBean(AbsDynamicDataSource.class)
@ConditionalOnBean(DynamicDataSourceConfig.class)
public class DefaultDynamicDataSource implements AbsDynamicDataSource {
}
