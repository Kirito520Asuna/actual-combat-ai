package com.actual.combat.user.controller;

import cn.hutool.core.collection.CollUtil;
import com.actual.combat.aop.all.log.SysLog;
import com.actual.combat.aop.enums.BusinessType;
import com.actual.combat.aop.utils.poi.ExcelUtil;
import com.actual.combat.auth.shiro.aop.ShiroPermissions;
import com.actual.combat.basic.core.template.BasicController;
import com.actual.combat.basic.dict.core.service.SysDictTypeService;
import com.actual.combat.basic.page.ResultPage;
import com.actual.combat.basic.result.Result;
import com.actual.combat.dict.domain.SysDictType;
import com.actual.combat.mp.utils.PageUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 字典类型Controller
 *
 * @author yan
 * @date 2025-04-16
 */
@Tag(name = "字典类型")
@RestController
@RequestMapping({"/api/dict/type", "/jwt/dict/type", "/dict/type"})
public class SysDictTypeController implements BasicController {
    @Resource
    private SysDictTypeService dictTypeService;

    /**
     * 查询字典类型列表
     */
    @SysLog
    @Operation(summary = "查询字典类型列表")
    @ShiroPermissions("minimalism:dict:list")
    @GetMapping("/list")
    public Result<ResultPage<SysDictType>> list(@RequestParam(defaultValue = "1") Long pageNumber,
                                                @RequestParam(defaultValue = "10") Long pageSize) {
        SysDictType sysDictType = new SysDictType();
        Map<String, Object> params = sysDictType.getParams();
        params.put(PageUtils.pageNumber, pageNumber);
        params.put(PageUtils.pageSize, pageSize);
        PageUtils.startPage(params);
        List<SysDictType> list = dictTypeService.selectSysDictTypeList(sysDictType);
        return listToPage(list);
    }

    /**
     * 导出字典类型列表
     */
    @SysLog(businessType = BusinessType.EXPORT)
    @Operation(summary = "导出字典类型列表")
    @ShiroPermissions("minimalism:dict:export")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysDictType sysDictType) {
        PageUtils.startPage(sysDictType.getParams());
        List<SysDictType> list = dictTypeService.selectSysDictTypeList(sysDictType);
        ExcelUtil<SysDictType> util = new ExcelUtil<SysDictType>(SysDictType.class);
        util.exportExcel(response, list, "字典类型数据");
    }


    /**
     * 获取字典类型详细信息
     */
    @SysLog
    @Operation(summary = "获取字典类型详细信息")
    @ShiroPermissions("minimalism:dict:query")
    @GetMapping(value = "/{dictId}")
    public Result<SysDictType> getInfo(@PathVariable("dictId") Long dictId) {
        return ok(dictTypeService.getById(dictId));
    }

    /**
     * 新增字典类型
     */
    @SysLog(businessType = BusinessType.INSERT)
    @Operation(summary = "新增字典类型")
    @ShiroPermissions("minimalism:dict:add")
    @PostMapping
    public Result add(@RequestBody SysDictType dictType) {
        if (!dictTypeService.checkDictTypeUnique(dictType)) {
            return fail("新增字典'" + dictType.getDictName() + "'失败，字典类型已存在");
        }
        return ok(dictTypeService.insertDictType(dictType));
    }

    /**
     * 修改字典类型
     */
    @SysLog(businessType = BusinessType.UPDATE)
    @Operation(summary = "修改字典类型")
    @ShiroPermissions("minimalism:dict:edit")
    @PutMapping
    public Result edit(@RequestBody SysDictType dictType) {
        if (!dictTypeService.checkDictTypeUnique(dictType)) {
            return fail("修改字典'" + dictType.getDictName() + "'失败，字典类型已存在");
        }
        return ok(dictTypeService.updateById(dictType));
    }

    /**
     * 删除字典类型
     */
    @SysLog(businessType = BusinessType.DELETE)
    @Operation(summary = "删除字典类型")
    @ShiroPermissions("minimalism:dict:remove")
    @DeleteMapping("/{dictIds}")
    public Result remove(@PathVariable Long[] dictIds) {

        List<Long> dictIdList = Arrays.stream(dictIds).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(dictIdList)) {
            dictTypeService.deleteByDictIds(dictIdList);
        }
        return ok();
    }

    /**
     * 刷新字典缓存
     */
    @ShiroPermissions("minimalism:dict:remove")
    @Operation(summary = "刷新字典缓存")
    @SysLog(businessType = BusinessType.CLEAN)
    @DeleteMapping("/refreshCache")
    public Result refreshCache() {
        dictTypeService.resetDictCache();
        return ok();
    }

    /**
     * 获取字典选择框列表
     */
    @GetMapping("/optionselect")
    public Result optionselect() {
        List<SysDictType> dictTypes = dictTypeService.list();
        return ok(dictTypes);
    }
}
