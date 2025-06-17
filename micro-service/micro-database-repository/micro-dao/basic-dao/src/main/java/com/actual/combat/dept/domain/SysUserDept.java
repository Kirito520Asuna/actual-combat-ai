package com.actual.combat.dept.domain;

import com.actual.combat.basic.core.constant.table.TableConstants;
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
 * 用户部门关联表
 */
@Schema(description="用户部门关联表")
@Data
@Accessors(chain = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = TableConstants.user_dept)
public class SysUserDept implements Serializable {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description="id")
    private Long id;

    /**
     * 用户id
     */
    @TableField(value = "user_id")
    @Schema(description="用户id")
    private Long userId;

    /**
     * 部门id
     */
    @TableField(value = "dept_id")
    @Schema(description="部门id")
    private Long deptId;

    public static final String COL_ID = "id";

    public static final String COL_USER_ID = "user_id";

    public static final String COL_DEPT_ID = "dept_id";
}