package com.actual_combat.base.core.pojo.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author yan
 * @date 2023/9/29 15:52
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户") @Accessors(chain = true)
public class UserInfo implements Serializable {
    private static final long serialVersionUID = 8715130069381530868L;
    @Schema(description = "id")
    private String id;
    @JsonIgnore
    @Schema(description = "账号")
    private String username;
    @Schema(description = "昵称")
    private String nickname;
    @JsonIgnore
    @Schema(description = "密码")
    private String password;
    @Schema(description = "头像")
    private String image;
    @JsonIgnore
    @Schema(description = "账号状态")
    private Boolean accountStatus = true;
    /**
     * 权限信息字符串
     */
    @JsonIgnore
    @Schema(description = "权限字符串")
    private List<String> roles;
}
