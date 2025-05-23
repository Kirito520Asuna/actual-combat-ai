package com.actual_combat.aop.all.aspect;

import com.actual_combat.aop.abs.aspect.AbsOperateLog;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Author yan
 * @Date 2025/5/23 21:35:49
 * @Description
 */
@Aspect
@Slf4j
@Component
public class OperateLogAspect implements AbsOperateLog {
    @Value("${operate.log.user.type.name:user_id}")
    private String userTypeName;
    @Override
    public String fetchUserTypeName() {
        return userTypeName;
    }

    @Override
    @Around(value = "Aop()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        return AbsOperateLog.super.around(joinPoint);
    }
}
