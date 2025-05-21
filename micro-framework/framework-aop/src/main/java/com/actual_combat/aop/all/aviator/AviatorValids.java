package com.actual_combat.aop.all.aviator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author yan
 * @Date 2024/11/12 上午1:30:05
 * @Description
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AviatorValids {
    Aviator[] aviator() default {};

    AviatorValid[] values() default {};

    AviatorNotBlank[] notBlanks() default {};

}
