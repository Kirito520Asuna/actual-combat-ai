package com.actual.combat.user.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.actual.combat.aop.all.log.SysLog;
import com.actual.combat.aop.enums.BusinessType;
import com.actual.combat.aop.utils.poi.ExcelUtil;
import com.actual.combat.auth.service.AuthUserService;
import com.actual.combat.auth.shiro.aop.ShiroPermissions;
import com.actual.combat.basic.core.abs.auth.service.AbstractLoginService;
import com.actual.combat.basic.core.abs.auth.service.AbstractUserService;
import com.actual.combat.basic.core.enums.Header;
import com.actual.combat.basic.core.pojo.auth.User;
import com.actual.combat.basic.core.pojo.auth.UserInfo;
import com.actual.combat.basic.core.template.BasicController;
import com.actual.combat.basic.core.view.BaseJsonView;
import com.actual.combat.basic.core.vo.UserInfoVo;
import com.actual.combat.basic.core.vo.user.UserVo;
import com.actual.combat.basic.page.ResultPage;
import com.actual.combat.basic.result.Result;
import com.actual.combat.basic.user.core.service.SysUserService;
import com.actual.combat.basic.utils.bean.CustomBeanUtils;
import com.actual.combat.basic.utils.jwt.JwtUtils;
import com.actual.combat.basic.utils.object.ObjectUtils;
import com.actual.combat.mp.utils.PageUtils;
import com.actual.combat.user.domain.SysUser;
import com.fasterxml.jackson.annotation.JsonView;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @Author yan
 * @Date 2024/10/3 下午9:14:44
 * @Description
 */

@RestController
@Tag(name = "用户模块")
@RequestMapping(value = {"/api/user/", "/jwt/user/", "/user/"})
public class SysUserController implements BasicController {
    @Resource
    private SysUserService sysUserService;

    /**
     * 查询用户信息列表
     */
    @SysLog
    @Operation(summary = "查询用户信息列表")
    @ShiroPermissions("system:user:list")
    @GetMapping("list")
    public Result<ResultPage<SysUser>> list(@RequestParam(defaultValue = "1") Long pageNumber,
                                            @RequestParam(defaultValue = "10") Long pageSize) {

        SysUser sysUser = new SysUser();
        Map<String, Object> params = sysUser.toParams();
        params.put(PageUtils.pageNumber, pageNumber);
        params.put(PageUtils.pageSize, pageSize);
        PageUtils.startPage(params);
        List<SysUser> list = sysUserService.selectSysUserList(sysUser);
        return listToPage(list);
    }

