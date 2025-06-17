package com.actual.combat.aop.all.async;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.annotation.*;
import java.util.concurrent.Executor;

/**
 * @Author yan
 * @Date 2024/11/3 上午2:53:02
 * @Description
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AsyncFuture {
    /**
     * 是否启用
     *
     * @return
     */
    boolean enable() default true;

    /**
     * 是否使用线程池
     *
     * @return
     */
    boolean useExecutor() default false;

    /**
     * 线程池
     * @return
     */
    Class<? extends Executor> executorClass() default ThreadPoolTaskExecutor.class;

    /**
     * 线程池Bean 名称
     * @return
     */
    String executorBeanName() default "";

}
