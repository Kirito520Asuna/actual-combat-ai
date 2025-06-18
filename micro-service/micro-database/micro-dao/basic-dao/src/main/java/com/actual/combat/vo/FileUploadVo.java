package com.actual.combat.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.UUID;

/**
 * @Author yan
 * @Date 2025/3/7 0:03:27
 * @Description
 */
@Data @Accessors(chain = true)
@NoArgsConstructor @SuperBuilder
@AllArgsConstructor
public class FileUploadVo implements Serializable {
    private static final long serialVersionUID = 5911393363016920061L;
    @Schema(description = "文件编号")
    private Long fileId;
    @Schema(description = "分片数")
    private int totalChunks;
    @Schema(description = "分片上限大小/每份")
    private int chunkSize;
    @Schema(description = "起始分片编号")
    private int chunkNumber = 1;
    @Schema(description = "文件大小")
    private Long totalFileSize;
    @Schema(description = "唯一标识")
    private String identifier = UUID.randomUUID().toString() + System.currentTimeMillis();
}
