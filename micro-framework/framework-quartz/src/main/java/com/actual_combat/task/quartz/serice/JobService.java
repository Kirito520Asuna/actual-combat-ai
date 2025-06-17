package com.actual_combat.task.quartz.serice;

import java.util.Map;

/**
 * @Author yan
 * @Date 2025/4/9 18:58:05
 * @Description
 */
public interface JobService {
    /**
     * 新增任务日志
     *
     * @param jobMap 调度日志信息
     */
    void addJobMap(Map<String, Object> jobMap);
}
