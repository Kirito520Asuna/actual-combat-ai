package com.actual.combat.redis.config;

import com.actual.combat.redis.abs.ban.BanManager;
import com.actual.combat.redis.abs.config.AbsRedissonConfig;
import com.actual.combat.redis.ban.BanConfiguration;
import com.actual.combat.redis.ban.SimpleBanManager;
import com.actual.combat.redis.service.RedisService;
import com.actual.combat.redis.service.impl.SimpleRedisService;
import jakarta.annotation.Resource;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;


/**
 * @Author yan
 * @Date 2024/5/23 0023 10:02
 * @Description
 */
@Configuration
@EnableAspectJAutoProxy
@EnableCaching // 开启Spring Redis Cache，使用注解驱动缓存机制
@ConditionalOnBean(RedisConfiguration.class)
public class RedissonConfig implements AbsRedissonConfig {

    @Resource
    @Lazy
    private RedisConfiguration configuration;
    @Resource
    @Lazy
    private Environment env;

    @Bean
    public RedissonClient redissonClient() {
        return initRedissonClient();
    }

    public RedissonClient initRedissonClient() {
        return AbsRedissonConfig.super.getRedissonClient(configuration);
    }

    @Bean @Primary
    @SuppressWarnings(value = {"unchecked", "rawtypes"})
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        return initRedisTemplate(connectionFactory);
    }


    @Bean
    @ConditionalOnExpression("${ip.enable:false}")
    @ConditionalOnMissingBean(BanConfiguration.class)
    public BanConfiguration banConfiguration() {
        return new BanConfiguration();
    }

    @Bean
    @ConditionalOnBean({RedissonClient.class, BanConfiguration.class})
    @ConditionalOnMissingBean(BanManager.class)
    public BanManager banManager() {
        return new SimpleBanManager();
    }

    @Bean
    @ConditionalOnMissingBean(RedisService.class)
    public RedisService redisService() {
        log.debug("redisService init");
        return new SimpleRedisService();
    }
}
