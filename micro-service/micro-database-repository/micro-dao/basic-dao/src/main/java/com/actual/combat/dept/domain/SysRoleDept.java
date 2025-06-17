package com.actual.combat.dept.domain;

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
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 角色和部门关联表
 */
@Schema(description="角色和部门关联表")
@Data
@EqualsAndHashCode(callSuper=true)
@Accessors(chain = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = TableConstants.role_dept)
public class SysRoleDept extends BaseEntity implements Serializable {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description="id")
    private Long id;

    /**
     * 角色ID
     */
    //@TableField(value = "role_id")
    @TableField(value = COL_ROLE_ID)
    @Schema(description="角色ID")
    private Long roleId;

    /**
     * 部门ID
     */
    @TableField(value = "dept_id")
    @Schema(description="部门ID")
    private Long deptId;

    public static final String COL_ID = "id";

    public static final String COL_ROLE_ID = TableConstants.ROLE_DEPT_COL_ROLE_ID;

    public static final String COL_DEPT_ID = "dept_id";
}