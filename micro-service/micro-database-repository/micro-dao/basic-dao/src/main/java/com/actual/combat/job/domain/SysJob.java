package com.actual.combat.job.domain;

import com.actual.combat.basic.core.constant.table.TableConstants;
import com.actual.combat.mp.pojo.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 定时任务调度表
 */
@Schema(description = "定时任务调度表")
@Data
@Accessors(chain = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = TableConstants.job)
public class SysJob extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 任务ID
     */
    @TableId(value = "job_id", type = IdType.AUTO)
    @Schema(description = "任务ID")
    private Long jobId;

    /**
     * 任务名称
     */
    @TableField(value = "job_name")
    @Schema(description = "任务名称")
    private String jobName;

    /**
     * 任务组名
     */
    @TableField(value = "job_group")
    @Schema(description = "任务组名")
    private String jobGroup;

    /**
     * 调用目标字符串
     */
    @TableField(value = "invoke_target")
    @Schema(description = "调用目标字符串")
    private String invokeTarget;

    /**
     * cron执行表达式
     */
    @TableField(value = "cron_expression")
    @Schema(description = "cron执行表达式")
    private String cronExpression;

    /**
     * 计划执行错误策略（1立即执行 2执行一次 3放弃执行）
     */
    @TableField(value = "misfire_policy")
    @Schema(description = "计划执行错误策略（1立即执行 2执行一次 3放弃执行）")
    private String misfirePolicy;

    /**
     * 是否并发执行（0允许 1禁止）
     */
    @TableField(value = "concurrent")
    @Schema(description = "是否并发执行（0允许 1禁止）")
    private String concurrent;

    /**
     * 状态（0正常 1暂停）
     */
    @TableField(value = "`status`")
    @Schema(description = "状态（0正常 1暂停）")
    private String status;


    public static final String COL_JOB_ID = "job_id";

    public static final String COL_JOB_NAME = "job_name";

    public static final String COL_JOB_GROUP = "job_group";

    public static final String COL_INVOKE_TARGET = "invoke_target";

    public static final String COL_CRON_EXPRESSION = "cron_expression";

    public static final String COL_MISFIRE_POLICY = "misfire_policy";

    public static final String COL_CONCURRENT = "concurrent";

    public static final String COL_STATUS = "status";

}