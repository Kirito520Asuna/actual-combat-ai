package com.actual.combat.basic.job.core.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.actual.combat.basic.utils.bean.CustomBeanUtils;
import com.actual.combat.job.domain.SysJob;
import com.actual.combat.job.mapper.SysJobMapper;
import com.actual.combat.task.quartz.exception.TaskException;
import com.actual.combat.task.quartz.schedule.ScheduleConstants;
import com.actual.combat.task.quartz.utils.CronUtils;
import com.actual.combat.task.quartz.utils.ScheduleUtils;
import com.google.common.collect.Maps;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.actual.combat.basic.job.core.service.SysJobService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SysJobServiceImpl extends ServiceImpl<SysJobMapper, SysJob> implements SysJobService {
    @Resource
    private Scheduler scheduler;

    @Override
    public Scheduler getScheduler() {
        if (scheduler == null) {
            scheduler = SysJobService.super.getScheduler();
        }
        return scheduler;
    }

    @PostConstruct
    public void init() throws SchedulerException, TaskException {
        Scheduler scheduler = getScheduler();
        scheduler.clear();
        List<SysJob> jobList = list();

        List<Map<String, Object>> jobMapList = jobList.stream()
                .map(job -> {

                    Map<String, Object> jobMap = Maps.newLinkedHashMap();
                    CustomBeanUtils.copyPropertiesIgnoreNull(job, jobMap);
                    return jobMap;
                }).collect(Collectors.toList());

        for (Map<String, Object> jobMap : jobMapList) {
            ScheduleUtils.createScheduleJob(scheduler, jobMap);
        }
    }

    /**
     * 获取quartz调度器的计划任务
     *
     * @param job 调度信息
     * @return 调度任务集合
     */
    @Override
    public List<SysJob> selectJobList(SysJob job) {
        return baseMapper.selectJobList(job);
    }

    /**
     * 暂停任务
     *
     * @param job 调度信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean pauseJob(SysJob job) throws SchedulerException {
        Long jobId = job.getJobId();
        String jobGroup = job.getJobGroup();
        job.setStatus(ScheduleConstants.Status.PAUSE.getValue());
        boolean update = updateById(job);
        if (update) {
            getScheduler().pauseJob(ScheduleUtils.buildDefaultJobKey(jobId, jobGroup));
        }
        return update;
    }

    /**
     * 恢复任务
     *
     * @param job
     * @return
     * @throws SchedulerException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean resumeJob(SysJob job) throws SchedulerException {
        Long jobId = job.getJobId();
        String jobGroup = job.getJobGroup();
        job.setStatus(ScheduleConstants.Status.NORMAL.getValue());
        boolean update = updateById(job);
        if (update) {
            SpringUtil.getBean(Scheduler.class).resumeJob(ScheduleUtils.buildDefaultJobKey(jobId, jobGroup));
        }
        return update;
    }

    /**
     * 删除任务后，所对应的trigger也将被删除
     *
     * @param job 调度信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteJob(SysJob job) throws SchedulerException {
        Long jobId = job.getJobId();
        boolean remove = removeById(jobId);
        if (remove) {
            getScheduler().deleteJob(ScheduleUtils.buildDefaultJobKey(jobId, job.getJobGroup()));
        }
        return remove;
    }

    /**
     * 批量删除调度信息
     *
     * @param ids
     * @throws SchedulerException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteJobByIds(List<Long> ids) throws SchedulerException {
        for (Long id : ids) {
            SysJob job = getById(id);
            deleteJob(job);
        }
    }

    /**
     * 调度任务 状态修改
     *
     * @param job
     * @return
     * @throws SchedulerException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean changeStatus(SysJob job) throws SchedulerException {
        String status = job.getStatus();
        boolean change = false;
        if (ScheduleConstants.Status.NORMAL.getValue().equals(status)) {
            change = resumeJob(job);
        } else if (ScheduleConstants.Status.PAUSE.getValue().equals(status)) {
            change = pauseJob(job);
        }
        return change;
    }

    /**
     * 立即执行任务
     *
     * @param job 调度信息
     * @return
     * @throws SchedulerException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean run(SysJob job) throws SchedulerException {
        boolean run = false;
        Long jobId = job.getJobId();
        String jobGroup = job.getJobGroup();
        SysJob properties = getById(jobId);
        //参数
        JobDataMap dataMap = new JobDataMap();
        dataMap.put(ScheduleConstants.TASK_PROPERTIES, properties);
        Scheduler scheduler = getScheduler();
        JobKey jobKey = ScheduleUtils.buildDefaultJobKey(jobId, jobGroup);
        if (scheduler.checkExists(jobKey)) {
            run = true;
            scheduler.triggerJob(jobKey, dataMap);
        }
        return run;
    }

    /**
     * 新增任务
     *
     * @param job 调度信息 调度信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertJob(SysJob job) throws SchedulerException, TaskException {
        job.setStatus(ScheduleConstants.Status.PAUSE.getValue());
        boolean save = save(job);
        if (save) {
            Map<String, Object> jobMap = Maps.newLinkedHashMap();
            CustomBeanUtils.copyPropertiesIgnoreNull(job, jobMap);
            ScheduleUtils.createScheduleJob(getScheduler(), jobMap);
        }
        return save;
    }


    /**
     * 更新任务的时间表达式
     *
     * @param job 调度信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateJob(SysJob job) throws SchedulerException, TaskException {
        SysJob properties = getById(job.getJobId());
        boolean update = updateById(job);
        if (update) {
            updateSchedulerJob(job, properties.getJobGroup());
        }
        return update;
    }

    /**
     * 更新任务
     *
     * @param job      任务对象
     * @param jobGroup 任务组名
     */
    public void updateSchedulerJob(SysJob job, String jobGroup) throws SchedulerException, TaskException {
        Long jobId = job.getJobId();
        // 判断是否存在
        JobKey jobKey = ScheduleUtils.buildDefaultJobKey(jobId, jobGroup);
        Scheduler scheduler = getScheduler();
        if (scheduler.checkExists(jobKey)) {
            // 防止创建时存在数据问题 先移除，然后在执行创建操作
            scheduler.deleteJob(jobKey);
        }
        Map<String, Object> jobMap = Maps.newLinkedHashMap();
        CustomBeanUtils.copyPropertiesIgnoreNull(job, jobMap);
        ScheduleUtils.createScheduleJob(scheduler, jobMap);
    }

    /**
     * 校验cron表达式是否有效
     *
     * @param cronExpression 表达式
     * @return 结果
     */
    @Override
    public boolean checkCronExpressionIsValid(String cronExpression) {
        return CronUtils.isValid(cronExpression);
    }
}
