package com.actual.combat.user.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cn.hutool.extra.spring.SpringUtil;
import com.actual.combat.aop.all.log.SysLog;
import com.actual.combat.aop.enums.BusinessType;
import com.actual.combat.aop.utils.poi.ExcelUtil;
import com.actual.combat.auth.service.AuthUserService;
import com.actual.combat.auth.shiro.aop.ShiroPermissions;
import com.actual.combat.basic.core.template.BasicController;
import com.actual.combat.basic.enums.ApiCode;
import com.actual.combat.basic.exceptions.GlobalCustomException;
import com.actual.combat.basic.page.ResultPage;
import com.actual.combat.basic.result.Result;
import com.actual.combat.basic.user.core.service.SysMenuService;
import com.actual.combat.basic.utils.jwt.JwtUtils;
import com.actual.combat.basic.utils.object.ObjectUtils;
import com.actual.combat.mp.utils.PageUtils;
import com.actual.combat.tomapper.SysMenuMapper;
import com.actual.combat.user.domain.SysMenu;
import com.actual.combat.vo.SysMenuTreeVo;
import com.actual.combat.vo.user.router.RouterVo;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import cn.hutool.core.collection.CollUtil;


import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;


/**
 * 菜单权限Controller
 *
 * @author ${author}
 * @date 2024-11-09
 */
@Tag(name = "菜单权限")
@RestController
@RequestMapping({"/api/menu", "/jwt/menu", "/menu"})
public class SysMenuController implements BasicController {
    @Resource
    private SysMenuService sysMenuService;

    /**
     * 查询菜单权限列表
     */
    @SysLog
    @Operation(summary = "查询菜单权限列表")
    @ShiroPermissions("system:menu:list")
    @GetMapping("/list")
    public Result<ResultPage<SysMenu>> list(@RequestParam(defaultValue = "1") Long pageNumber,
                                            @RequestParam(defaultValue = "10") Long pageSize) {
        SysMenu sysMenu = new SysMenu();
        Map<String, Object> params = sysMenu.getParams();
        params.put(PageUtils.pageNumber, pageNumber);
        params.put(PageUtils.pageSize, pageSize);
        PageUtils.startPage(params);
        List<SysMenu> list = sysMenuService.selectSysMenuList(sysMenu);
        return listToPage(list);
    }

    /**
     * 导出菜单权限列表
     */
    @SysLog(businessType = BusinessType.EXPORT)
    @Operation(summary = "导出菜单权限列表")
    @ShiroPermissions("system:menu:export")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysMenu sysMenu) {
        PageUtils.startPage(sysMenu.getParams());
        List<SysMenu> list = sysMenuService.selectSysMenuList(sysMenu);
        ExcelUtil<SysMenu> util = new ExcelUtil<SysMenu>(SysMenu.class);
        util.exportExcel(response, list, "菜单权限数据");
    }

    /**
     * 导入菜单权限
     */
    @SneakyThrows
    @SysLog(businessType = BusinessType.EXPORT)
    @Operation(summary = "导入菜单权限")
    @ShiroPermissions("system:menu:import")
    @PostMapping("/importData")
    public Result importData(MultipartFile file, boolean updateSupport) {
        ExcelUtil<SysMenu> util = new ExcelUtil<SysMenu>(SysMenu.class);
        List<SysMenu> sysMenuList = util.importExcel(file.getInputStream());
        String operName = SpringUtil.getBean(AuthUserService.class).getUserId();
        String message = sysMenuService.importDataSysMenu(sysMenuList, updateSupport, operName);
        return ok();
    }


    /**
     * 获取菜单权限详细信息
     */
    @SysLog
    @Operation(summary = "获取菜单权限详细信息")
    @ShiroPermissions("system:menu:query")
    @GetMapping(value = "/{menuId}")
    public Result getInfo(@PathVariable("menuId") Long menuId) {
        return ok(sysMenuService.selectSysMenuByMenuId(menuId));
    }

    /**
     * 新增菜单权限
     */
    @SysLog(businessType = BusinessType.INSERT)
    @Operation(summary = "新增菜单权限")
    @ShiroPermissions("system:menu:add")
    @PostMapping
    public Result add(@RequestBody SysMenu sysMenu) {
        return ok(sysMenuService.save(sysMenu));
    }

    /**
     * 修改菜单权限
     */
    @SysLog(businessType = BusinessType.UPDATE)
    @Operation(summary = "修改菜单权限")
    @ShiroPermissions("system:menu:edit")
    @PutMapping
    public Result edit(@RequestBody SysMenu sysMenu) {
        return ok(sysMenuService.updateById(sysMenu));
    }

    /**
     * 删除菜单权限
     */
    @SysLog(businessType = BusinessType.DELETE)
    @Operation(summary = "删除菜单权限")
    @ShiroPermissions("system:menu:remove")
    @DeleteMapping("/{menuIds}")
    public Result remove(@PathVariable Long[] menuIds) {

        List<Long> menuIdList = Arrays.stream(menuIds).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(menuIdList)) {
            sysMenuService.deleteByMenuIds(menuIdList);
        }
        return ok();
    }

    /**
     * 获取路由信息
     *
     * @return 路由信息
     */
    @SneakyThrows
    @SysLog
    @Operation(summary = "获取路由信息")
    @GetMapping("getRouters")
    public Result<List<RouterVo>> getRouters(@RequestParam(required = false) Long userId
            , @RequestParam(required = false) String token) {
        if (ObjectUtils.isNotEmpty(token)) {
            String subjectByParseJWT = JwtUtils.getSubjectByParseJWT(token);
            userId = Long.parseLong(subjectByParseJWT);
        }
        if (ObjectUtils.isEmpty(userId)) {
            userId = Long.parseLong(SpringUtil.getBean(AuthUserService.class).getUserId());
        }
        if (ObjectUtils.isEmpty(userId)) {
            throw new GlobalCustomException(ApiCode.UNAUTHORIZED);
        }

        List<SysMenuTreeVo> sysMenuTreeVos = sysMenuService.selectMenuTreeByUserId(userId);
        return ok(SysMenuMapper.buildMenus(sysMenuTreeVos));
    }
}
