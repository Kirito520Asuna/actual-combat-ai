package com.actual.combat.job.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.actual.combat.job.domain.SysJobLog;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysJobLogMapper extends BaseMapper<SysJobLog> {
    List<SysJobLog> selectJobLogList(SysJobLog jobLog);

    /**
     *
     * 清空任务日志
     */
    void cleanJobLog();
}