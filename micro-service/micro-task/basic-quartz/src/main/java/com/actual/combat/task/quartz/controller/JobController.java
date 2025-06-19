package com.actual.combat.task.quartz.controller;


import cn.hutool.core.collection.CollUtil;
import com.actual.combat.aop.all.log.SysLog;
import com.actual.combat.aop.enums.BusinessType;
import com.actual.combat.aop.utils.poi.ExcelUtil;
import com.actual.combat.auth.shiro.aop.ShiroPermissions;
import com.actual.combat.basic.core.template.BasicController;
import com.actual.combat.basic.job.core.service.SysJobService;
import com.actual.combat.basic.page.ResultPage;
import com.actual.combat.basic.result.Result;
import com.actual.combat.job.domain.SysJob;
import com.actual.combat.mp.utils.PageUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.quartz.SchedulerException;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//import org.springframework.security.access.prepost.PreAuthorize;

@Tag(description = "定时任务管理", name = "定时任务管理")
@RestController
@RequestMapping({"/api/job", "/jwt/job", "/job"})
public class JobController implements BasicController {
    @Resource
    private SysJobService sysJobService;

    /**
     * 查询定时任务调度列表
     */
    @SysLog(title = "查询定时任务调度列表")
    @Operation(summary = "查询定时任务调度列表")
    //@PreAuthorize("@custom.hasAuthority('actual:job:list')")
    @ShiroPermissions("actual:job:list")
    @GetMapping("/list")
    public Result<ResultPage<SysJob>> list(@RequestParam(defaultValue = "1") Long pageNumber,
                                           @RequestParam(defaultValue = "10") Long pageSize) {
        SysJob sysJob = new SysJob();
        Map<String, Object> params = sysJob.getParams();
        params.put(PageUtils.pageNumber, pageNumber);
        params.put(PageUtils.pageSize, pageSize);
        PageUtils.startPage(params);
        List<SysJob> list = sysJobService.selectJobList(sysJob);
        return listToPage(list);
    }

    /**
     * 导出定时任务调度列表
     */
    @SysLog(title = "导出定时任务调度列表", businessType = BusinessType.EXPORT)
    @Operation(summary = "导出定时任务调度列表")
    @ShiroPermissions("actual:job:export")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysJob sysJob) {
        PageUtils.startPage(sysJob.getParams());
        List<SysJob> list = sysJobService.selectJobList(sysJob);
        ExcelUtil<SysJob> util = new ExcelUtil<SysJob>(SysJob.class);
        util.exportExcel(response, list, "定时任务调度数据");
    }

    /**
     * 获取定时任务调度详细信息
     */
    @SysLog(title = "获取定时任务调度详细信息")
    @Operation(summary = "获取定时任务调度详细信息")
    @ShiroPermissions("actual:job:query")
    @GetMapping(value = "/{jobId}")
    public Result<SysJob> getInfo(@PathVariable("jobId") Long jobId) {
        return ok(sysJobService.getById(jobId));
    }

    /**
     * 新增定时任务调度
     */
    @SysLog(title = "新增定时任务调度", businessType = BusinessType.INSERT)
    @Operation(summary = "新增定时任务调度")
    @ShiroPermissions("actual:job:add")
    @PostMapping
    public Result add(@RequestBody SysJob sysJob) {
        return ok(sysJobService.save(sysJob));
    }

    /**
     * 修改定时任务调度
     */
    @SysLog(title = "修改定时任务调度", businessType = BusinessType.UPDATE)
    @Operation(summary = "修改定时任务调度")
    @ShiroPermissions("actual:job:edit")
    @PutMapping
    public Result edit(@RequestBody SysJob sysJob) {
        return ok(sysJobService.updateById(sysJob));
    }

    /**
     * 定时任务状态修改
     */
    @ShiroPermissions("actual:job:changeStatus")
    @SysLog(businessType = BusinessType.UPDATE)
    @Operation(summary = "定时任务状态修改")
    @PutMapping("/changeStatus")
    public Result changeStatus(@RequestBody SysJob job) throws SchedulerException {
        SysJob newJob = sysJobService.getById(job.getJobId());
        newJob.setStatus(job.getStatus());
        return ok(sysJobService.changeStatus(newJob));
    }

    /**
     * 定时任务立即执行一次
     */
    @ShiroPermissions("actual:job:changeStatus")
    @SysLog(businessType = BusinessType.UPDATE)
    @Operation(summary = "定时任务立即执行一次")
    @PutMapping("/run")
    public Result run(@RequestBody SysJob job) throws SchedulerException {
        boolean result = sysJobService.run(job);
        return result ? ok() : fail("任务不存在或已过期！");
    }

    /**
     * 删除定时任务调度
     */
    @SysLog(title = "删除定时任务调度", businessType = BusinessType.DELETE)
    @Operation(summary = "删除定时任务调度")
    @ShiroPermissions("actual:job:remove")
    @DeleteMapping("/{jobIds}")
    public Result remove(@PathVariable Long[] jobIds) throws SchedulerException {

        List<Long> jobIdList = Arrays.stream(jobIds).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(jobIdList)) {
            sysJobService.deleteJobByIds(jobIdList);
        }
        return ok();
    }
}
