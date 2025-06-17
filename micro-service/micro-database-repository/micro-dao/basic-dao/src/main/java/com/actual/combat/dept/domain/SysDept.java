package com.actual.combat.dept.domain;

import com.actual.combat.basic.core.constant.table.TableConstants;
import com.actual.combat.mp.pojo.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;


/**
 * 部门表
 */
@Schema(description="部门表")
@Data
@EqualsAndHashCode(callSuper=true)
@Accessors(chain = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = TableConstants.dept)
public class SysDept extends BaseEntity implements Serializable {
    /**
     * 部门id
     */
    //@TableId(value = "dept_id", type = IdType.AUTO)
    @TableId(value = COL_DEPT_ID, type = IdType.AUTO)
    @Schema(description="部门id")
    private Long deptId;

    /**
     * 父部门id
     */
    @TableField(value = "parent_id")
    @Schema(description="父部门id")
    private Long parentId;

    /**
     * 部门名称
     */
    @NotBlank(message = "部门名称不能为空")
    @Size(min = 0, max = 30, message = "部门名称长度不能超过30个字符")
    @TableField(value = "dept_name")
    @Schema(description="部门名称")
    private String deptName;

    /**
     * 显示顺序
     */
    @NotNull(message = "显示顺序不能为空")
    @TableField(value = "order_num")
    @Schema(description="显示顺序")
    private Integer orderNum;

    /**
     * 负责人
     */
    @TableField(value = "leader")
    @Schema(description="负责人")
    private String leader;

    /**
     * 联系电话
     */
    @Size(min = 0, max = 11, message = "联系电话长度不能超过11个字符")
    @TableField(value = "phone")
    @Schema(description="联系电话")
    private String phone;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    @Size(min = 0, max = 50, message = "邮箱长度不能超过50个字符")
    @TableField(value = "email")
    @Schema(description="邮箱")
    private String email;

    /**
     * 部门状态（0正常 1停用）
     */
    @TableField(value = "`status`")
    @Schema(description="部门状态（0正常 1停用）")
    private String status;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @TableField(value = "del_flag")
    @Schema(description="删除标志（0代表存在 2代表删除）")
    private String delFlag;

    public static final String COL_DEPT_ID = TableConstants.DEPT_COL_DEPT_ID;

    public static final String COL_PARENT_ID = "parent_id";

    public static final String COL_DEPT_NAME = "dept_name";

    public static final String COL_ORDER_NUM = "order_num";

    public static final String COL_LEADER = "leader";

    public static final String COL_PHONE = "phone";

    public static final String COL_EMAIL = "email";

    public static final String COL_STATUS = "status";

    public static final String COL_DEL_FLAG = "del_flag";
}