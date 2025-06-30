package com.minimalism.mybatis.abs.service.impl;

import com.minimalism.mybatis.abs.mapper.BaseMapper;
import com.minimalism.mybatis.abs.service.IService;
import jakarta.annotation.Resource;


/**
 * @Author yan
 * @Date 2025/3/10 17:26:44
 * @Description
 */
public class ImplService<M extends BaseMapper<T>, T> implements IService<T> {
    @Resource
    private M baseMapper;

    public M getBaseMapper() {
        return baseMapper;
    }
}
