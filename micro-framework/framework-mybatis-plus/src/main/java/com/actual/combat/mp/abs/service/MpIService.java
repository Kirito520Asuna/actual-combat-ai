package com.actual.combat.mp.abs.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Map;

/**
 * @Author yan
 * @Date 2025/3/10 17:25:21
 * @Description
 */
public interface MpIService<T> extends IService<T> {
    @Override
    @Transactional(rollbackFor = {Exception.class})
    default boolean save(T entity) {
        return IService.super.save(entity);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    default boolean removeById(Serializable id) {
        return IService.super.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    default boolean removeById(Serializable id, boolean useFill) {
        return IService.super.removeById(id, useFill);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    default boolean removeById(T entity) {
        return IService.super.removeById(entity);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    default boolean removeByMap(Map<String, Object> columnMap) {
        return IService.super.removeByMap(columnMap);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    default boolean remove(Wrapper<T> queryWrapper) {
        return IService.super.remove(queryWrapper);
    }
}
