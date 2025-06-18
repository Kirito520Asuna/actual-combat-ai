package com.actual.combat.basic.job.core.service;

import com.actual.combat.job.domain.SysJobLog;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SysJobLogService extends IService<SysJobLog>{

    /**
     * 查询定时任务日志列表
     * @param jobLog
     * @return
     */
    List<SysJobLog> selectJobLogList(SysJobLog jobLog);

    /**
     * 清空定时任务日志
     */
    void cleanJobLog();
}
