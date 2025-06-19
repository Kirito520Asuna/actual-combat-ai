package com.actual.combat.user.controller;

import cn.hutool.core.collection.CollUtil;
import com.actual.combat.aop.all.log.SysLog;
import com.actual.combat.aop.enums.BusinessType;
import com.actual.combat.aop.utils.poi.ExcelUtil;
import com.actual.combat.auth.shiro.aop.ShiroPermissions;
import com.actual.combat.basic.core.constant.user.UserConstants;
import com.actual.combat.basic.core.template.BasicController;
import com.actual.combat.basic.dept.core.service.SysDeptAncestorService;
import com.actual.combat.basic.dept.core.service.SysDeptService;
import com.actual.combat.basic.exceptions.BusinessException;
import com.actual.combat.basic.page.ResultPage;
import com.actual.combat.basic.result.Result;
import com.actual.combat.basic.utils.object.ObjectUtils;
import com.actual.combat.dept.domain.SysDept;
import com.actual.combat.dept.domain.SysDeptAncestor;
import com.actual.combat.mp.utils.PageUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 部门Controller
 *
 * @author ${author}
 * @date 2025-04-16
 */
@Tag(name = "部门")
@RestController
@RequestMapping({"/api/dept", "/jwt/dept", "/dept"})
public class SysDeptController implements BasicController {
    @Resource
    private SysDeptService deptService;
    @Resource
    private SysDeptAncestorService deptAncestorService;

    /**
     * 查询部门列表
     */
    @SysLog
    @Operation(summary = "查询部门列表")
    @ShiroPermissions("system:dept:list")
    @GetMapping("/list")
    public Result<ResultPage<SysDept>> list(@RequestParam(defaultValue = "1") Long pageNumber,
                                            @RequestParam(defaultValue = "10") Long pageSize) {
        SysDept sysDept = new SysDept();
        Map<String, Object> params = sysDept.getParams();
        params.put(PageUtils.pageNumber, pageNumber);
        params.put(PageUtils.pageSize, pageSize);
        PageUtils.startPage(params);
        List<SysDept> list = deptService.selectDeptList(sysDept);
        return listToPage(list);
    }

    /**
     * 查询部门列表（排除节点）
     */
    @SysLog
    @Operation(summary = "查询部门列表（排除节点）")
    @ShiroPermissions("system:dept:list")
    @GetMapping("/list/exclude/{deptId}")
    public Result excludeChild(@PathVariable(value = "deptId", required = false) Long deptId) {
        List<SysDept> depts = deptService.selectDeptList(new SysDept());
        depts.removeIf(d -> ObjectUtils.equal(d.getDeptId(), deptId) ||
                CollUtil.contains(deptAncestorService.selectDeptAncestorList(new SysDeptAncestor().setDeptId(deptId))
                                .stream().map(SysDeptAncestor::getDeptParentId).collect(Collectors.toList()),
                        String.valueOf(deptId)
                ));
        return ok(depts);
    }

    /**
     * 获取部门详细信息
     */
    @SysLog
    @Operation(summary = "获取部门详细信息")
    @ShiroPermissions("system:dept:query")
    @GetMapping(value = "/{deptId}")
    public Result<SysDept> getInfo(@PathVariable("deptId") Long deptId) {
        deptService.checkDeptDataScope(deptId);
        return ok(deptService.getById(deptId));
    }

    /**
     * 导出部门列表
     */
    @SysLog(businessType = BusinessType.EXPORT)
    @Operation(summary = "导出部门列表")
    @ShiroPermissions("system:dept:export")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysDept sysDept) {
        PageUtils.startPage(sysDept.getParams());
        List<SysDept> list = deptService.selectDeptList(sysDept);
        ExcelUtil<SysDept> util = new ExcelUtil<SysDept>(SysDept.class);
        util.exportExcel(response, list, "部门数据");
    }


    /**
     * 新增部门
     */
    @SysLog(businessType = BusinessType.INSERT)
    @Operation(summary = "新增部门")
    @ShiroPermissions("system:dept:add")
    @PostMapping
    public Result add(@Validated @RequestBody SysDept sysDept) {
        boolean checkDeptNameUnique = deptService.checkDeptNameUnique(sysDept);
        if (!checkDeptNameUnique) {
            throw new BusinessException("新增部门'" + sysDept.getDeptName() + "'失败，部门名称已存在");
        }
        return ok(deptService.saveDept(sysDept));
    }

    /**
     * 修改部门
     */
    @SysLog(businessType = BusinessType.UPDATE)
    @Operation(summary = "修改部门")
    @ShiroPermissions("system:dept:edit")
    @PutMapping
    public Result edit(@Validated @RequestBody SysDept sysDept) {
        Long deptId = sysDept.getDeptId();
        deptService.checkDeptDataScope(deptId);
        boolean checkDeptNameUnique = deptService.checkDeptNameUnique(sysDept);
        if (!checkDeptNameUnique) {
            throw new BusinessException("修改部门'" + sysDept.getDeptName() + "'失败，部门名称已存在");
        } else if (ObjectUtils.equals(deptId, sysDept.getParentId())) {
            throw new BusinessException("修改部门'" + sysDept.getDeptName() + "'失败，上级部门不能是自己");
        } else if (ObjectUtils.equals(UserConstants.DEPT_DISABLE, sysDept.getStatus())
                && deptService.selectNormalChildrenDeptById(deptId) > 0) {
            throw new BusinessException("该部门包含未停用的部门");
        }
        return ok(deptService.updateDept(sysDept));
    }

    /**
     * 删除部门
     */
    @SysLog(businessType = BusinessType.DELETE)
    @Operation(summary = "删除部门")
    @ShiroPermissions("system:dept:remove")
    @DeleteMapping("/{deptIds}")
    public Result remove(@PathVariable Long[] deptIds) {

        List<Long> deptIdList = Arrays.stream(deptIds).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(deptIdList)) {
            boolean hasChild = deptService.hasChildByDeptIds(deptIdList);
            if (hasChild) {
                throw new BusinessException("存在下部部门,不允许删除");
            }
            boolean checkDeptExistUser = deptService.checkDeptExistUser(deptIdList);
            if (checkDeptExistUser) {
                throw new BusinessException("部门存在用户,不允许删除");
            }
            deptService.checkDeptDataScope(deptIdList);
            deptService.deleteByDeptIds(deptIdList);
        }
        return ok();
    }
}