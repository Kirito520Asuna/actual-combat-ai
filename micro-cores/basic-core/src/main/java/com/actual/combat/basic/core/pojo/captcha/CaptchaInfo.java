package com.actual.combat.basic.core.pojo.captcha;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Author yan
 * @Date 2024/11/11 下午6:36:39
 * @Description
 */
@Data @Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class CaptchaInfo {
    private String uuid;
    private String img;
    private Boolean captchaEnabled = true;
}
