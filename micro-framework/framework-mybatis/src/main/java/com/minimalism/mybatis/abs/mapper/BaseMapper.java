package com.minimalism.mybatis.abs.mapper;

import com.minimalism.mybatis.abs.handler.AbsEntityHandler;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

/**
 * @Author yan
 * @Date 2025/3/10 17:29:36
 * @Description
 */
@Mapper
@ConditionalOnBean(AbsEntityHandler.class) // 当存在AbsEntityHandler类时，才加载该类
@ConditionalOnMissingBean(BaseMapper.class) // 当不存在BaseMapper类时，才加载该类
public interface BaseMapper<T>{
}
