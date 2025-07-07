package com.actual.combat.ai.langchain4j.edu.volunteer.service.impl;

import com.actual.combat.ai.langchain4j.edu.volunteer.domain.Reservation;
import com.actual.combat.ai.langchain4j.edu.volunteer.mapper.ReservationMapper;
import com.actual.combat.ai.langchain4j.edu.volunteer.service.ReservationService;
import com.minimalism.mybatis.abs.service.impl.ImplService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * @Author yan
 * @Date 2025/6/30 00:39:18
 * @Description
 */
@Service
public class ReservationServiceImpl extends ImplService<ReservationMapper, Reservation> implements ReservationService {

    @Override
    public Reservation getByPhone(String phone) {
        return baseMapper.getOneByPhone(phone);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Reservation insert(Reservation reservation) {
        baseMapper.insert(reservation);
        return reservation;
    }
}
