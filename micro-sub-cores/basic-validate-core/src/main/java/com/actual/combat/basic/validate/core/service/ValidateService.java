package com.actual.combat.basic.validate.core.service;


import com.actual.combat.basic.core.pojo.captcha.CaptchaInfo;

/**
 * @Author yan
 * @Date 2024/11/11 下午6:43:15
 * @Description 验证码处理
 */
public interface ValidateService {
    /**
     * 生成验证码
     */
    CaptchaInfo createCaptcha() ;
    /**
     * 校验验证码
     */
    void checkCaptcha(String code, String uuid);
}
