package com.actual.combat.auth.shiro.aop.aspect;

import com.actual.combat.auth.shiro.abs.AbsShiroAopAspect;
import com.actual.combat.auth.shiro.utils.AuthShiroContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author yan
 * @Date 2024/10/21 上午1:05:53
 * @Description
 */
@Aspect
@Slf4j
@Component
public class ShiroAopAspect implements AbsShiroAopAspect {
    @Override
    @Pointcut(value = "com.actual.combat.auth.shiro.aop.aspect.ShiroPermissionsAspect.Aop()||com.actual.combat.auth.shiro.aop.aspect.ShiroRolesAspect.Aop()")
    public void Aop() {
    }

    @Override
    public List<String> getRoles() {
        return AuthShiroContextUtil.getRoles();
    }

    @Override
    public List<String> getPermissions() {
        return AuthShiroContextUtil.getPermissions();
    }

    @Around(value = "Aop()")
    @Override
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        hasRolesPermissions(joinPoint);
        return AbsShiroAopAspect.super.around(joinPoint);
    }
}
