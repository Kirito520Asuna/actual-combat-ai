package com.actual_combat.easy.elasticsearch.config;

import com.actual_combat.easy.elasticsearch.service.EasyElasticsearchService;
import com.actual_combat.easy.elasticsearch.service.impl.SimpleEasyElasticsearchService;
import org.dromara.easyes.spring.annotation.EsMapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author yan
 * @Date 2024/8/13 0013 13:22:37
 * @Description
 */
@Configuration
@EsMapperScan("com.actual_combat.elasticsearch")
public class EasyElasticsearchConfig {

    @Bean
    @ConditionalOnMissingBean(EasyElasticsearchService.class)
    public EasyElasticsearchService easyElasticsearchService() {
        return new SimpleEasyElasticsearchService();
    }
}
