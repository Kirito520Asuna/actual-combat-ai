package com.actual_combat.aop.abs.aop;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONConfig;
import com.actual_combat.aop.abs.bean.AbsBean;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @Author yan
 * @Date 2025/5/18 15:38:27
 * @Description
 */
public interface AbsAop extends Ordered, AbsBean {
    JSONConfig JSON_CONFIG = JSONConfig.create().setIgnoreNullValue(false);

    @Override
    @PostConstruct
    default void init() {
        log().debug("[init]-[Aop]:[Order:{}]::[{}]", getOrder(), getAClassName());
    }
    @SneakyThrows
    default <T extends Annotation> T getAnnotation(JoinPoint joinPoint, Class<T> annotationClass) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        T annotation = null;
        if (ObjectUtil.isNotEmpty(method)) {
            annotation = method.getAnnotation(annotationClass);
        }
        return annotation;
    }
    @Override
    default int getOrder() {
        return AopConstants.BaseOrder;
    }

    void Aop();

    default void doBefore(JoinPoint joinPoint) throws Exception {
    }

    default Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        return joinPoint.proceed();
    }

    default void doAfterReturning(JoinPoint joinPoint, Object reValue) {
    }
}
