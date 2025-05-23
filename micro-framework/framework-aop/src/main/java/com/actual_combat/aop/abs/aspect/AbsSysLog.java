package com.actual_combat.aop.abs.aspect;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.actual_combat.aop.abs.aop.AbsAop;
import com.actual_combat.aop.abs.aop.AopConstants;
import com.actual_combat.aop.all.log.SysLog;
import com.actual_combat.aop.utils.gateway.GatewayUtils;
import com.actual_combat.aop.utils.thread.AopThreadMdcUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.env.Environment;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;

/**
 * @Author yan
 * @Date 2023/6/1 0001 17:03
 * @Description
 */
public interface AbsSysLog extends AbsAop {
    default SysLog getSysLog(JoinPoint joinPoint) {
        return getAnnotation(joinPoint, SysLog.class);
    }

    @Override
    @Pointcut(value = "@annotation(com.actual_combat.aop.all.log.SysLog)")
    default void Aop() {
    }

    default Object aroundSysLog(ProceedingJoinPoint joinPoint) throws Throwable {
        String trackId = AopThreadMdcUtil.getTraceId();
        //获取是否有注解
        SysLog sysLog = getSysLog(joinPoint);
        /**
         * 开启请求日志
         */
        boolean openRequestLog = ObjectUtil.isNotEmpty(sysLog) && sysLog.flag();
        if (openRequestLog) {
            // 接收到请求，记录请求内容
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            //可能有空指针问题

            HttpServletRequest request = attributes.getRequest();
            // 记录下请求内容、
            String applicationName = SpringUtil.getBean(Environment.class)
                    .getProperty("spring.application.name", String.class);

            String method = request.getMethod();

            Map<String, String> paramMap = JakartaServletUtil.getParamMap(request);
            log().debug("parse:{}", JSONUtil.parse(paramMap, JSON_CONFIG));

            String title = sysLog.title();
            String remoteAddr = request.getRemoteAddr();
            StringBuffer requestURL = request.getRequestURL();

            Object[] pointArgs = joinPoint.getArgs();
            String args = JSONUtil.toJsonStr(JSONUtil.parse(CollUtil.isEmpty(paramMap) ? pointArgs : paramMap, JSON_CONFIG));

            String declaringTypeName = joinPoint.getSignature().getDeclaringTypeName();
            String name = joinPoint.getSignature().getName();
            String module = getModule(joinPoint);
            if (StrUtil.isBlank(title)) {
                title = getDescription(joinPoint);
            }

            String url = GatewayUtils.replaceUrl(request, requestURL.toString());
            log().info(new StringBuffer()
                            .append("\n====================================请求内容====================================")
                            .append("\n==>TRACK_ID : {} <==")
                            .append("\n==>请求服务名 : {} <==")
                            .append("\n==>请求模块名 : {} <==")
                            .append("\n==>请求描述 : {} <==")
                            .append("\n==>请求IP : {} <==")
                            .append("\n==>请求地址 : {} <==")
                            .append("\n==>请求方式 : {} <==")
                            .append("\n==>请求参数 : {} <==")
                            .append("\n==>请求类方法 : {}.{} <==")
                            .append("\n================================================================================")
                            .toString()
                    , trackId, applicationName, module, title, remoteAddr, url, method, args, declaringTypeName, name);
        }
        // 执行方法
        Object around = AbsAop.super.around(joinPoint);

        /**
         * 开启响应日志
         */
        boolean openResultLog = ObjectUtil.isNotEmpty(sysLog) && sysLog.result();
        Object returnObj = around;
        if (openResultLog) {
            // 处理完请求，返回内容
            if (returnObj == null) {
                returnObj = "返回值为空";
            } else if (JSONUtil.isTypeJSON(JSONUtil.toJsonStr(returnObj))) {
                JSON parse = JSONUtil.parse(around, JSON_CONFIG);
                returnObj = parse;
            } else {
                returnObj = around;
            }
            String jsonStr = JSONUtil.toJsonStr(returnObj);
            log().info(new StringBuffer()
                    .append("\n====================================响应内容====================================")
                    .append("\n==>TRACK_ID : {} <==")
                    .append("\n==>响应 : {} <==")
                    .append("\n================================================================================")
                    .toString(), trackId, jsonStr);
        }
        return around;
    }

    @Override
    default int getOrder() {
        return AopConstants.SysLogOrder;
    }
}
