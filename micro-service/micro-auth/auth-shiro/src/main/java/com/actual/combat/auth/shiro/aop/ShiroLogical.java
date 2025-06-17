package com.actual.combat.auth.shiro.aop;


import com.actual.combat.auth.shiro.aop.aspect.ShiroAopAspect;
import org.apache.shiro.authz.annotation.Logical;
import org.aspectj.lang.ProceedingJoinPoint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author yan
 * @Date 2024/10/21 上午12:45:20
 * @Description Shiro逻辑注解
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ShiroLogical {

    /**
     * 灵活控制注解 逻辑
     * 注解逻辑 默认为 and
     * {@link ShiroRoles}
     * {@link ShiroPermissions}
     * and:注解同时满足才允许访问方法
     * @see org.apache.shiro.authz.annotation.Logical
     * @see ShiroAopAspect#around(ProceedingJoinPoint)
     */
    Logical logical() default Logical.AND;
}
