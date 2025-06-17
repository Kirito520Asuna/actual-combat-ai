package com.actual.combat.dynamic_datasource.config;

import cn.hutool.extra.spring.SpringUtil;
import com.actual.combat.dynamic_datasource.abs.AbsDynamicDataSource;
import com.baomidou.dynamic.datasource.provider.DynamicDataSourceProvider;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceAutoConfiguration;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @Author yan
 * @Date 2025/5/18 16:11:33
 * @Description
 */

@Configuration
@AutoConfigureBefore({DynamicDataSourceAutoConfiguration.class, SpringBootConfiguration.class})
public class DynamicDataSourceConfig {
    private AbsDynamicDataSource absDynamicDataSource() {
        return SpringUtil.getBean(AbsDynamicDataSource.class);
    }

    @Bean
    public DataSource initDataSource() throws Exception {
        return absDynamicDataSource().dataSource();
    }

    /**
     * 将shardingDataSource放到了多数据源（dataSourceMap）中
     * 注意有个版本的bug，3.1.1版本 不会进入loadDataSources 方法，这样就一直造成数据源注册失败
     */

    @Bean
    public DynamicDataSourceProvider initDynamicDataSourceProvider() throws Exception {
        return absDynamicDataSource().dynamicDataSourceProvider();
    }
}
