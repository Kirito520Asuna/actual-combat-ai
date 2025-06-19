package com.actual.combat.user.controller;


import cn.hutool.core.collection.CollUtil;
import com.actual.combat.aop.all.log.SysLog;
import com.actual.combat.aop.enums.BusinessType;
import com.actual.combat.aop.utils.poi.ExcelUtil;
import com.actual.combat.auth.shiro.aop.ShiroPermissions;
import com.actual.combat.basic.core.template.BasicController;
import com.actual.combat.basic.dict.core.service.SysDictDataService;
import com.actual.combat.basic.page.ResultPage;
import com.actual.combat.basic.result.Result;
import com.actual.combat.dict.domain.SysDictData;
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
 * 字典数据Controller
 *
 * @author ${author}
 * @date 2025-04-16
 */
@Tag(name = "字典数据")
@RestController
@RequestMapping({"/api/dict/data", "/jwt/dict/data", "/dict/data"})
public class SysDictDataController implements BasicController {
    @Resource
    private SysDictDataService dictDataService;

    /**
     * 查询字典数据列表
     */
    @SysLog
    @Operation(summary = "查询字典数据列表")
    @ShiroPermissions("minimalism:data:list")
    @GetMapping("/list")
    public Result<ResultPage<SysDictData>> list(@RequestParam(defaultValue = "1") Long pageNumber,
                                                @RequestParam(defaultValue = "10") Long pageSize) {
        SysDictData sysDictData = new SysDictData();
        Map<String, Object> params = sysDictData.getParams();
        params.put(PageUtils.pageNumber, pageNumber);
        params.put(PageUtils.pageSize, pageSize);
        PageUtils.startPage(params);
        List<SysDictData> list = dictDataService.selectSysDictDataList(sysDictData);
        return listToPage(list);
    }

    /**
     * 导出字典数据列表
     */
    @SysLog(businessType = BusinessType.EXPORT)
    @Operation(summary = "导出字典数据列表")
    @ShiroPermissions("minimalism:data:export")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysDictData sysDictData) {
        PageUtils.startPage(sysDictData.getParams());
        List<SysDictData> list = dictDataService.selectSysDictDataList(sysDictData);
        ExcelUtil<SysDictData> util = new ExcelUtil<SysDictData>(SysDictData.class);
        util.exportExcel(response, list, "字典数据");
    }


    /**
     * 获取字典数据详细信息
     */
    @SysLog
    @Operation(summary = "获取字典数据详细信息")
    @ShiroPermissions("minimalism:data:query")
    @GetMapping(value = "/{dictCode}")
    public Result<SysDictData> getInfo(@PathVariable("dictCode") Long dictCode) {
        return ok(dictDataService.getById(dictCode));
    }

    /**
     * 根据字典类型查询字典数据信息
     */
    @SysLog
    @Operation(summary = "根据字典类型查询字典数据信息")
    @GetMapping(value = "/type/{dictType}")
    public Result dictType(@PathVariable String dictType) {
        List<SysDictData> data = dictDataService.selectDictDataByType(dictType);
        return ok(data);
    }

    /**
     * 新增字典数据
     */
    @SysLog(businessType = BusinessType.INSERT)
    @Operation(summary = "新增字典数据")
    @ShiroPermissions("minimalism:data:add")
    @PostMapping
    public Result add(@RequestBody SysDictData sysDictData) {
        return ok(dictDataService.save(sysDictData));
    }

    /**
     * 修改字典数据
     */
    @SysLog(businessType = BusinessType.UPDATE)
    @Operation(summary = "修改字典数据")
    @ShiroPermissions("minimalism:data:edit")
    @PutMapping
    public Result edit(@RequestBody SysDictData sysDictData) {
        return ok(dictDataService.updateById(sysDictData));
    }

    /**
     * 删除字典数据
     */
    @SysLog(businessType = BusinessType.DELETE)
    @Operation(summary = "删除字典数据")
    @ShiroPermissions("minimalism:data:remove")
    @DeleteMapping("/{dictCodes}")
    public Result remove(@PathVariable Long[] dictCodes) {

        List<Long> dictCodeList = Arrays.stream(dictCodes).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(dictCodeList)) {
            dictDataService.deleteByDictCodes(dictCodeList);
        }
        return ok();
    }
}