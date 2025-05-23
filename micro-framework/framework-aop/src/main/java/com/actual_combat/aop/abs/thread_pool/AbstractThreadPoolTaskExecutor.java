package com.actual_combat.aop.abs.thread_pool;

import com.actual_combat.aop.utils.thread.AopThreadMdcUtil;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * @Author yan
 * @Date 2024/5/14 0014 15:00
 * @Description
 */
public abstract class AbstractThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {
    @Override
    public boolean prefersShortLivedTasks() {
        return super.prefersShortLivedTasks();
    }
    @Override
    public void execute(Runnable task) {
        super.execute(AopThreadMdcUtil.wrap(task, AopThreadMdcUtil.getCopyOfContextMap()));
    }
    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return super.submit(AopThreadMdcUtil.wrap(task, AopThreadMdcUtil.getCopyOfContextMap()));
    }
    @Override
    public Future<?> submit(Runnable task) {
        return super.submit(AopThreadMdcUtil.wrap(task, AopThreadMdcUtil.getCopyOfContextMap()));
    }
}
