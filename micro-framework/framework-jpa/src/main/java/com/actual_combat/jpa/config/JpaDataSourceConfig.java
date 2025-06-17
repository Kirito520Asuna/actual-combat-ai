package com.actual_combat.jpa.config;


import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author yan
 * @Date 2024/6/11 0011 11:11
 * @Description
 */
@Configuration
@ConditionalOnExpression("${config.jpa.datasource.prop.customer.enabled:false}")
@EnableTransactionManagement
@EnableJpaRepositories(
        /*事务管理工厂*/
        entityManagerFactoryRef = JpaDataSourceConfig.customerEntityManagerFactoryName,
        /*事务管理*/
        transactionManagerRef = JpaDataSourceConfig.customerTransactionManagerName,
        /*dao地址*/
        basePackages = {JpaDataSourceConfig.customerJpaBasePackage})
public class JpaDataSourceConfig {
    final static String customerDataSourcePrefix = "spring.datasource.customer";
    final static String customerDataSourceName = "customerDataSource";
    final static String customerName = "customer";
    final static String customerEntityManagerFactoryName = "customerEntityManagerFactory";
    final static String customerTransactionManagerName = "customerTransactionManager";
    /*dao地址*/
    final static String customerJpaBasePackage = "com.actual_combat.jpa.customer.repository";
    /*实体类地址*/
    final static String customerPackageToScan = "com.actual_combat.jpa.customer.models";
    /*dao地址*/
    final static String[] customerJpaBasePackages = {customerJpaBasePackage};
    /*实体类地址*/
    final static String[] customerPackagesToScan = {customerPackageToScan};

    final static String[] getCustomerJpaBasePackages() {
        String[] packages = JpaDataSourceConfig.customerJpaBasePackages;
        List<String> packageList = Arrays.stream(packages).collect(Collectors.toList());
        packageList.add("com.actual_combat.config");
        packages = packageList.toArray(new String[packageList.size()]);
        return packages;
    }

    //@Primary
    @Bean(name = JpaDataSourceConfig.customerDataSourceName)
    @ConfigurationProperties(prefix = JpaDataSourceConfig.customerDataSourcePrefix)
    @ConditionalOnMissingBean(DataSource.class)
    public DataSource customerDataSource() {
        return DataSourceBuilder.create().build();
    }

    //@Primary
    @Bean(name = JpaDataSourceConfig.customerEntityManagerFactoryName)
    @ConditionalOnMissingBean(LocalContainerEntityManagerFactoryBean.class)
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder, @Qualifier(JpaDataSourceConfig.customerDataSourceName) DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages(JpaDataSourceConfig.customerPackagesToScan)
                .persistenceUnit(JpaDataSourceConfig.customerName)
                .build();
    }

    //@Primary
    @Bean(name = JpaDataSourceConfig.customerTransactionManagerName)
    @ConditionalOnMissingBean(PlatformTransactionManager.class)
    public PlatformTransactionManager customerTransactionManager(@Qualifier(JpaDataSourceConfig.customerEntityManagerFactoryName) EntityManagerFactory customerEntityManagerFactory) {
        return new JpaTransactionManager(customerEntityManagerFactory);
    }
}