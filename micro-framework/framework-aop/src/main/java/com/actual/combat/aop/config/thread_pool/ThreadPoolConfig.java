package com.actual.combat.aop.config.thread_pool;

import cn.hutool.extra.spring.SpringUtil;
import com.actual.combat.aop.pojo.GlobalThreadPoolTaskExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * @Author yan
 * @Date 2024/5/14 0014 16:59
 * @Description
 */
@Configuration
@EnableAsync
@Slf4j @ConditionalOnExpression("${config.thread-pool.enabled:true}")
public class ThreadPoolConfig {
    private static final String DEFAULT_TASK_EXECUTOR = ThreadPoolConfiguration.DEFAULT_TASK_EXECUTOR;
    private static final String DEFAULT_ASYNC_EXECUTOR = ThreadPoolConfiguration.DEFAULT_ASYNC_EXECUTOR;
    private static final String GLOBAL_THREAD_POOL_TASK_EXECUTOR = ThreadPoolConfiguration.GLOBAL_THREAD_POOL_TASK_EXECUTOR;

    // 配置默认异步 TaskExecutor
    @Bean(name = {DEFAULT_TASK_EXECUTOR, DEFAULT_ASYNC_EXECUTOR,GLOBAL_THREAD_POOL_TASK_EXECUTOR}) // 注册为 Spring 默认异步执行器
    public ThreadPoolTaskExecutor globalThreadPoolTaskExecutor() {
        GlobalThreadPoolTaskExecutor executor = new GlobalThreadPoolTaskExecutor();
        ThreadPoolConfiguration threadPoolConfiguration;
        try {
            threadPoolConfiguration = SpringUtil.getBean(ThreadPoolConfiguration.class);
        } catch (Exception e) {
            log.error("ThreadPoolConfiguration not found, use default configuration");
            threadPoolConfiguration = new ThreadPoolConfiguration();
        }
        //核心线程数5：线程池创建时候初始化的线程数
        executor.setCorePoolSize(threadPoolConfiguration.getCorePoolSize());
        //最大线程数5：线程池最大的线程数，只有在缓冲队列满了之后才会申请超过核心线程数的线程
        executor.setMaxPoolSize(threadPoolConfiguration.getMaxPoolSize());
        //缓冲队列500：用来缓冲执行任务的队列
        executor.setQueueCapacity(threadPoolConfiguration.getQueueCapacity());
        //允许线程的空闲时间60秒：当超过了核心线程出之外的线程在空闲时间到达之后会被销毁
        executor.setKeepAliveSeconds(threadPoolConfiguration.getKeepAliveSeconds());
        //线程池名的前缀：设置好了之后可以方便我们定位处理任务所在的线程池
        executor.setThreadNamePrefix(threadPoolConfiguration.getThreadNamePrefix()); // 线程名前缀
        ThreadPoolConfiguration.MDCTaskDecorator(executor);
        executor.initialize();
        return executor;
    }

    /**
     * 声明一个线程池
     *
     * @return 执行器
     */
    //@Bean("Executor")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = SpringUtil.getBean(GLOBAL_THREAD_POOL_TASK_EXECUTOR, ThreadPoolTaskExecutor.class);
        ThreadPoolConfiguration threadPoolConfiguration;
        try {
            threadPoolConfiguration = SpringUtil.getBean(ThreadPoolConfiguration.class);
        } catch (Exception e) {
            log.error("ThreadPoolConfiguration not found, use default configuration");
            threadPoolConfiguration = new ThreadPoolConfiguration();
        }
        //核心线程数5：线程池创建时候初始化的线程数
        executor.setCorePoolSize(threadPoolConfiguration.getCorePoolSize());
        //最大线程数5：线程池最大的线程数，只有在缓冲队列满了之后才会申请超过核心线程数的线程
        executor.setMaxPoolSize(threadPoolConfiguration.getMaxPoolSize());
        //缓冲队列500：用来缓冲执行任务的队列
        executor.setQueueCapacity(threadPoolConfiguration.getQueueCapacity());
        //允许线程的空闲时间60秒：当超过了核心线程出之外的线程在空闲时间到达之后会被销毁
        executor.setKeepAliveSeconds(threadPoolConfiguration.getKeepAliveSeconds());
        //线程池名的前缀：设置好了之后可以方便我们定位处理任务所在的线程池
        executor.setThreadNamePrefix(threadPoolConfiguration.getThreadNamePrefix()); // 线程名前缀
        ThreadPoolConfiguration.MDCTaskDecorator(executor);
        executor.initialize();
        return executor;
    }

}