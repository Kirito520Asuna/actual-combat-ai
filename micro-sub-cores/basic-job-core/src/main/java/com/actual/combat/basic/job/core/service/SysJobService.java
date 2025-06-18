package com.actual.combat.basic.job.core.service;

import com.actual.combat.job.domain.SysJob;
import com.actual.combat.task.quartz.exception.TaskException;
import com.baomidou.mybatisplus.extension.service.IService;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import java.util.List;

public interface SysJobService extends IService<SysJob> {
    /**
     * 获取Scheduler
     * @return
     */
    default Scheduler getScheduler() {
        return cn.hutool.extra.spring.SpringUtil.getBean(Scheduler.class);
    }

    /**
     * 获取quartz调度器的计划任务
     *
     * @param job 调度信息
     * @return 调度任务集合
     */
    List<SysJob> selectJobList(SysJob job);

    /**
     * 暂停任务
     *
     * @param job 调度信息
     * @return 结果
     */
    boolean pauseJob(SysJob job) throws SchedulerException;

    /**
     * 恢复任务
     *
     * @param job
     * @return
     * @throws SchedulerException
     */

    boolean resumeJob(SysJob job) throws SchedulerException;

    /**
     * 删除任务后，所对应的trigger也将被删除
     *
     * @param job 调度信息
     * @return 结果
     */
    boolean deleteJob(SysJob job) throws SchedulerException;

    /**
     * 批量删除调度信息
     * @param ids
     * @throws SchedulerException
     */
    void deleteJobByIds(List<Long> ids) throws SchedulerException;

    /**
     * 调度任务 状态修改
     * @param job
     * @return
     * @throws SchedulerException
     */
    boolean changeStatus(SysJob job) throws SchedulerException;

    /**
     * 立即执行任务
     *
     * @param job 调度信息
     */
    boolean run(SysJob job) throws SchedulerException;

    /**
     * 新增任务
     *
     * @param job 调度信息 调度信息
     */
    boolean insertJob(SysJob job) throws SchedulerException, TaskException;

    /**
     * 更新任务的时间表达式
     *
     * @param job 调度信息
     */
    boolean updateJob(SysJob job) throws SchedulerException, TaskException;

    /**
     * 校验cron表达式是否有效
     *
     * @param cronExpression 表达式
     * @return 结果
     */
    boolean checkCronExpressionIsValid(String cronExpression);
}
