package com.actual_combat.auth.shiro.aop.aspect;

import com.actual_combat.auth.shiro.abs.AbsShiroAopAspect;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @Author yan
 * @Date 2024/10/21 上午1:05:53
 * @Description
 */
@Aspect
@Slf4j
@Component
public class ShiroRolesAspect implements AbsShiroAopAspect {
    @Pointcut(value = "@annotation(com.actual_combat.auth.shiro.aop.ShiroRoles)")
    @Override
    public void Aop() {
    }
}
