package com.actual_combat.aop.abs;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import net.minidev.json.annotate.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @Author yan
 * @Date 2025/5/18 15:39:46
 * @Description
 */
public interface AbsBean {
    @Data
    @Accessors(chain = true)
    @NoArgsConstructor
    @AllArgsConstructor
    class LogBean {
        private org.slf4j.Logger logger;
        private Class<?> aClass;
    }

    @JsonIgnore
    default LogBean getLogBean() {
        LogBean logBean = new LogBean()
                .setAClass(this.getClass())
                .setLogger(LoggerFactory.getLogger(this.getClass()));
        return logBean;
    }

    default <T> T getBean(Class<T> clazz) {
        T bean = SpringUtil.getBean(clazz);
        return bean;
    }

    @JsonIgnore
    default Logger getLogger() {
        return getLogBean().getLogger();
    }

    @JsonIgnore
    default Class<?> getAClass() {
        return getLogBean().getAClass();
    }

    @JsonIgnore
    default String getAClassName() {
        return StrUtil.subBefore(getAClass().getName(), "$", false);
    }

    /**
     * 初始化
     */
    @PostConstruct
    default void init() {
        LogBean logBean = getLogBean();
        logBean.getLogger().info("[init]::[{}]: ", getAClassName());
    }

    /**
     * 销毁
     */
    @PreDestroy
    default void destroy() {
        LogBean logBean = getLogBean();
        logBean.getLogger().info("[destroy]::[{}]", getAClassName());
    }

}
