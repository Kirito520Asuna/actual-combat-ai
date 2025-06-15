package com.actual_combat.basic.core.pojo.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Author yan
 * @Date 2024/11/5 上午12:01:57
 * @Description
 */
@Data @Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class TokenInfo implements Serializable {
    private static final long serialVersionUID = 1362569757290757470L;
    @Schema(description = "token名称")
    String tokenName;
    @Schema(description = "token值")
    String token;
    @Schema(description = "刷新token名称")
    String refreshTokenName;
    @Schema(description = "刷新token值")
    String refreshToken;
    @Schema(description = "是否开启双token")
    Boolean enableTwoToken;
}
