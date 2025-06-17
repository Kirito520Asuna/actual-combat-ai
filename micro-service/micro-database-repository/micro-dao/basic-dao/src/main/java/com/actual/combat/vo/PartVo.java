package com.actual.combat.vo;

import com.actual.combat.basic.enums.OSType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.InputStream;

/**
 * @Author yan
 * @Date 2025/3/6 15:32:25
 * @Description
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class PartVo {
    @Schema(description = "url")
    private String url;
    @Schema(description = "本地资源")
    @JsonIgnore
    private String localResource;
    @Schema(description = "服务器所在上传文件夹")
    @JsonIgnore
    private String uploadDir;
    @Schema(description = "文件唯一值")
    private String identifier;
    @Schema(description = "是否本地资源")
    private Boolean local = Boolean.FALSE;
    @Schema(description = "输入流")
    private InputStream inputStream;
    @Schema(description = "服务器实例id")
    private String instanceId;
    @Schema(description = "系统")
    private OSType osType;
}
