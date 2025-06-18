package com.actual.combat.dict.domain;

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
 * 字典数据表
 */
@Schema(description = "字典数据表")
@Data
@Accessors(chain = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = TableConstants.dict_data)
public class SysDictData extends BaseEntity implements Serializable {
    /**
     * 字典编码
     */
    @TableId(value = "dict_code", type = IdType.AUTO)
    @Schema(description = "字典编码")
    private Long dictCode;

    /**
     * 字典排序
     */
    @TableField(value = "dict_sort")
    @Schema(description = "字典排序")
    private Integer dictSort;

    /**
     * 字典标签
     */
    @TableField(value = "dict_label")
    @Schema(description = "字典标签")
    private String dictLabel;

    /**
     * 字典键值
     */
    @TableField(value = "dict_value")
    @Schema(description = "字典键值")
    private String dictValue;

    /**
     * 字典类型
     */
    @TableField(value = "dict_type")
    @Schema(description = "字典类型")
    private String dictType;

    /**
     * 样式属性（其他样式扩展）
     */
    @TableField(value = "css_class")
    @Schema(description = "样式属性（其他样式扩展）")
    private String cssClass;

    /**
     * 表格回显样式
     */
    @TableField(value = "list_class")
    @Schema(description = "表格回显样式")
    private String listClass;

    /**
     * 是否默认（Y是 N否）
     */
    @TableField(value = "is_default")
    @Schema(description = "是否默认（Y是 N否）")
    private String isDefault;

    /**
     * 状态（0正常 1停用）
     */
    @TableField(value = "`status`")
    @Schema(description = "状态（0正常 1停用）")
    private String status;

    public static final String COL_DICT_CODE = "dict_code";

    public static final String COL_DICT_SORT = "dict_sort";

    public static final String COL_DICT_LABEL = "dict_label";

    public static final String COL_DICT_VALUE = "dict_value";

    public static final String COL_DICT_TYPE = "dict_type";

    public static final String COL_CSS_CLASS = "css_class";

    public static final String COL_LIST_CLASS = "list_class";

    public static final String COL_IS_DEFAULT = "is_default";

    public static final String COL_STATUS = "status";
}