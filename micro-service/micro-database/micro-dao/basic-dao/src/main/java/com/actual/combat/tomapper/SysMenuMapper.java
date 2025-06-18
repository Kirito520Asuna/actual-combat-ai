package com.actual.combat.tomapper;

import cn.hutool.core.util.StrUtil;
import com.actual.combat.basic.constant.Constants;
import com.actual.combat.basic.core.constant.user.UserConstants;
import com.actual.combat.basic.utils.bean.CustomBeanUtils;
import com.actual.combat.basic.utils.object.ObjectUtils;
import com.actual.combat.basic.utils.str.StrUtils;
import com.actual.combat.user.dto.SysMenuDto;
import com.actual.combat.user.domain.SysMenu;
import com.actual.combat.vo.SysMenuTreeVo;
import com.actual.combat.vo.user.router.MetaVo;
import com.actual.combat.vo.user.router.RouterVo;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author yan
 * @Date 2024/10/3 下午8:49:16
 * @Description
 */
public class SysMenuMapper {
    public static SysMenuTreeVo mapperToTreeVo(SysMenu sysMenu) {
        SysMenuTreeVo sysMenuTreeVo = new SysMenuTreeVo();
        CustomBeanUtils.copyPropertiesIgnoreNull(sysMenu, sysMenuTreeVo);
        return sysMenuTreeVo;
    }

    public static SysMenuDto mapperToDto(SysMenu sysMenu) {
        SysMenuDto sysMenuDto = new SysMenuDto();
        CustomBeanUtils.copyPropertiesIgnoreNull(sysMenu, sysMenuDto);
        return sysMenuDto;
    }

    public static SysMenu mapperToDao(SysMenuDto sysMenuDto) {
        SysMenu sysMenu = new SysMenu();
        CustomBeanUtils.copyPropertiesIgnoreNull(sysMenuDto, sysMenu);
        return sysMenu;
    }
    /*=============================================================================================================================================================================================*/

    /**
     * 获取路由名称
     *
     * @param menu 菜单信息
     * @return 路由名称
     */
    public static String getRouteName(SysMenuTreeVo menu) {
        // 非外链并且是一级目录（类型为目录）
        if (isMenuFrame(menu)) {
            return StrUtil.EMPTY;
        }
        return getRouteName(menu.getRouteName(), menu.getPath());
    }

    /**
     * 获取路由名称，如没有配置路由名称则取路由地址
     *
     * @param name 路由名称
     * @param path 路由地址
     * @return 路由名称（驼峰格式）
     */
    public static String getRouteName(String name, String path) {
        String routerName = StringUtils.isNotEmpty(name) ? name : path;
        return StringUtils.capitalize(routerName);
    }

    /**
     * 内链域名特殊字符替换
     *
     * @return 替换后的内链域名
     */
    public static String innerLinkReplaceEach(String path) {
        return StringUtils.replaceEach(path, new String[]{Constants.HTTP, Constants.HTTPS, Constants.WWW, ".", ":"},
                new String[]{"", "", "", "/", "/"});
    }

    /**
     * 获取路由地址
     *
     * @param menu 菜单信息
     * @return 路由地址
     */
    public static String getRouterPath(SysMenuTreeVo menu) {
        String routerPath = menu.getPath();
        // 内链打开外网方式
        if ((menu.getParentId() != null && menu.getParentId().intValue() != 0) && isInnerLink(menu)) {
            routerPath = innerLinkReplaceEach(routerPath);
        }
        // 非外链并且是一级目录（类型为目录）
        if ((menu.getParentId() == null || 0 == menu.getParentId().intValue()) && UserConstants.TYPE_DIR.equals(menu.getMenuType())
                && UserConstants.NO_FRAME.equals(menu.getIsFrame())) {
            routerPath = "/" + menu.getPath();
        }
        // 非外链并且是一级目录（类型为菜单）
        else if (isMenuFrame(menu)) {
            routerPath = "/";
        }
        return routerPath;
    }

