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
 * 部门祖先表
 */
@Schema(description = "部门祖先表")
@Data
@Accessors(chain = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = TableConstants.dept_ancestor)
public class SysDeptAncestor implements Serializable {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "id")
    private Long id;

    /**
     * ID
     */
    //@TableField(value = "dept_id")
    @TableField(value = COL_DEPT_ID)
    @Schema(description = "ID")
    private Long deptId;

    /**
     * 上级ID
     */
    //@TableField(value = "dept_parent_id")
    @TableField(value = COL_DEPT_PARENT_ID)
    @Schema(description = "上级ID")
    private Long deptParentId;

    /**
     * 第几级祖先 从自身往上数 0->
     */
    @TableField(value = "`level`")
    @Schema(description = "第几级祖先 从自身往上数 0->")
    private Long level;

    public static final String COL_ID = "id";

    public static final String COL_DEPT_ID = TableConstants.DEPT_ANCESTOR_COL_DEPT_ID;

    public static final String COL_DEPT_PARENT_ID = TableConstants.DEPT_ANCESTOR_COL_DEPT_PARENT_ID;

    public static final String COL_LEVEL = "level";
}