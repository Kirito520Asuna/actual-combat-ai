package com.actual.combat.ai.edu.volunteer.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * @Author yan
 * @Date 2025/6/30 21:59:43
 * @Description
 */
@Data
@Accessors(chain = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Reservation {
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_GENDER = "gender";
    public static final String COL_PHONE = "phone";
    public static final String COL_COMMUNICATION_TIME = "communication_time";
    public static final String COL_PROVINCE = "province";
    public static final String COL_ESTIMATED_SCORE = "estimated_score";
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 考生姓名
     */
    private String name;

    /**
     * 考生性别
     */
    private String gender;

    /**
     * 考生手机号
     */
    private String phone;

    /**
     * 沟通时间
     */
    private LocalDateTime communicationTime;

    /**
     * 考生所处的省份
     */
    private String province;

    /**
     * 考生预估分数
     */
    private Integer estimatedScore;
}