package com.actual.combat.task.quartz.core.config;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author yan
 * @DateTime 2024/6/23 14:42:09
 * @Description
 */
@Component
@Slf4j
public class JobStartupRunner implements CommandLineRunner {
    @Resource
    SchedulerConfig schedulerConfig;

    @Override
    public void run(String... args) throws Exception {
        Scheduler scheduler;
        try {
            scheduler = schedulerConfig.scheduler();
            List<QuartzObject> quartzObjectList = QuartzConfig.quartzObjectList;

            quartzObjectList.stream().forEach(quartzObject -> {
                //CronScheduleBuilder scheduleBuilder = quartzObject.getScheduleBuilder();
                TriggerKey triggerKey = quartzObject.getTriggerKey();
                JobKey jobKey = quartzObject.getJobKey();
                Trigger trigger = quartzObject.getTrigger();
                JobDetail jobDetail = quartzObject.getJobDetail();
                try {
                    CronTrigger cronTrigger = (CronTrigger) scheduler.getTrigger(triggerKey);
                    if (null == cronTrigger && jobDetail != null && trigger != null) {
                        scheduler.scheduleJob(jobDetail, trigger);
                        log.info("==> Quartz 创建了job: {} <==", jobDetail.getKey());
                    } else {
                        if (jobDetail != null) {
                            log.info("==> {} job已存在 <==", jobDetail.getKey());
                        } else {
                            log.info("==> {} job未初始化 <==",jobKey);
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            scheduler.start();
        } catch (Exception e) {
            throw e;
        }
    }

}


