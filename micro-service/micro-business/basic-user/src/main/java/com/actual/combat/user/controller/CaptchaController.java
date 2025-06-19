package com.actual.combat.user.controller;

import com.actual.combat.aop.all.log.SysLog;
import com.actual.combat.basic.core.pojo.captcha.CaptchaInfo;
import com.actual.combat.basic.core.template.BasicController;
import com.actual.combat.basic.result.Result;
import com.actual.combat.basic.validate.core.service.ValidateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @Author yan
 * @Date 2024/11/11 下午6:33:12
 * @Description
 */
@RestController
@Tag(name = "验证码模块")
@RequestMapping(value = {"/api/captcha","/captcha"})
public class CaptchaController implements BasicController {
    @Resource
    private ValidateService validateService;
    @SysLog
    @Operation(summary = "获取验证码")
    @GetMapping("/getImgCode")
    public Result<CaptchaInfo> getCaptcha() {
        CaptchaInfo captcha = validateService.createCaptcha();
        return ok(captcha);
    }
}
