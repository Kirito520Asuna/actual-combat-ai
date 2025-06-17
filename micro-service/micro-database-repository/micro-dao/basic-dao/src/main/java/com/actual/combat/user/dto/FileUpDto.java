package com.actual.combat.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Author yan
 * @Date 2025/3/6 上午1:13:42
 * @Description
 */
@AllArgsConstructor
@NoArgsConstructor
@Data @Accessors(chain = true)
public class FileUpDto implements Serializable {
    private static final long serialVersionUID = 5360609118081008199L;
    @JsonProperty("fileName")
    @Schema(description = "文件名")
    private String fileName;
    @JsonProperty("suffix")
    @Schema(description = "后缀名")
    private String suffix;
    @JsonProperty("img")
    @Schema(description = "是图片？")
    private Boolean img = Boolean.FALSE;
    @JsonProperty("size")
    @Schema(description = "文件大小")
    private Long size;
    @JsonProperty("type")
    @Schema(description = "文件类型")
    private String type;
    @JsonProperty("dir")
    @Schema(description = "是文件夹？")
    private Boolean dir = Boolean.TRUE;
}
