package com.actual.combat.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Author yan
 * @Date 2024/10/31 上午9:16:41
 * @Description
 */
@Data @NoArgsConstructor
@AllArgsConstructor @Accessors(chain = true)
public class SysMenuDto {
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
    private static final long serialVersionUID = 1L;
    /**
     * 菜单ID
     */
    @Schema(description = "菜单ID")
    private Long menuId;
    /**
     * 菜单名称
     */
    @Schema(description = "菜单名称")
    private String menuName;
    /**
     * 父菜单ID
     */
    @Schema(description = "父菜单ID")
    private Long parentId;
    /**
     * 显示顺序
     */
    @Schema(description = "显示顺序")
    private Integer orderNum;
    /**
     * 路由地址
     */
    @Schema(description = "路由地址")
    private String path;
    /**
     * 组件路径
     */
    @Schema(description = "组件路径")
    private String component;
    /**
     * 路由参数
     */
    @Schema(description = "路由参数")
    private String query;
    /**
     * 路由名称
     */
    @Schema(description = "路由名称")
    private String routeName;
    /**
     * 是否为外链（0是 1否）
     */
    @Schema(description = "是否为外链（0是 1否）")
    private Integer isFrame;
    /**
     * 是否缓存（0缓存 1不缓存）
     */
    @Schema(description = "是否缓存（0缓存 1不缓存）")
    private Integer isCache;
    /**
     * 菜单类型（M目录 C菜单 F按钮）
     */
    @Schema(description = "菜单类型（M目录 C菜单 F按钮）")
    private String menuType;
    /**
     * 菜单状态（0显示 1隐藏）
     */
    @Schema(description = "菜单状态（0显示 1隐藏）")
    private String visible;
    /**
     * 菜单状态（0正常 1停用）
     */
    @Schema(description = "菜单状态（0正常 1停用）")
    private String status;
    /**
     * 权限标识
     */
    @Schema(description = "权限标识")
    private String perms;
    /**
     * 菜单图标
     */
    @Schema(description = "菜单图标")
    private String icon;
}
