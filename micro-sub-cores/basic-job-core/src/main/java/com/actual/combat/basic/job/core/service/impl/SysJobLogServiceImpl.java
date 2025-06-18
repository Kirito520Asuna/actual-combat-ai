package com.actual.combat.basic.job.core.service.impl;

import com.actual.combat.job.domain.SysJobLog;
import com.actual.combat.job.mapper.SysJobLogMapper;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.actual.combat.basic.job.core.service.SysJobLogService;

import java.util.List;

@Service
public class SysJobLogServiceImpl extends ServiceImpl<SysJobLogMapper, SysJobLog> implements SysJobLogService{


    @Override
    public List<SysJobLog> selectJobLogList(SysJobLog jobLog) {
        return baseMapper.selectJobLogList(jobLog);
    }

    @Override
    public void cleanJobLog() {
        baseMapper.cleanJobLog();
    }
}
