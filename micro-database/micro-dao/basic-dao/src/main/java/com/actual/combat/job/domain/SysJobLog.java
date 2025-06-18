package com.actual.combat.job.domain;

import com.actual.combat.basic.core.constant.table.TableConstants;
import com.actual.combat.mp.pojo.SearchEntity;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 定时任务调度日志表
 */
@Schema(description = "定时任务调度日志表")
@Data
@Accessors(chain = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = TableConstants.job_log)
public class SysJobLog extends SearchEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 任务日志ID
     */
    @TableId(value = "job_log_id", type = IdType.AUTO)
    @Schema(description = "任务日志ID")
    private Long jobLogId;

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
     * 日志信息
     */
    @TableField(value = "job_message")
    @Schema(description = "日志信息")
    private String jobMessage;

    /**
     * 执行状态（0正常 1失败）
     */
    @TableField(value = "`status`")
    @Schema(description = "执行状态（0正常 1失败）")
    private String status;

    /**
     * 异常信息
     */
    @TableField(value = "exception_info")
    @Schema(description = "异常信息")
    private String exceptionInfo;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    public static final String COL_JOB_LOG_ID = "job_log_id";

    public static final String COL_JOB_NAME = "job_name";

    public static final String COL_JOB_GROUP = "job_group";

    public static final String COL_INVOKE_TARGET = "invoke_target";

    public static final String COL_JOB_MESSAGE = "job_message";

    public static final String COL_STATUS = "status";

    public static final String COL_EXCEPTION_INFO = "exception_info";

    public static final String COL_CREATE_TIME = "create_time";
}