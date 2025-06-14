package com.actual_combat.nacos.config;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @Author yan
 * @Date 2025/3/11 23:24:31
 * @Description
 */
@Configuration
@Slf4j
public class NaCosConfig {
    @Bean
    public NacosDiscoveryProperties nacosDiscoveryProperties() {
        NacosDiscoveryProperties properties = new NacosDiscoveryProperties();
        int port = properties.getPort();
        if (port == -1) {
            port = SpringUtil.getBean(Environment.class).getProperty("server.port", Integer.class, 8080);
            properties.setPort(port);
        }

        log.debug("[init]-[Bean]::[{}]-[{}]: ", StrUtil.subBefore(getClass().getName(),"$",false), properties.getClass().getName());
        return properties;
    }
}
