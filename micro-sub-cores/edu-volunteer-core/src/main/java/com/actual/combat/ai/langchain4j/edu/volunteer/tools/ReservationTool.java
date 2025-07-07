package com.actual.combat.ai.langchain4j.edu.volunteer.tools;

import com.actual.combat.ai.langchain4j.edu.volunteer.domain.Reservation;
import com.actual.combat.ai.langchain4j.tools.AbstractTool;
import com.actual.combat.ai.langchain4j.edu.volunteer.service.ReservationService;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @Author yan
 * @Date 2025/7/3 16:46:04
 * @Description
 */
@Component
public class ReservationTool<T extends ReservationService>  implements AbstractTool<ReservationService> {
    @Resource
    protected T toolService;

    @Override
    public ReservationService toolService() {
        return toolService;
    }

    @Tool("添加志愿指导服务预约")
    public void addReservation(
            @P("考生姓名") String name,
            @P("考生性别") String gender,
            @P("考生电话") String phone,
            @P("考生所在省份") String province,
            @P("考生预估分数") Integer score,
            @P("沟通时间") LocalDateTime date
    ) {
        Reservation build = Reservation.builder()
                .name(name)
                .gender(gender)
                .phone(phone)
                .province(province)
                .estimatedScore(score)
                .communicationTime(date)
                .build();
        toolService().insert(build);
    }

    @Tool("根据考生电话查询考生预约情况")
    public Reservation findReservation(@P("考生电话") String phone) {
        return toolService().getByPhone(phone);
    }

}
