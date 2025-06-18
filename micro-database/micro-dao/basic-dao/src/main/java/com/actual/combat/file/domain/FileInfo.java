package com.actual.combat.file.domain;

import com.actual.combat.basic.core.constant.table.TableConstants;
import com.actual.combat.mp.pojo.BaseEntity;
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
 * @Date 2025/3/12 23:24:55
 * @Description
 */

/**
 * 文件信息表
 */
@Schema(description = "文件信息表")
@Data
@Accessors(chain = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = TableConstants.file_info)
public class FileInfo extends BaseEntity implements Serializable {
    /**
     * 编号
     */
    @TableId(value = "file_id", type = IdType.AUTO)
    @Schema(description = "编号")
    private Long fileId;

    /**
     * 资源路径
     */
    @TableField(value = "url")
    @Schema(description = "资源路径")
    private String url;

    /**
     * 上传目录
     */
    @TableField(value = "upload_dir")
    @Schema(description = "上传目录")
    private String uploadDir;

    /**
     * 资源原始名称
     */
    @TableField(value = "`name`")
    @Schema(description = "资源原始名称")
    private String name;

    /**
     * 资源名称
     */
    @TableField(value = "file_name")
    @Schema(description = "资源名称")
    private String fileName;

    /**
     * 后缀名
     */
    @TableField(value = "suffix")
    @Schema(description = "后缀名")
    private String suffix;

    /**
     * 是否图片
     */
    @TableField(value = "is_img")
    @Schema(description = "是否图片")
    private Boolean img;

    /**
     * 尺寸
     */
    @TableField(value = "`size`")
    @Schema(description = "尺寸")
    private Long size;

    /**
     * 文件展示类型，非后缀名
     */
    @TableField(value = "`type`")
    @Schema(description = "文件展示类型，非后缀名")
    private String type;

    /**
     * 是否目录
     */
    @TableField(value = "is_dir")
    @Schema(description = "是否目录")
    private Boolean dir;

    /**
     * 是否本地资源
     */
    @TableField(value = "is_local")
    @Schema(description = "是否本地资源")
    private Boolean local;

    @TableField(value = "parent_id")
    @Schema(description = "")
    private Long parentId;

    /**
     * code用于合并文件
     */
    @TableField(value = "part_code")
    @Schema(description = "code用于合并文件")
    private String partCode;

    private static final long serialVersionUID = 1L;

    public static final String COL_FILE_ID = "file_id";

    public static final String COL_URL = "url";

    public static final String COL_UPLOAD_DIR = "upload_dir";

    public static final String COL_NAME = "name";

    public static final String COL_FILE_NAME = "file_name";

    public static final String COL_SUFFIX = "suffix";

    public static final String COL_IS_IMG = "is_img";

    public static final String COL_SIZE = "size";

    public static final String COL_TYPE = "type";

    public static final String COL_IS_DIR = "is_dir";

    public static final String COL_IS_LOCAL = "is_local";

    public static final String COL_PARENT_ID = "parent_id";

    public static final String COL_PART_CODE = "part_code";
}