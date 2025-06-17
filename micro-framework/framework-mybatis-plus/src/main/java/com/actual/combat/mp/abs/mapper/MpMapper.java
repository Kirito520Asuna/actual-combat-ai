package com.actual.combat.mp.abs.mapper;

import com.actual.combat.mp.abs.handler.AbsEntityHandler;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

/**
 * @Author yan
 * @Date 2025/3/10 17:29:36
 * @Description
 */
@Mapper
@ConditionalOnBean(AbsEntityHandler.class)
@ConditionalOnMissingBean(MpMapper.class)
public interface MpMapper<T> extends BaseMapper<T> {
}
