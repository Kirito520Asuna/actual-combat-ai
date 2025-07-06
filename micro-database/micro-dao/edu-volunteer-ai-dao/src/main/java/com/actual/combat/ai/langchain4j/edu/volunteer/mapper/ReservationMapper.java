package com.actual.combat.ai.langchain4j.edu.volunteer.mapper;

import com.actual.combat.ai.langchain4j.edu.volunteer.domain.Reservation;
import com.minimalism.mybatis.abs.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author yan
 * @Date 2025/6/30 21:59:43
 * @Description
 */
@Mapper
public interface ReservationMapper extends BaseMapper<Reservation> {
    int deleteByPrimaryKey(Long id);

    int insert(Reservation record);

    int insertSelective(Reservation record);

    Reservation selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Reservation record);

    int updateByPrimaryKey(Reservation record);

    Reservation getOneByPhone(String phone);
}