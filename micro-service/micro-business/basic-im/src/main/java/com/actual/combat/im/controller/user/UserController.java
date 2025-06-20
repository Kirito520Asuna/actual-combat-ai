package com.actual.combat.im.controller.user;

import com.actual.combat.aop.all.log.SysLog;
import com.actual.combat.basic.core.template.BasicController;
import com.actual.combat.basic.core.view.BaseJsonView;
import com.actual.combat.basic.core.vo.user.UserVo;
import com.actual.combat.basic.im.core.service.im.ApplyService;
import com.actual.combat.basic.im.core.service.im.FriendService;
import com.actual.combat.basic.result.Result;
import com.actual.combat.basic.user.core.service.SysUserService;
import com.actual.combat.basic.utils.bean.CustomBeanUtils;
import com.actual.combat.basic.utils.object.ObjectUtils;
import com.actual.combat.im.domain.im.Apply;
import com.actual.combat.im.domain.im.Friend;
import com.actual.combat.user.domain.SysUser;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


/**
 * @Author minimalism
 * @Date 2023/8/9 0009 14:53
 * @Description
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping(value = {"/api/user/", "/jwt/user/", "/user/"})
public class UserController implements BasicController {
    @Resource
    private SysUserService userService;
    @Resource
    private ApplyService applyService;
    @Resource
    private FriendService friendService;

    private static UserVo createUserVo(SysUser user) {
        UserVo userVo = new UserVo();
        CustomBeanUtils.copyPropertiesIgnoreNull(user, userVo);
        return userVo;
    }

    @GetMapping("getFriends")
    @Operation(summary = "获取好友")
    @SysLog(title = "获取好友")
    @JsonView(value = {BaseJsonView.WebView.class})
    public Result<List<UserVo>> getFriends(@Parameter(description = "用户id") @RequestParam Long userId,
                                           @Parameter(description = "账号或者昵称") @RequestParam(required = false) String keyword) {
        List<UserVo> users = friendService.getFriends(userId, keyword);
        return ok(users);
    }


    @PostMapping("apply")
    @Operation(summary = "申请添加好友")
    @SysLog(title = "申请添加好友")
    public Result apply(@Validated(BaseJsonView.ApplyView.class)
                        @JsonView(BaseJsonView.ApplyView.class)
                        @RequestBody Apply apply) {
        LambdaQueryWrapper<Apply> la = new LambdaQueryWrapper<>();
        la.eq(Apply::getUid, apply.getUid()).eq(Apply::getTid, apply.getTid());
        int count = (int) applyService.count(la);

        if (count > 0){
            return fail("该用户已向您发起了好友申请！");
        }

        LambdaQueryWrapper<Friend> friendLambdaQueryWrapper = new LambdaQueryWrapper<>();
        friendLambdaQueryWrapper.eq(Friend::getUid, apply.getUid()).eq(Friend::getFid, apply.getTid());
        count = (int) friendService.count(friendLambdaQueryWrapper);
        if (count > 0) {
            return fail("已添加过该好友！");
        }
        applyService.saveOrUpdate(apply);
        return ok();
    }

    @PostMapping("apply/agree")
    @Operation(summary = "是否同意添加好友")
    @SysLog(title = "是否同意添加好友")
    @JsonView(BaseJsonView.ApplyAgreeView.class)
    public Result<UserVo> applyAgree(@Validated(BaseJsonView.UpdateView.class)
                             @JsonView(BaseJsonView.UpdateView.class)
                             @RequestBody Apply apply) {
        UserVo user = applyService.applyAgree(apply);
        return ok(user);
    }

    @GetMapping("apply/list")
    @Operation(summary = "获取申请列表")
    @SysLog(title = "获取申请列表")
    @JsonView(BaseJsonView.ApplyView.class)
    public Result<List<UserVo>> applyList(@RequestParam Long userId) {
        List<SysUser> list = userService.applyList(userId);
        List<UserVo> userVoList = list.stream().filter(ObjectUtils::isNotEmpty)
                .map(sysUser -> createUserVo(sysUser)).collect(Collectors.toList());
        return ok(userVoList);
    }

}
