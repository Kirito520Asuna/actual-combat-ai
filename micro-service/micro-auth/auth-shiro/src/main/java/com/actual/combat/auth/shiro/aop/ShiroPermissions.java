package com.actual.combat.auth.shiro.aop;

import org.apache.shiro.authz.annotation.Logical;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author yan
 * @Date 2024/10/21 上午12:45:20
 * @Description
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ShiroPermissions {
    /**
     * 需要单个 String 权限名称或多个逗号分隔的权限名称，才能允许方法调用。
     */
    String[] value();
    /**
     * 指定了多个权限时进行权限检查的逻辑操作。AND 是默认值
     */
    Logical logical() default Logical.AND;
}
