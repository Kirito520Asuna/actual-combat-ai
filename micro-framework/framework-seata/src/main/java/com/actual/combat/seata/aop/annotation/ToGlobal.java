package com.actual.combat.seata.aop.annotation;

import java.lang.annotation.*;

/**
 * @Author yan
 * @Date 2025/4/25 20:34:23
 * @Description
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ToGlobal {
    /**
     * 是否开启本地事务转全局事务
     * @return
     */
    boolean open() default true;
}
