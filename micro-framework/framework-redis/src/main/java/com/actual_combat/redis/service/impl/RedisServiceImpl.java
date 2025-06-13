package com.actual_combat.redis.service.impl;

import com.actual_combat.redis.service.RedisService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnMissingBean(RedisService.class)
public class RedisServiceImpl implements RedisService {
}
