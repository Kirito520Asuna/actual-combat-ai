package com.actual.combat.file.domain;

import com.actual.combat.basic.core.constant.table.TableConstants;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * @Author yan
 * @Date 2025/3/12 22:58:05
 * @Description
 */

/**
 * 文件分片表
 */
@Schema(description = "文件分片表")
@Data
@Accessors(chain = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = TableConstants.file_part)
public class FilePart implements Serializable {
    /**
     * 编号
     */
    @TableId(value = "part_id", type = IdType.AUTO)
    @Schema(description = "编号")
    private Long partId;

    /**
     * code用于合并文件
     */
    @TableField(value = "part_code")
    @Schema(description = "code用于合并文件")
    private String partCode;

    /**
     * 文件编号
     */
    @TableField(value = "file_id")
    @Schema(description = "文件编号")
    private Long fileId;

    /**
     * 资源路径
     */
    @TableField(value = "url")
    @Schema(description = "资源路径")
    private String url;

    /**
     * 本地资源路径
     */
    @TableField(value = "local_resource")
    @Schema(description = "本地资源路径")
    private String localResource;

    /**
     * 是否本地资源
     */
    @TableField(value = "is_local")
    @Schema(description = "是否本地资源")
    private Boolean local;

    /**
     * 分片大小
     */
    @TableField(value = "part_size")
    @Schema(description = "分片大小")
    private Long partSize;

    /**
     * 分片顺序
     */
    @TableField(value = "part_sort")
    @Schema(description = "分片顺序")
    private Integer partSort;

    /**
     * 合并后是否删除分片
     */
    @TableField(value = "merge_delete")
    @Schema(description = "合并后是否删除分片")
    private Boolean mergeDelete;

    /**
     * 操作系统类型 linux,win,mac
     */
    @TableField(value = "os_type")
    @Schema(description = "操作系统类型 linux,win,mac")
    private String osType;

    /**
     * 上传目录
     */
    @TableField(value = "upload_dir")
    @Schema(description = "上传目录")
    private String uploadDir;

    private static final long serialVersionUID = 1L;

    public static final String COL_PART_ID = "part_id";

    public static final String COL_PART_CODE = "part_code";

    public static final String COL_FILE_ID = "file_id";

    public static final String COL_URL = "url";

    public static final String COL_LOCAL_RESOURCE = "local_resource";

    public static final String COL_IS_LOCAL = "is_local";

    public static final String COL_PART_SIZE = "part_size";

    public static final String COL_PART_SORT = "part_sort";

    public static final String COL_MERGE_DELETE = "merge_delete";

    public static final String COL_OS_TYPE = "os_type";

    public static final String COL_UPLOAD_DIR = "upload_dir";
}