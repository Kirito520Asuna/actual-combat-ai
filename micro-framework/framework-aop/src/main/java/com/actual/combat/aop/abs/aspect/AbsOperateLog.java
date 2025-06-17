package com.actual.combat.aop.abs.aspect;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.actual.combat.aop.abs.aop.AbsAop;
import com.actual.combat.aop.abs.aop.AopConstants;
import com.actual.combat.aop.abs.service.AbsOperateLogService;
import com.actual.combat.aop.all.log.SysLog;
import com.actual.combat.aop.pojo.OperateLogInfo;
import com.actual.combat.aop.pojo.ResultLog;
import com.actual.combat.aop.utils.date.AopDateUtils;
import com.actual.combat.aop.utils.servlet.AopServletUtils;
import com.actual.combat.aop.utils.thread.AopThreadMdcUtil;
import com.alibaba.ttl.TransmittableThreadLocal;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * @Author yan
 * @Date 2025/5/23 21:36:51
 * @Description
 */
public interface AbsOperateLog extends AbsAop {

    /**
     * 操作记录通用实体
     */
    ThreadLocal<OperateLogInfo> operateLogThreadLocal = new TransmittableThreadLocal<>();

    /**
     * 需要获取请求行的请求方法
     */
    List<String> getParamsMethod = CollUtil.newArrayList(RequestMethod.GET.name(), RequestMethod.DELETE.name());

    /**
     * 需要获取请求体的请求方法
     */
    List<String> getBodyMethod = CollUtil.newArrayList(RequestMethod.POST.name(), RequestMethod.PUT.name());


    default OperateLogInfo setOperateLog(OperateLogInfo operateLog) {
        operateLogThreadLocal.set(operateLog);
        return operateLog;
    }

    @Override
    @Pointcut("@annotation(com.actual.combat.aop.all.log.SysLog)")
    default void Aop() {

    }

    @Override
    @Around(value = "Aop()")
    default Object around(ProceedingJoinPoint joinPoint) throws Throwable {

        LocalDateTime now = AopDateUtils.longToLocalDateTime(System.currentTimeMillis());
        OperateLogInfo operateLog = getOperateLog(now);
        SysLog sysLog = getAnnotation(joinPoint, SysLog.class);

        Environment env = SpringUtil.getBean(Environment.class);

        boolean hasSysLog = ObjectUtil.isNotEmpty(sysLog);
        if (hasSysLog && sysLog.enableOperate()) {
            // 接收到请求，记录请求内容
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            //可能有空指针问题
            HttpServletRequest request = attributes.getRequest();
            // 记录下请求内容、
            String applicationName = env.getProperty("spring.application.name", String.class);
            Map<String, String> paramMap = JakartaServletUtil.getParamMap(request);
            Object[] pointArgs = joinPoint.getArgs();
            String args = Arrays.toString(pointArgs);
            args = CollUtil.isEmpty(paramMap) ? args : JSONUtil.parse(paramMap, JSON_CONFIG).toString();

            String declaringTypeName = joinPoint.getSignature().getDeclaringTypeName();
            String name = joinPoint.getSignature().getName();


            Operate operate = new Operate()
                    .setNow(now)
                    .setArgs(args)
                    .setApplicationName(applicationName)
                    .setMethod(request.getMethod())
                    .setParamMap(paramMap)
                    .setTitle(sysLog.title())
                    .setRemoteAddr(JakartaServletUtil.getClientIP(request))
                    .setRequestURL(request.getRequestURL())
                    .setPointArgs(pointArgs)
                    .setDeclaringTypeName(declaringTypeName)
                    .setInterfaceName(name);
            operateInit(joinPoint, operateLog, sysLog, request, operate);
        }

        Object around = AbsAop.super.around(joinPoint);

        if (hasSysLog && sysLog.enableOperate() && sysLog.logResultData()) {
            //记录响应
            JSON parse = JSONUtil.parse(around != null ? around : new LinkedHashMap<>(), JSON_CONFIG);
            Map<String,Object> bean = parse.toBean(Map.class);
            operateFinish(operateLog, bean);
        }

        return around;
    }

