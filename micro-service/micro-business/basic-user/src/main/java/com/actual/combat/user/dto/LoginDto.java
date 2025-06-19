package com.actual.combat.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Author yan
 * @Date 2024/11/5 下午11:10:17
 * @Description
 */
@Data @Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto implements Serializable {
    private static final long serialVersionUID = 158109656970186395L;
    @Schema(description = "用户名")
    @NotBlank(message = "用户名不能为空")
    private String username;
    @Schema(description = "密码")
    @NotBlank(message = "密码不能为空")
    private String password;
    private Boolean captchaEnabled = true;
    @Schema(description = "使用管理端登录？")
    private Boolean isAdmin = true;
    @Schema(description = "验证码")
    //@NotBlank(message = "验证码不能为空")
    private String code;
    @Schema(description = "验证码唯一值")
    //@NotBlank(message = "验证码唯一值不能为空")
    private String uuid;
}
