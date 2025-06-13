package com.actual_combat.aop.all.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author yan
 * @Date 2024/9/27 上午10:47:40
 * @Description 登录认证：只有登录之后才能进入该方法
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface Login {
}
