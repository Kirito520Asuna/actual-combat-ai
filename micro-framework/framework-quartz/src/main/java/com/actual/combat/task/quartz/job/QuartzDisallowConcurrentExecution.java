package com.actual.combat.task.quartz.job;


import com.actual.combat.task.quartz.utils.JobInvokeUtil;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;

import java.util.Map;

@DisallowConcurrentExecution
public class QuartzDisallowConcurrentExecution extends AbstractQuartzJob {
    @Override
    protected void doExecute(JobExecutionContext context, Map<String, Object> jobMap) throws Exception {
        JobInvokeUtil.invokeMethod(jobMap);
    }
}
