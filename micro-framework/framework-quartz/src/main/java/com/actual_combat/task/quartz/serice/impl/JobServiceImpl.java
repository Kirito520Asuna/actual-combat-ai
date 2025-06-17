package com.actual_combat.task.quartz.serice.impl;

import com.actual_combat.task.quartz.serice.JobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @Author yan
 * @Date 2025/4/9 19:00:04
 * @Description
 */
@Service
@Slf4j
public class JobServiceImpl implements JobService {
    @Override
    public void addJobMap(Map<String, Object> jobMap) {
        log.warn("jobMap:{}", jobMap);
    }
}