    /**
     * 导出用户信息列表
     */
    @SysLog(businessType = BusinessType.EXPORT)
    @Operation(summary = "导出用户信息列表")
    @ShiroPermissions("system:user:export")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysUser sysUser) {
        PageUtils.startPage(sysUser.getParams());
        List<SysUser> list = sysUserService.selectSysUserList(sysUser);
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        util.exportExcel(response, list, "用户信息数据");
    }

    /**
     * 获取用户信息详细信息
     */
    @SysLog(title = "获取用户信息详细信息")
    @Operation(summary = "获取用户信息详细信息")
    @GetMapping(value = "getUserInfo")
    public Result<UserInfoVo> getUserInfo(@RequestParam(required = false) Long userId) {
        if (ObjectUtils.isEmpty(userId)) {
            userId = Long.parseLong(SpringUtil.getBean(AuthUserService.class).getUserId());
        }
        AbstractUserService bean = SpringUtil.getBean(AbstractUserService.class);
        List<String> roles = bean.getRolesById(userId);
        List<String> perms = bean.getPermsById(userId);

        SysUser user = sysUserService.selectSysUserByUserId(userId);
        UserInfoVo userInfoVo = new UserInfoVo()
                .setUserId(String.valueOf(user.getUserId()))
                .setAvatar(user.getAvatar())
                .setUsername(user.getUserName())
                .setPermissions(perms)
                .setRoles(roles);
        return ok(userInfoVo);
    }

    /**
     * 获取用户信息详细信息
     */
    @SysLog
    @Operation(summary = "获取用户信息详细信息")
    @ShiroPermissions("system:user:query")
    @GetMapping(value = "{userId}")
    public Result getInfo(@PathVariable("userId") Long userId) {
        return ok(sysUserService.selectSysUserByUserId(userId));
    }

    /**
     * 新增用户信息
     */
    @SysLog(businessType = BusinessType.INSERT)
    @Operation(summary = "新增用户信息")
    @ShiroPermissions("system:user:add")
    @PostMapping
    public Result add(@RequestBody SysUser sysUser) {
        return ok(sysUserService.save(sysUser));
    }

    /**
     * 修改用户信息
     */
    @SysLog(businessType = BusinessType.UPDATE)
    @Operation(summary = "修改用户信息")
    @ShiroPermissions("system:user:edit")
    @PutMapping
    public Result edit(@RequestBody SysUser sysUser) {
        return ok(sysUserService.updateById(sysUser));
    }

    /**
     * 删除用户信息
     */
    @SysLog(businessType = BusinessType.DELETE)
    @Operation(summary = "删除用户信息")
    @ShiroPermissions("system:user:remove")
    @DeleteMapping("{userIds}")
    public Result remove(@PathVariable Long[] userIds) {
        List<Long> userIdList = Arrays.stream(userIds).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(userIdList)) {
            sysUserService.deleteByUserIds(userIdList);
        }
        return ok();
    }

    //=========================================================================================
    private static UserVo createUserVo(SysUser user) {
        UserVo userVo = new UserVo();
        CustomBeanUtils.copyPropertiesIgnoreNull(user, userVo);
        return userVo;
    }

    @GetMapping("getOne")
    @Operation(summary = "获取当前登录用户[im]")
    @SysLog
    @JsonView(value = {BaseJsonView.WebView.class})
    public Result<UserVo> getOne(
            HttpServletRequest request) throws Exception {
        String token = request.getHeader(Header.TOKEN.getName());
        token = !StringUtils.hasText(token) ? request.getHeader(Header.AUTHORIZATION.getName()) : token;
        Claims claims = JwtUtils.parseJWT(token);
        String id = claims.getSubject();

        User oneRedis = SpringUtil.getBean(AbstractLoginService.class).getOneRedis(id);
        UserInfo user = oneRedis.getUser();
        SysUser user1 = sysUserService.getById(user.getId());
        UserVo userVo = createUserVo(user1);

        return ok(userVo);
    }

    @GetMapping("getUser")
    @Operation(summary = "获取用户[im]")
    @SysLog
    @JsonView(value = {BaseJsonView.WebView.class})
    public Result<UserVo> getUser(@Parameter(description = "用户id") @RequestParam Long userId) {
        SysUser user = sysUserService.getById(userId);
        UserVo userVo = createUserVo(user);
        return ok(userVo);
    }

    @GetMapping("getOneUser")
    @Operation(summary = "获取用户")
    @SysLog
    @JsonView(value = {BaseJsonView.UserView.class})
    public Result<UserVo> getOneUser(@Parameter(description = "用户id") @RequestParam(required = false) Long userId,
                                     @Parameter(description = "当前登录用户id") @RequestParam(required = false) Long nowUserId) {
        if (userId == null && nowUserId == null) {
            String userIdStr = SpringUtil.getBean(AuthUserService.class).getUserId();
            Long userIdLong = Long.parseLong(userIdStr);
            userId = nowUserId = userIdLong;
        }

        SysUser user = sysUserService.getOneUser(userId, nowUserId);
        UserVo userVo = createUserVo(user);
        return ok(userVo);
    }

    @GetMapping("getUsers")
    @Operation(summary = "搜索用户[im]")
    @SysLog
    @JsonView(value = {BaseJsonView.UserView.class})
    public Result<List<UserVo>> getUsers(@Parameter(description = "用户id") @RequestParam Long userId,
                                         @Parameter(description = "账号或者昵称") @RequestParam(required = false) String keyword) {
        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setUserName(keyword);
        user.setNickName(keyword);
        List<UserVo> users = sysUserService.getUsers(user)
                .stream().filter(ObjectUtils::isNotEmpty)
                .map(sysUser -> createUserVo(sysUser)).collect(Collectors.toList());

        return ok(users);
    }
}
