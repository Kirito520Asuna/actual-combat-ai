package com.actual.combat.basic.im.core.config;

import com.actual.combat.basic.im.core.service.impl.datasource.SimpleDynamicDataSource;
import com.actual.combat.dynamic_datasource.abs.AbsDynamicDataSource;
import com.actual.combat.dynamic_datasource.config.DynamicDataSourceConfig;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author yan
 * @Date 2025/6/20 13:01:24
 * @Description
 */
@Configuration
@AutoConfigureBefore(DynamicDataSourceConfig.class)
public class ImConfig {
    @Bean
    @ConditionalOnMissingBean(AbsDynamicDataSource.class)
    public AbsDynamicDataSource dynamicDataSource() {
        return new SimpleDynamicDataSource();
    }
}
