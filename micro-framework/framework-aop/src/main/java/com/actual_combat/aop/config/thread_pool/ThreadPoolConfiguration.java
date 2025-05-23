package com.actual_combat.aop.config.thread_pool;

import com.actual_combat.aop.utils.thread.AopThreadMdcUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;

/**
 * @Author yan
 * @Date 2025/5/24 00:15:14
 * @Description
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@SuperBuilder
@AllArgsConstructor
@Configuration
@ConditionalOnExpression("${thread-pool.config.enabled:false}")
@ConfigurationProperties(prefix = "thread-pool.config")
public class ThreadPoolConfiguration {
    public static final String DEFAULT_TASK_EXECUTOR = "taskExecutor";
    public static final String DEFAULT_ASYNC_EXECUTOR = "asyncExecutor";
    public static final String GLOBAL_THREAD_POOL_TASK_EXECUTOR = "GLOBAL_THREAD_POOL_TASK_EXECUTOR";
    //是否启用
    boolean enabled;
    //线程池名的前缀：设置好了之后可以方便我们定位处理任务所在的线程池
    String threadNamePrefix = "Global-Task-"; // 线程名前缀
    /**
     * corePoolSize：核心线程数，默认 1。
     * maxPoolSize：最大线程数，默认 Integer.MAX_VALUE。
     * keepAliveSeconds：非核心线程空闲存活时间（秒），默认 60。
     * queueCapacity：任务队列容量，默认 Integer.MAX_VALUE。
     * allowCoreThreadTimeOut：是否允许核心线程超时，默认 false。
     * prestartAllCoreThreads：是否预启动所有核心线程，默认 false。
     */
    int corePoolSize = 5;
    int maxPoolSize = 5;
    int queueCapacity = 500;
    int keepAliveSeconds = 60;
    boolean allowCoreThreadTimeOut;
    boolean prestartAllCoreThreads;

    /**
     * @param executor
     */
    public static void MDCTaskDecorator(ThreadPoolTaskExecutor executor) {
        // 设置任务装饰器，以传递 MDC
        executor.setTaskDecorator(r -> {
            // 包装任务以传递 MDC
            Map<String, String> mdcContext = AopThreadMdcUtil.getCopyOfContextMap();
            return () -> {
                try {
                    if (mdcContext != null) {
                        // 设置 MDC 上下文
                        AopThreadMdcUtil.setContextMap(mdcContext);
                    }
                    // 执行任务
                    r.run();
                } finally {
                    // 清除 MDC 上下文
                    AopThreadMdcUtil.clear();
                }
            };
        });
    }
}
