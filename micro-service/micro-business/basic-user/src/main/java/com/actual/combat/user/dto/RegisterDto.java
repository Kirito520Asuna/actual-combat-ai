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
 * @Date 2024/11/5 下午11:38:31
 * @Description
 */
@Data @Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDto implements Serializable {
    private static final long serialVersionUID = 1247623984374153422L;
    @Schema(description = "用户昵称")
    @NotBlank(message = "用户昵称不能为空")
    private String nickname;
    @Schema(description = "用户密码")
    @NotBlank(message = "用户密码不能为空")
    private String password;
    @Schema(description = "确认密码")
    @NotBlank(message = "确认密码不能为空")
    private String password2;
}
