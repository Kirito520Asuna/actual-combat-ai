package com.actual.combat.ai.langchain4j.edu.volunteer.service;

import com.actual.combat.ai.langchain4j.edu.volunteer.domain.Reservation;

/**
 * @Author yan
 * @Date 2025/6/30 00:39:07
 * @Description
 */
public interface ReservationService {
    Reservation getByPhone(String phone);

    Reservation insert(Reservation reservation);
}
