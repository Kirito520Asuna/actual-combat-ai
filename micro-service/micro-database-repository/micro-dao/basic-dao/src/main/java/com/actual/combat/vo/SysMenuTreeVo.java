package com.actual.combat.vo;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.actual.combat.aop.abs.bean.AbsBean;
import com.actual.combat.aop.all.tree.Tree;
import com.actual.combat.aop.all.tree.TreeMap;
import com.actual.combat.aop.utils.tree.TreeUtils;
import com.actual.combat.mp.pojo.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author yan
 * @Date 2024/10/3 下午8:07:48
 * @Description
 */
@Data @Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class SysMenuTreeVo extends BaseEntity implements AbsBean {
    /**
     * 菜单ID
     */
    @Schema(description = "菜单ID")
    @Tree(id = true)
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
    @Tree(parentId = true )
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
    @Tree(subset = true)
    List<SysMenuTreeVo> children = CollUtil.newArrayList();

    @Override
    public void init() {
        AbsBean.super.init();
    }

    public static void main(String[] args) {
        ArrayList<SysMenuTreeVo> sysMenus = CollUtil.newArrayList(
                new SysMenuTreeVo().setMenuId(1l).setMenuName("fdgdg").setParentId(null),
                new SysMenuTreeVo().setMenuId(2l).setParentId(1l),
                new SysMenuTreeVo().setMenuId(3l).setParentId(1l),
                new SysMenuTreeVo().setMenuId(4l).setParentId(1l),

                new SysMenuTreeVo().setMenuId(5l).setParentId(2l),
                new SysMenuTreeVo().setMenuId(6l).setParentId(2l),
                new SysMenuTreeVo().setMenuId(7l).setParentId(2l),
                new SysMenuTreeVo().setMenuId(8l).setParentId(2l),

                new SysMenuTreeVo().setMenuId(5l + 4).setParentId(3l),
                new SysMenuTreeVo().setMenuId(6l + 4).setParentId(3l),
                new SysMenuTreeVo().setMenuId(7l + 4).setParentId(3l),
                new SysMenuTreeVo().setMenuId(8l + 4).setParentId(3l),

                new SysMenuTreeVo().setMenuId(8l + 4 + 1).setParentId(3l + 1)
        );

        List<Map<String, Object>> maps = TreeUtils.listToMapList(sysMenus);
        Class<SysMenuTreeVo> tClass = SysMenuTreeVo.class;

        List<SysMenuTreeVo> treeVos = TreeUtils.mapsToTree(maps, tClass, true, TreeMap.getTreeFieldMaps(tClass));
        System.err.println(JSONUtil.toJsonStr(treeVos));
    }
}
