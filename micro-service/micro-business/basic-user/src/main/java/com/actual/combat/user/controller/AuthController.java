package com.actual.combat.user.controller;

import cn.hutool.extra.spring.SpringUtil;
import com.actual.combat.aop.all.aviator.AviatorNotBlank;
import com.actual.combat.aop.all.aviator.AviatorValid;
import com.actual.combat.aop.all.aviator.AviatorValids;
import com.actual.combat.aop.all.log.SysLog;
import com.actual.combat.basic.core.abs.auth.service.AbstractLoginService;
import com.actual.combat.basic.core.pojo.auth.TokenInfo;
import com.actual.combat.basic.core.pojo.auth.User;
import com.actual.combat.basic.core.pojo.auth.UserInfo;
import com.actual.combat.basic.core.template.BasicController;
import com.actual.combat.basic.result.Result;
import com.actual.combat.basic.utils.object.ObjectUtils;
import com.actual.combat.basic.validate.core.service.ValidateService;
import com.actual.combat.user.dto.LoginDto;
import com.actual.combat.user.dto.RegisterDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * @Author yan
 * @Date 2024/11/5 下午11:04:45
 * @Description
 */
@RestController
@Tag(name = "登陆注册模块")
@RequestMapping(value = {"/api/auth", "/jwt/auth", "/auth"})
public class AuthController implements BasicController {
    /**
     * 查询用户信息列表
     */
    @SysLog
    @Operation(summary = "登陆")
    @PostMapping("/login")
    @AviatorValids(
            notBlanks = {
                    @AviatorNotBlank(key = "arg0.username", errorMessage = "用户名不能为空"),
                    @AviatorNotBlank(key = "arg0.password", errorMessage = "密码不能为空"),
                    @AviatorNotBlank(precondition = "arg0.isAdmin",key = "arg0.code", errorMessage = "验证码不能为空"),
                    @AviatorNotBlank(precondition = "arg0.isAdmin",key = "arg0.uuid", errorMessage = "验证码唯一值不能为空"),
            },
            values = {
                    @AviatorValid(precondition = "arg0.isAdmin",expression = "arg0.captchaEnabled==true && arg0.code!=null && arg0.code!=''", errorMessage = "验证码不能为空"),
            }
    )
    public Result<TokenInfo> login(@RequestBody LoginDto dto, HttpServletResponse response) {

        if (ObjectUtils.defaultIfEmpty(dto.getIsAdmin(),true)){
            SpringUtil.getBean(ValidateService.class)
                    .checkCaptcha(dto.getCode(), dto.getUuid());
        }

        UserInfo userInfo = new UserInfo().setPassword(dto.getPassword()).setUsername(dto.getUsername());
        TokenInfo tokenInfo = SpringUtil.getBean(AbstractLoginService.class).login(userInfo);
        response.setHeader(tokenInfo.getTokenName(), tokenInfo.getToken());
        if (tokenInfo.getEnableTwoToken()) {
            response.setHeader(tokenInfo.getRefreshTokenName(), tokenInfo.getRefreshToken());
        }
        return ok(tokenInfo);
    }

    @SysLog
    @Operation(summary = "注册")
    @PostMapping("/register")
    @AviatorValids(values = {
            @AviatorValid(expression = "arg0.password == arg0.password2", errorMessage = "密码不一致")
    })
    public Result<User> register(@Validated @NotNull @RequestBody RegisterDto dto) {
        User register = SpringUtil.getBean(AbstractLoginService.class)
                .register(dto.getNickname(), dto.getPassword(), dto.getPassword2());
        return ok(register);
    }

    @SysLog
    @Operation(summary = "登出")
    @PostMapping("/logout")
    public Result logout() {
        SpringUtil.getBean(AbstractLoginService.class).logout();
        return ok();
    }
}
