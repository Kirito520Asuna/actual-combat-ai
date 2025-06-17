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
 * 菜单权限表
 */
@Schema(description = "菜单权限表")
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = TableConstants.menu)
public class SysMenu extends BaseEntity implements Serializable {
    /**
     * 菜单ID
     */
    @TableId(value = "menu_id", type = IdType.AUTO)
    @Schema(description = "菜单ID")
    private Long menuId;

    /**
     * 菜单名称
     */
    @TableField(value = "menu_name")
    @Schema(description = "菜单名称")
    private String menuName;

    /**
     * 父菜单ID
     */
    @TableField(value = "parent_id")
    @Schema(description = "父菜单ID")
    private Long parentId;

    /**
     * 显示顺序
     */
    @TableField(value = "order_num")
    @Schema(description = "显示顺序")
    private Integer orderNum;

    /**
     * 路由地址
     */
    @TableField(value = "`path`")
    @Schema(description = "路由地址")
    private String path;

    /**
     * 组件路径
     */
    @TableField(value = "component")
    @Schema(description = "组件路径")
    private String component;

    /**
     * 路由参数
     */
    @TableField(value = "query")
    @Schema(description = "路由参数")
    private String query;

    /**
     * 路由名称
     */
    @TableField(value = "route_name")
    @Schema(description = "路由名称")
    private String routeName;

    /**
     * 是否为外链（0是 1否）
     */
    @TableField(value = "is_frame")
    @Schema(description = "是否为外链（0是 1否）")
    private Integer isFrame;

    /**
     * 是否缓存（0缓存 1不缓存）
     */
    @TableField(value = "is_cache")
    @Schema(description = "是否缓存（0缓存 1不缓存）")
    private Integer isCache;

    /**
     * 菜单类型（M目录 C菜单 F按钮）
     */
    @TableField(value = "menu_type")
    @Schema(description = "菜单类型（M目录 C菜单 F按钮）")
    private String menuType;

    /**
     * 菜单状态（0显示 1隐藏）
     */
    @TableField(value = "visible")
    @Schema(description = "菜单状态（0显示 1隐藏）")
    private String visible;

    /**
     * 菜单状态（0正常 1停用）
     */
    @TableField(value = "`status`")
    @Schema(description = "菜单状态（0正常 1停用）")
    private String status;

    /**
     * 权限标识
     */
    @TableField(value = "perms")
    @Schema(description = "权限标识")
    private String perms;

    /**
     * 菜单图标
     */
    @TableField(value = "icon")
    @Schema(description = "菜单图标")
    private String icon;
    private static final long serialVersionUID = 1L;

    public static final String COL_MENU_ID = "menu_id";

    public static final String COL_MENU_NAME = "menu_name";

    public static final String COL_PARENT_ID = "parent_id";

    public static final String COL_ORDER_NUM = "order_num";

    public static final String COL_PATH = "path";

    public static final String COL_COMPONENT = "component";

    public static final String COL_QUERY = "query";

    public static final String COL_ROUTE_NAME = "route_name";

    public static final String COL_IS_FRAME = "is_frame";

    public static final String COL_IS_CACHE = "is_cache";

    public static final String COL_MENU_TYPE = "menu_type";

    public static final String COL_VISIBLE = "visible";

    public static final String COL_STATUS = "status";

    public static final String COL_PERMS = "perms";

    public static final String COL_ICON = "icon";

}