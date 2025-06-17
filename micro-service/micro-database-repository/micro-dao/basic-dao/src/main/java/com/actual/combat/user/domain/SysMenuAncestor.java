package com.actual.combat.user.domain;

import com.actual.combat.basic.core.constant.table.TableConstants;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Author yan
 * @Date 2024/9/28 上午1:26:20
 * @Description
 */

/**
 * 菜单权限祖先表
 */
@Schema(description = "菜单权限祖先表")
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = TableConstants.menu_ancestor)
public class SysMenuAncestor implements Serializable {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "id")
    private Long id;

    /**
     * 菜单ID
     */
    @TableField(value = "menu_id")
    @Schema(description = "菜单ID")
    private Long menuId;

    /**
     * 上级菜单ID
     */
    @TableField(value = "menu_parent_id")
    @Schema(description = "上级菜单ID")
    private Long menuParentId;

    /**
     * 第几级祖先 从自身往上数 0->
     */
    @TableField(value = "`level`")
    @Schema(description = "第几级祖先 从自身往上数 0->")
    private Long level;

    private static final long serialVersionUID = 1L;

    public static final String COL_ID = "id";

    public static final String COL_MENU_ID = "menu_id";

    public static final String COL_MENU_PARENT_ID = "menu_parent_id";

    public static final String COL_LEVEL = "level";
}