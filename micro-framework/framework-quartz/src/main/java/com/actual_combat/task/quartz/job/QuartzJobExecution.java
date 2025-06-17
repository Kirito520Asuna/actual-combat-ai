package com.actual_combat.task.quartz.job;


import com.actual_combat.task.quartz.utils.JobInvokeUtil;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;

import java.util.Map;

/**
 * 定时任务允许并发
 */
public class QuartzJobExecution extends AbstractQuartzJob {
    @Override
    protected void doExecute(JobExecutionContext context, Map<String, Object> jobMap) throws Exception {
        JobInvokeUtil.invokeMethod(jobMap);
    }
}
