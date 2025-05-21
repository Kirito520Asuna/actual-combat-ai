package com.actual_combat.aop.all.aspect;

import com.actual_combat.aop.abs.aspect.AbsAsyncFuture;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @Author yan
 * @Date 2024/11/3 上午2:54:17
 * @Description
 */
@Aspect
@Slf4j
@Component
public class AsyncFutureAspect implements AbsAsyncFuture {


    /**
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Override
    @Around(value = "Aop()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        return AbsAsyncFuture.super.around(joinPoint);
    }

}
