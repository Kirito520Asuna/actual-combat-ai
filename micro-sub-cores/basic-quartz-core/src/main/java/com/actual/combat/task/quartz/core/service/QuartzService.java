package com.actual.combat.task.quartz.core.service;

import cn.hutool.extra.spring.SpringUtil;
import com.actual.combat.task.quartz.core.job.DynamicQuartzJob;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author yan
 * @Date 2024/12/13 22:10:19
 * @Description
 */
public interface QuartzService {
    String jobFormat = "Job Name: %s, Group: %s, Trigger: %s, Next Fire Time: %s";

    default Scheduler getScheduler() {
        return SpringUtil.getBean(Scheduler.class);
    }

    /**
     * 获取所有定时任务列表
     *
     * @return 任务信息列表
     * @throws SchedulerException
     */
    default List<String> listJobs() throws SchedulerException {
        Scheduler scheduler = getScheduler();
        List<String> jobDetails = new ArrayList<>();

        // 获取所有任务组
        for (String groupName : scheduler.getJobGroupNames()) {
            // 获取任务组中的所有任务
            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
                List<String> strList = triggers.stream().map(trigger -> {
                    String jobInfo = String.format(
                            jobFormat,
                            jobKey.getName(), jobKey.getGroup(),
                            trigger.getKey(),
                            trigger.getNextFireTime() // 下一次触发时间
                    );
                    return jobInfo;
                }).collect(Collectors.toList());

                jobDetails.addAll(strList);
            }
        }
        return jobDetails;
    }

    /**
     * 添加定时任务
     *
     * @param jobName        任务名称
     * @param jobGroup       任务组
     * @param cronExpression Cron表达式
     * @param message        自定义参数
     * @throws SchedulerException
     */
    default void addJob(String jobName, String jobGroup, String cronExpression, String message) throws SchedulerException {
        // 定义任务
        JobDetail jobDetail = JobBuilder.newJob(DynamicQuartzJob.class)
                .withIdentity(jobName, jobGroup)
                .usingJobData("message", message) // 传递参数
                .build();
        // 定义触发器
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
        CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(jobName, jobGroup)
                .withSchedule(scheduleBuilder)
                .build();
        Scheduler scheduler = getScheduler();
        // 调度任务
        scheduler.scheduleJob(jobDetail, trigger);
        // 启动调度器（如果未启动）
        if (!scheduler.isStarted()) {
            scheduler.start();
        }
    }

    /**
     * 删除任务
     *
     * @param jobName  任务名称
     * @param jobGroup 任务组
     * @throws SchedulerException
     */
    default void deleteJob(String jobName, String jobGroup) throws SchedulerException {
        JobKey jobKey = new JobKey(jobName, jobGroup);
        Scheduler scheduler = getScheduler();
        scheduler.deleteJob(jobKey);
    }
}
