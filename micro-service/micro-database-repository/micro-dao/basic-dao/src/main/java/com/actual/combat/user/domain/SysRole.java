package com.actual.combat.user.domain;

import com.actual.combat.basic.core.constant.table.TableConstants;
import com.actual.combat.mp.pojo.BaseEntity;
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
 * @Date 2024/9/28 上午1:26:19
 * @Description
 */

/**
 * 角色信息表
 */
@Schema(description = "角色信息表")
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = TableConstants.role)
public class SysRole extends BaseEntity implements Serializable {
    /**
     * 角色ID
     */
    @TableId(value = "role_id", type = IdType.AUTO)
    @Schema(description = "角色ID")
    private Long roleId;

    /**
     * 角色名称
     */
    @TableField(value = "role_name")
    @Schema(description = "角色名称")
    private String roleName;

    /**
     * 角色权限字符串
     */
    @TableField(value = "role_key")
    @Schema(description = "角色权限字符串")
    private String roleKey;

    /**
     * 显示顺序
     */
    @TableField(value = "role_sort")
    @Schema(description = "显示顺序")
    private Integer roleSort;

    /**
     * 数据范围（1：全部数据权限 2：自定数据权限）
     */
    @TableField(value = "data_scope")
    @Schema(description = "数据范围（1：全部数据权限 2：自定数据权限）")
    private String dataScope;

    /**
     * 菜单树选择项是否关联显示
     */
    @TableField(value = "menu_check_strictly")
    @Schema(description = "菜单树选择项是否关联显示")
    private Object menuCheckStrictly;

    /**
     * 角色状态（0正常 1停用）
     */
    @TableField(value = "`status`")
    @Schema(description = "角色状态（0正常 1停用）")
    private String status;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @TableField(value = "del_flag")
    @Schema(description = "删除标志（0代表存在 2代表删除）")
    private String delFlag;
    private static final long serialVersionUID = 1L;

    public static final String COL_ROLE_ID = "role_id";

    public static final String COL_ROLE_NAME = "role_name";

    public static final String COL_ROLE_KEY = "role_key";

    public static final String COL_ROLE_SORT = "role_sort";

    public static final String COL_DATA_SCOPE = "data_scope";

    public static final String COL_MENU_CHECK_STRICTLY = "menu_check_strictly";

    public static final String COL_STATUS = "status";

    public static final String COL_DEL_FLAG = "del_flag";
}