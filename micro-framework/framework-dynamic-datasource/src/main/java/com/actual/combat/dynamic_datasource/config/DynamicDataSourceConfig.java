package com.actual.combat.dynamic_datasource.config;

import cn.hutool.extra.spring.SpringUtil;
import com.actual.combat.dynamic_datasource.abs.AbsDynamicDataSource;
import com.actual.combat.dynamic_datasource.abs.Impl.DefaultDynamicDataSource;
import com.baomidou.dynamic.datasource.creator.DefaultDataSourceCreator;
import com.baomidou.dynamic.datasource.provider.DynamicDataSourceProvider;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceAssistConfiguration;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceAutoConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * @Author yan
 * @Date 2025/5/18 16:11:33
 * @Description
 */

@Configuration
@AutoConfigureBefore({DynamicDataSourceAutoConfiguration.class, SpringBootConfiguration.class})
public class DynamicDataSourceConfig {
    @Bean
    @ConditionalOnMissingBean(AbsDynamicDataSource.class)
    public AbsDynamicDataSource dynamicDataSource() {
        return new DefaultDynamicDataSource();
    }
    private AbsDynamicDataSource absDynamicDataSource() {
        return SpringUtil.getBean(AbsDynamicDataSource.class);
    }

    @Bean
    @ConditionalOnMissingBean(DataSource.class)
    public DataSource initDataSource() throws Exception {
        return absDynamicDataSource().dataSource();
    }

    /**
     * 将shardingDataSource放到了多数据源（dataSourceMap）中
     * 注意有个版本的bug，3.1.1版本 不会进入loadDataSources 方法，这样就一直造成数据源注册失败
     */

    @ConditionalOnMissingBean({DynamicDataSourceAssistConfiguration.class,DynamicDataSourceProvider.class})
    //@Bean @Primary
    public DynamicDataSourceProvider initDynamicDataSourceProvider(DefaultDataSourceCreator defaultDataSourceCreator) throws Exception {
        return absDynamicDataSource().dynamicDataSourceProvider(defaultDataSourceCreator);
    }
}
