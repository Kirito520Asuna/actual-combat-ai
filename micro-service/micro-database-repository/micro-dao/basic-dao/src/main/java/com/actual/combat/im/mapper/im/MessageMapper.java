package com.actual.combat.im.mapper.im;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.actual.combat.im.domain.im.Message;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author yan
 * @Date 2023/8/7 0007 10:39
 * @Description
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {
}