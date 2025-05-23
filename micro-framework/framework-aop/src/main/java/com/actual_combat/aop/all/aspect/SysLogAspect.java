package com.actual_combat.aop.all.aspect;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.actual_combat.aop.abs.aspect.AbsSysLog;
import com.actual_combat.aop.all.log.SysLog;
import com.actual_combat.aop.utils.gateway.GatewayUtils;
import com.actual_combat.aop.utils.thread.AopThreadMdcUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;

/**
 * @Author yan
 * @Date 2025/5/21 23:42:27
 * @Description
 */
@Slf4j
@Getter
@Aspect
@Component
public class SysLogAspect implements AbsSysLog {

    @Override
    @Around(value = "Aop()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return AbsSysLog.super.aroundSysLog(joinPoint);
        }finally {
            AopThreadMdcUtil.clear();
        }
    }

}
