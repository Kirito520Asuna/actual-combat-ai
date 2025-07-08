package com.actual.combat.basic.core.advice;

import cn.hutool.core.collection.CollUtil;
import com.actual.combat.aop.abs.bean.AbsBean;
import com.actual.combat.aop.all.log.SysLog;
import com.actual.combat.basic.result.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.List;

/**
 * @Author yan
 * @Date 2025/7/8 17:09:33
 * @Description
 */
@Slf4j
@RestControllerAdvice
public class GlobalResponseBodyAdvice<T extends Object> implements ResponseBodyAdvice<T>, AbsBean {
    @Resource
    protected ObjectMapper objectMapper;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        //这里可以判断是否需要封装响应
        SysLog annotation = returnType.getMethod().getAnnotation(SysLog.class);
        if (annotation != null) {
            Class<?> parameterType = returnType.getParameterType();
            List<Class<?>> classes = CollUtil.newArrayList(Result.class, Void.class);
            boolean wrapResult = annotation.wrapResult() && !CollUtil.contains(classes, parameterType);
            return wrapResult;
        }
        //默认不封装
        return false;
    }

    @Override
    public T beforeBodyWrite(T body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof String) {
            try {
                return (T) objectMapper.writeValueAsString(body);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        log.debug("需要封装响应");
        return (T) Result.ok(body);
    }
}
