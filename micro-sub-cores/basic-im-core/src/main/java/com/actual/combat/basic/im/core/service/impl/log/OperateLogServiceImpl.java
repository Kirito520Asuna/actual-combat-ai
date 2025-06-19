package com.actual.combat.basic.im.core.service.impl.log;

import com.actual.combat.aop.abs.service.AbsOperateLogService;
import com.actual.combat.aop.pojo.OperateLogInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Author minimalism
 * @Date 2023/10/31 0031 15:22
 * @Description
 */
@Slf4j
@Service
public class OperateLogServiceImpl implements AbsOperateLogService {
    @Override
    public void createOperateLog(OperateLogInfo operateLog) {
        AbsOperateLogService.super.createOperateLog(operateLog);
    }

    @Override
    public void updateOperateLog(OperateLogInfo operateLog) {
        AbsOperateLogService.super.updateOperateLog(operateLog);
    }

}
