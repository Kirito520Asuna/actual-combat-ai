package com.actual.combat.aop.all.aviator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author yan
 * @Date 2024/11/16 下午7:43:04
 * @Description
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Aviator {
    /**
     * 前置条件
     * @return
     */
    String[] preconditions() default {"true"};
    String defaultPrecondition() default "参数不符合要求"; // 错误信息
    /**
     * 解析异常是否抛出异常
     * @return
     */
    boolean throwException() default true;
    String defaultErrorMessage() default "参数不符合要求"; // 错误信息
    boolean isStr() default false;
    String[] keys() default {};
    String[] expressions() default {}; // Aviator表达式
    String[] errorMessages() default {"参数不符合要求"}; // 错误信息
}