    /**
     * 获取组件信息
     *
     * @param menu 菜单信息
     * @return 组件信息
     */
    public static String getComponent(SysMenuTreeVo menu) {
        String component = UserConstants.LAYOUT;
        if (StrUtils.isNotEmpty(menu.getComponent()) && !isMenuFrame(menu)) {
            component = menu.getComponent();
        } else if (StrUtils.isEmpty(menu.getComponent()) && (menu.getParentId() != null && menu.getParentId().intValue() != 0) && isInnerLink(menu)) {
            component = UserConstants.INNER_LINK;
        } else if (StrUtils.isEmpty(menu.getComponent()) && isParentView(menu)) {
            component = UserConstants.PARENT_VIEW;
        }
        return component;
    }

    /**
     * 是否为菜单内部跳转
     *
     * @param menu 菜单信息
     * @return 结果
     */
    public static boolean isMenuFrame(SysMenuTreeVo menu) {
        return (menu.getParentId() == null || menu.getParentId().intValue() == 0) && UserConstants.TYPE_MENU.equals(menu.getMenuType())
                && menu.getIsFrame().equals(UserConstants.NO_FRAME);
    }

    /**
     * 是否为内链组件
     *
     * @param menu 菜单信息
     * @return 结果
     */
    public static boolean isInnerLink(SysMenuTreeVo menu) {
        return menu.getIsFrame().equals(UserConstants.NO_FRAME) && StrUtils.isHttp(menu.getPath());
    }

    /**
     * 是否为parent_view组件
     *
     * @param menu 菜单信息
     * @return 结果
     */
    public static boolean isParentView(SysMenuTreeVo menu) {
        return (menu.getParentId() != null && menu.getParentId().intValue() != 0) && UserConstants.TYPE_DIR.equals(menu.getMenuType());
    }


    /**
     * 构建前端路由所需要的菜单
     *
     * @param menus 菜单列表
     * @return 路由列表
     */
    public static List<RouterVo> buildMenus(List<SysMenuTreeVo> menus) {
        List<RouterVo> routers = new LinkedList<RouterVo>();
        for (SysMenuTreeVo menu : menus) {
            RouterVo router = new RouterVo();
            router.setHidden("1".equals(menu.getVisible()));
            router.setName(getRouteName(menu));
            router.setPath(getRouterPath(menu));
            router.setComponent(getComponent(menu));
            router.setQuery(menu.getQuery());
            router.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), ObjectUtils.equals(1, menu.getIsCache()), menu.getPath()));
            List<SysMenuTreeVo> cMenus = menu.getChildren();
            if (ObjectUtils.isNotEmpty(cMenus) && UserConstants.TYPE_DIR.equals(menu.getMenuType())) {
                router.setAlwaysShow(true);
                router.setRedirect("noRedirect");
                router.setChildren(buildMenus(cMenus));
            } else if (isMenuFrame(menu)) {
                router.setMeta(null);
                List<RouterVo> childrenList = new ArrayList<RouterVo>();
                RouterVo children = new RouterVo();
                children.setPath(menu.getPath());
                children.setComponent(menu.getComponent());
                children.setName(getRouteName(menu.getRouteName(), menu.getPath()));
                children.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), ObjectUtils.equals(1, menu.getIsCache()), menu.getPath()));
                children.setQuery(menu.getQuery());
                childrenList.add(children);
                router.setChildren(childrenList);
            } else if ((menu.getParentId() == null || menu.getParentId().intValue() == 0) && isInnerLink(menu)) {
                router.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon()));
                router.setPath("/");
                List<RouterVo> childrenList = new ArrayList<RouterVo>();
                RouterVo children = new RouterVo();
                String routerPath = innerLinkReplaceEach(menu.getPath());
                children.setPath(routerPath);
                children.setComponent(UserConstants.INNER_LINK);
                children.setName(getRouteName(menu.getRouteName(), routerPath));
                children.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), menu.getPath()));
                childrenList.add(children);
                router.setChildren(childrenList);
            }
            routers.add(router);
        }
        return routers;
    }
    /*=============================================================================================================================================================================================*/

}