    /**
     * 获取
     *
     * @param now
     * @return
     */
    default OperateLogInfo getOperateLog(LocalDateTime now) {
        OperateLogInfo operateLog = operateLogThreadLocal.get();
        if (operateLog == null && ObjectUtil.isNotEmpty(now)) {
            //获取秒数
            //Long second = now.toEpochSecond(ZoneOffset.of("+8"));
            //获取毫秒数 自动获取时区
            String traceId = AopThreadMdcUtil.getTraceId();
            if (ObjectUtil.isEmpty(traceId)) {
                Long milliSecond = now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                traceId = new StringBuffer().append(UUID.randomUUID()).append("::").append(milliSecond).toString();
            }
            operateLog = new OperateLogInfo();
            operateLog.setTraceId(traceId);
            setOperateLog(operateLog);
        }
        return operateLog;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    class Operate {
        /**
         * 应用名
         */
        String applicationName;
        /**
         * 请求方法
         */
        String method;
        /**
         * 请求参数
         */
        Map<String, String> paramMap;
        /**
         * 请求描述
         */
        String title;
        /**
         * 请求IP
         */
        String remoteAddr;
        /**
         * 请求地址
         */
        StringBuffer requestURL;
        /**
         * 请求参数
         */
        Object[] pointArgs;
        /**
         * 请求参数
         */
        String args;
        /**
         * 类名
         */
        String declaringTypeName;
        /**
         * 方法名
         */
        String interfaceName;
        /**
         * 当前时间
         */
        LocalDateTime now;
    }

    /**
     * 初始化操作记录
     *
     * @param joinPoint
     * @param operateLog
     * @param sysLog
     * @param request
     * @param operate
     * @return
     * @throws IOException
     */
    default OperateLogInfo operateInit(JoinPoint joinPoint, OperateLogInfo operateLog, SysLog sysLog, HttpServletRequest request, Operate operate) throws IOException {
        String title = operate.getTitle();
        String remoteAddr = operate.getRemoteAddr();
        StringBuffer requestURL = operate.getRequestURL();
        Object[] pointArgs = operate.getPointArgs();
        String args = operate.getArgs();
        String declaringTypeName = operate.getDeclaringTypeName();
        String interfaceName = operate.getInterfaceName();
        LocalDateTime now = operate.getNow();
        String applicationName = operate.getApplicationName();
        String method = operate.getMethod();
        Map<String, String> paramMap = operate.getParamMap();


        String module = sysLog.module();
        //  尝试读取 {@link Tag#interfaceName()} 属性
        module = ObjectUtil.isEmpty(module) ? getModule(joinPoint) : module;
        //获取用户类型名
        //String userTypeName = environment.getProperty("operate.log.user.type.interfaceName");
        String userTypeName = fetchUserTypeName();
        String userType = null;
        if (ObjectUtil.isNotEmpty(userTypeName)) {
            //从请求头中获取用户类型 可指定枚举用于记录服务器之间的调用记录
            userType = request.getHeader(userTypeName);
        }
        // 获取浏览器
        String userAgent = getUserAgent();
        operateLog.setName(title)
                .setContent(title)
                .setStartTime(AopDateUtils.LocalDateTimeTolong(now))
                .setRequestApplication(applicationName)
                .setModule(module)
                .setUserType(userType)
                .setUserAgent(userAgent)
                .setType(sysLog.type().name())
                .setBusinessType(sysLog.businessType().name())
                .setRequestUrl(requestURL.toString())
                .setRequestMethod(method)
                .setUserIp(remoteAddr)
                .setJavaMethod(new StringBuffer().append(declaringTypeName).append(".").append(interfaceName).toString());

        if (sysLog.enableOperate() && sysLog.logArgs()) {
            String requestBody = null;
            if (ObjectUtil.isEmpty(args)) {
                String toJsonStr = JSONUtil.toJsonStr(pointArgs, JSON_CONFIG);
                args = toJsonStr;
            } else {
                //requestBody = ServletUtils.getRequestContent(request);
                requestBody = AopServletUtils.getRequestBody(request.getInputStream());
                JSON requestBodyJson = null;
                if (ObjectUtil.isEmpty(requestBody)) {
                    try {
                        requestBodyJson = JSONUtil.parse(args, JSON_CONFIG);
                        requestBody = requestBodyJson.toString();
                    } catch (Exception e) {
                        //解析失败
                        log().error("[Error]:RequestBodyJson Parsing failed:{}", args);
                        log().error(e.getMessage());
                        requestBody = args;
                        operateLog.setJavaMethodArgsBodyToJson(false);
                    }
                }
                log().debug("argsJson:{}", requestBodyJson);
            }
            log().debug("args:{}", args);
            //记录请求参数
            String params = JSONUtil.parse(paramMap, JSON_CONFIG).toString();
            if (getParamsMethod.contains(method)) {
                operateLog.setJavaMethodArgsParams(params);
            } else if (getBodyMethod.contains(method)) {
                operateLog.setJavaMethodArgsBody(requestBody);
            }

            AbsOperateLogService service = SpringUtil.getBean(AbsOperateLogService.class);
            Map<String, Object> beanToMap = BeanUtil.beanToMap(operateLog);
            service.createOperateLog(beanToMap);
        }
        return operateLog;
    }

    default <T> OperateLogInfo operateFinish(OperateLogInfo operateLog, T bean) {
        Map<String, Object> beanToMap = BeanUtil.beanToMap(bean);

        Long startTime = operateLog.getStartTime();
        Long resultTime = Long.parseLong((String) beanToMap.get("resultTime"));
        String resultCode = String.valueOf(beanToMap.get("code"));
        String resultMsg = String.valueOf(beanToMap.get("message"));
        Object resultData = String.valueOf(beanToMap.get("data"));
        //获取毫秒数 东八区

        Long resultMilliSecond = resultTime;
        Long startMilliSecond = startTime;
        operateLog.setResultCode(resultCode);
        operateLog.setResultMsg(resultMsg);
        operateLog.setResultData(JSONUtil.toJsonStr(resultData, JSON_CONFIG));
        operateLog.setResultTime(resultTime);
        operateLog.setDuration(resultMilliSecond - startMilliSecond);
        AbsOperateLogService service = SpringUtil.getBean(AbsOperateLogService.class);
        Map<String, Object> operateLogToMap = BeanUtil.beanToMap(operateLog);
        service.updateOperateLog(operateLogToMap);
        return operateLog;
    }

    /**
     * 操作记录结束
     *
     * @param operateLog
     * @param bean
     * @return
     */
    default OperateLogInfo operateFinish(OperateLogInfo operateLog, ResultLog bean) {
        Long startTime = operateLog.getStartTime();
        Long resultTime = bean.getResultTime();
        //获取毫秒数 东八区

        Long resultMilliSecond = resultTime;
        Long startMilliSecond = startTime;
        operateLog.setResultCode(bean.getCode());
        operateLog.setResultMsg(bean.getMessage());
        operateLog.setResultData(JSONUtil.toJsonStr(bean.getData(), JSON_CONFIG));
        operateLog.setResultTime(resultTime);
        operateLog.setDuration(resultMilliSecond - startMilliSecond);
        AbsOperateLogService service = SpringUtil.getBean(AbsOperateLogService.class);
        Map<String, Object> beanToMap = BeanUtil.beanToMap(operateLog);
        service.updateOperateLog(beanToMap);
        return operateLog;
    }
    default String getUserAgent() {
        return AopServletUtils.getUserAgent();
    }

    @Override
    default int getOrder() {
        return AopConstants.OperateLogOrder;
    }

    String fetchUserTypeName();

}
