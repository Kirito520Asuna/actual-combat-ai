package com.actual.combat.task.quartz.core.job;

import com.actual.combat.task.quartz.core.dstributed.DistributedJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

/**
 * @Author yan
 * @Date 2024/12/13 22:04:47
 * @Description
 */
@Slf4j
@Component
public class DynamicQuartzJob extends DistributedJob {


    @Override
    public void executeInternal(JobExecutionContext context) throws JobExecutionException {
        String message = context.getJobDetail().getJobDataMap().getString("message");
        log.info("DynamicQuartzJob executeInternal message:{}", message);
    }
}
