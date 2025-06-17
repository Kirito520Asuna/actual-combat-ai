package com.actual.combat.aop.abs.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import com.actual.combat.aop.abs.bean.AbsBean;
import com.actual.combat.aop.all.async.AsyncFuture;
import com.actual.combat.aop.pojo.OperateLogInfo;
import jakarta.annotation.PostConstruct;

import java.util.Map;

/**
 * 操作日志  抽象Service 接口
 *
 * @author yan
 */
public interface AbsOperateLogService extends AbsBean {
    JSONConfig JSON_CONFIG = JSONConfig.create().setIgnoreNullValue(false);

    @Override
    @PostConstruct
    default void init() {
        log().debug("[init]-[Service]-[OperateLog] {}", getAClass().getName());
    }

    //=====================同步=====================
    default void createOperateLog(Map<String, Object> operateLogMap) {
        if (ObjectUtil.isEmpty(operateLogMap)) {
            return;
        }
        OperateLogInfo operateLog = new OperateLogInfo();
        BeanUtil.copyProperties(operateLogMap, operateLog);
        createOperateLog(operateLog);
    }

    default void updateOperateLog(Map<String, Object> operateLogMap) {
        if (ObjectUtil.isEmpty(operateLogMap)) {
            return;
        }
        OperateLogInfo operateLog = new OperateLogInfo();
        BeanUtil.copyProperties(operateLogMap, operateLog);
        updateOperateLog(operateLog);
    }

    /**
     * 记录操作日志
     *
     * @param operateLog 操作日志请求
     */
    default void createOperateLog(OperateLogInfo operateLog) {
        JSON json = JSONUtil.parse(operateLog, JSON_CONFIG);
        log().debug("operateLog==>{}", json);
    }


    /**
     * 更新操作日志
     */
    default void updateOperateLog(OperateLogInfo operateLog) {
        JSON json = JSONUtil.parse(operateLog, JSON_CONFIG);
        log().info("operateLog==>{}", json);
    }


    //=====================异步=====================

    /**
     * 注意同实现类调同实现类的异步是不生效的
     * 因此需要将所有异步集中在异步server接口实现生效
     * 记录操作日志
     *
     * @param operateLog 操作日志请求
     */
    @AsyncFuture(useExecutor = true)
    default void createOperateLogAsync(OperateLogInfo operateLog) {
        JSON json = JSONUtil.parse(operateLog, JSON_CONFIG);
        log().debug("operateLog==>{}", json);
    }

    /**
     * 更新操作日志
     */
    @AsyncFuture(useExecutor = true)
    default void updateOperateLogAsync(OperateLogInfo operateLog) {
        JSON json = JSONUtil.parse(operateLog, JSON_CONFIG);
        log().debug("operateLog==>{}", json);
    }

}
