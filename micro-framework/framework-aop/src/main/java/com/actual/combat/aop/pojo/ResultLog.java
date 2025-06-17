package com.actual.combat.aop.pojo;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 通用返回对象封装类
 *
 * @author yan
 */
@Schema(description = "通用返回对象")
@Data
@NoArgsConstructor
@AllArgsConstructor @Accessors(chain = true)
public class ResultLog<T> implements Serializable {
    private static final long serialVersionUID = 8831550221241490078L;

    /**
     * 返回代码
     */
    @JsonProperty("code")
    @Schema(description = "返回代码", example = "200")
    private String code;
    /**
     * 返回信息
     */
    @JsonProperty("message")
    @Schema(description = "返回信息", example = "操作成功")
    private String message;

    /**
     *
     */
    @JsonProperty("resultTime")
    @Schema(description = "响应时间")
    private Long resultTime = System.currentTimeMillis();
    /**
     * 返回数据
     */
    @JsonProperty("data")
    @Schema(description = "返回数据")
    private T data;

}
