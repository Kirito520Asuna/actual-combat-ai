package com.actual_combat.aop.all.aviator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author yan
 * @Date 2024/11/12 下午2:53:34
 * @Description
 */
@Aviator
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AviatorNotBlank {
    /**
     * 前置条件
     * @return
     */
    String precondition() default "true";
    /**
     * 解析异常是否抛出异常
     * @return
     */
    boolean throwException() default true;
    String key();

    String errorMessage() default "参数不符合要求"; // 错误信息
}
