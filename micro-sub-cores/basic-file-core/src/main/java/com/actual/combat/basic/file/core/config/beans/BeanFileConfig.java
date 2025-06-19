package com.actual.combat.basic.file.core.config.beans;

import com.actual.combat.aop.abs.bean.AbsBean;
import com.actual.combat.basic.file.core.properties.FileProperties;
import com.actual.combat.basic.file.core.storage.clientAbs.AliyunClient;
import com.actual.combat.basic.file.core.storage.clientAbs.LocalClient;
import com.actual.combat.basic.file.core.storage.platform.AliyunOssStorageClient;
import com.actual.combat.basic.file.core.storage.platform.LocalStorageClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author yan
 * @Date 2025/3/9 上午12:40:27
 * @Description
 */
@Configuration
public class BeanFileConfig implements AbsBean {
    @Bean
    public FileProperties.LocalProperties localProperties() {
        return new FileProperties.LocalProperties();
    }

    @Bean
    @ConditionalOnBean(FileProperties.LocalProperties.class)
    public LocalClient localClient() {
        return new LocalStorageClient();
    }

    @Bean
    @ConditionalOnBean(FileProperties.AliyunOssProperties.class)
    public AliyunClient aliyunClient() {
        return new AliyunOssStorageClient();
    }
}
