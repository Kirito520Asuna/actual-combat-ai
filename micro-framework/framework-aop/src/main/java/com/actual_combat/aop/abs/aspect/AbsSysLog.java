package com.actual_combat.aop.abs.aspect;

import com.actual_combat.aop.abs.aop.AbsAop;
import com.actual_combat.aop.abs.aop.AopConstants;
import com.actual_combat.aop.all.log.SysLog;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Pointcut;

/**
 * @Author yan
 * @Date 2023/6/1 0001 17:03
 * @Description
 */
public interface AbsSysLog extends AbsAop {
    default SysLog getSysLog(JoinPoint joinPoint) {
        return getAnnotation(joinPoint, SysLog.class);
    }

    @Override
    @Pointcut(value = "@annotation(com.actual_combat.aop.all.log.SysLog)")
    default void Aop() {
    }

    @Override
    default int getOrder() {
        return AopConstants.SysLogOrder;
    }
}
