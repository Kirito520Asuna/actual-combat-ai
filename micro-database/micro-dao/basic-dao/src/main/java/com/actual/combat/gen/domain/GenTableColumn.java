package com.actual.combat.gen.domain;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.actual.combat.basic.core.constant.table.TableConstants;
import com.actual.combat.basic.utils.other.StringUtils;
import com.actual.combat.mp.pojo.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Author yan
 * @Date 2024/10/29 上午9:55:25
 * @Description
 */

/**
 * 代码生成业务表字段
 */
@Schema(description = "代码生成业务表字段")
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = TableConstants.gen_table_column)
public class GenTableColumn extends BaseEntity implements Serializable {
    /**
     * 编号
     */
    @TableId(value = "`column_id`", type = IdType.AUTO)
    @Schema(description = "编号")
    private Long columnId;

    /**
     * 归属表编号
     */
    @TableField(value = "`table_id`")
    @Schema(description = "归属表编号")
    private Long tableId;

    /**
     * 列名称
     */
    @TableField(value = "`column_name`")
    @Schema(description = "列名称")
    private String columnName;

    /**
     * 列描述
     */
    @TableField(value = "`column_comment`")
    @Schema(description = "列描述")
    private String columnComment;

    /**
     * 列类型
     */
    @TableField(value = "`column_type`")
    @Schema(description = "列类型")
    private String columnType;

    /**
     * JAVA类型
     */
    @TableField(value = "`java_type`")
    @Schema(description = "JAVA类型")
    private String javaType;

    /**
     * JAVA字段名
     */
    @TableField(value = "`java_field`")
    @Schema(description = "JAVA字段名")
    private String javaField;

    /**
     * java_field ==> JavaField
     */
    @TableField(exist = false)
    private String toUpJavaField;

    /**
     * 是否主键（1是）
     */
    @TableField(value = "`is_pk`")
    @Schema(description = "是否主键（1是）")
    private String isPk;

    /**
     * 是否父id
     */
    @TableField(exist = false)
    @Schema(description = "是否父id")
    private Boolean isParentId;

    /**
     * 是否自增（1是）
     */
    @TableField(value = "`is_increment`")
    @Schema(description = "是否自增（1是）")
    private String isIncrement;

    /**
     * 是否必填（1是）
     */
    @TableField(value = "`is_required`")
    @Schema(description = "是否必填（1是）")
    private String isRequired;

    /**
     * 是否为插入字段（1是）
     */
    @TableField(value = "`is_insert`")
    @Schema(description = "是否为插入字段（1是）")
    private String isInsert;

    /**
     * 是否编辑字段（1是）
     */
    @TableField(value = "`is_edit`")
    @Schema(description = "是否编辑字段（1是）")
    private String isEdit;

    /**
     * 是否列表字段（1是）
     */
    @TableField(value = "`is_list`")
    @Schema(description = "是否列表字段（1是）")
    private String isList;

    /**
     * 是否查询字段（1是）
     */
    @TableField(value = "`is_query`")
    @Schema(description = "是否查询字段（1是）")
    private String isQuery;

    /**
     * 查询方式（等于、不等于、大于、小于、范围）
     */
    @TableField(value = "`query_type`")
    @Schema(description = "查询方式（等于、不等于、大于、小于、范围）")
    private String queryType;

    /**
     * 显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）
     */
    @TableField(value = "`html_type`")
    @Schema(description = "显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）")
    private String htmlType;

    /**
     * 字典类型
     */
    @TableField(value = "`dict_type`")
    @Schema(description = "字典类型")
    private String dictType;

    /**
     * 排序
     */
    @TableField(value = "`sort`")
    @Schema(description = "排序")
    private Integer sort;

    public GenTableColumn setIsParentId(Boolean parentId) {
        if (ObjectUtil.isNotEmpty(parentId)) {
            this.isParentId = parentId;
        } else if (ObjectUtil.isNotEmpty(getColumnName())) {
            String columnName = getColumnName();
            this.isParentId = columnName.contains("parent_id");
        }
        return this;
    }

    public boolean isParentId() {
        Boolean isParentId = getIsParentId();
        if (ObjectUtil.isEmpty(isParentId)) {
            String columnName = getColumnName();
            isParentId = StrUtil.isNotBlank(columnName) &&
                    columnName.contains("parent_id");
        }
        return isParentId;
    }

    public boolean isPk() {
        return isPk(this.isPk);
    }

    public boolean isPk(String isPk) {
        return ObjectUtil.defaultIfNull(StrUtil.equals(isPk, "1"), false);
    }

    public boolean isSuperColumn() {
        return isSuperColumn(this.javaField);
    }

    public static boolean isSuperColumn(String javaField) {
        return StringUtils.equalsAnyIgnoreCase(javaField,
                // BaseEntity
                "createBy", "createTime", "updateBy", "updateTime", "remark",
                // TreeEntity
                "parentName", "parentId", "orderNum", "ancestors");
    }

    public boolean isList() {
        return isList(this.isList);
    }

    public boolean isList(String isList) {
        return ObjectUtil.defaultIfNull(StrUtil.equals(isList, "1"), false);
    }

    public GenTableColumn setJavaField(String javaField) {
        if (StrUtil.isBlank(javaField)) {
            this.javaField = javaField;
        } else if (StrUtil.isNotBlank(getColumnName())) {
            String toCamelCase = StrUtil.toCamelCase(getColumnName());
            this.javaField = toCamelCase;
        }
        return this;
    }

    public boolean isInsert() {
        return isInsert(this.isInsert);
    }

    public boolean isInsert(String isInsert) {
        return ObjectUtil.defaultIfNull(StrUtil.equals(isInsert, "1"), false);
    }

    public boolean isEdit() {
        return isInsert(this.isEdit);
    }

    public boolean isEdit(String isEdit) {
        return ObjectUtil.defaultIfNull(StrUtil.equals(isEdit, "1"), false);
    }

    public boolean isUsableColumn() {
        return isUsableColumn(this.javaField);
    }

    public static boolean isUsableColumn(String javaField) {
        // isSuperColumn()中的名单用于避免生成多余Domain属性，若某些属性在生成页面时需要用到不能忽略，则放在此处白名单
        return StringUtils.equalsAnyIgnoreCase(javaField, "parentId", "orderNum", "remark");
    }

    public String getJavaField() {
        String javaField = this.javaField;
        if (StrUtil.isBlank(javaField)) {
            javaField = StrUtil.toCamelCase(getColumnName());
        }
        return javaField;
    }

    public String getToUpJavaField() {
        String toUpJavaField = this.toUpJavaField;
        if (StrUtil.isNotBlank(toUpJavaField)) {
            toUpJavaField = StringUtils.toUp(toUpJavaField);
        } else if (StrUtil.isNotBlank(getJavaField())) {
            toUpJavaField = StringUtils.toUp(getJavaField());
        }
        return toUpJavaField;
    }

    public GenTableColumn setToUpJavaField(String toUpJavaField) {
        if (StrUtil.isNotBlank(toUpJavaField)) {
            this.toUpJavaField = StringUtils.toUp(toUpJavaField);
        } else if (StrUtil.isNotBlank(getJavaField())) {
            this.toUpJavaField = StringUtils.toUp(getJavaField());
        }
        return this;
    }

    private static final long serialVersionUID = 1L;
    public static final String COL_COLUMN_ID = "column_id";
    public static final String COL_TABLE_ID = "table_id";
    public static final String COL_COLUMN_NAME = "column_name";
    public static final String COL_COLUMN_COMMENT = "column_comment";
    public static final String COL_COLUMN_TYPE = "column_type";
    public static final String COL_JAVA_TYPE = "java_type";
    public static final String COL_JAVA_FIELD = "java_field";
    public static final String COL_IS_PK = "is_pk";
    public static final String COL_IS_INCREMENT = "is_increment";
    public static final String COL_IS_REQUIRED = "is_required";
    public static final String COL_IS_INSERT = "is_insert";
    public static final String COL_IS_EDIT = "is_edit";
    public static final String COL_IS_LIST = "is_list";
    public static final String COL_IS_QUERY = "is_query";
    public static final String COL_QUERY_TYPE = "query_type";
    public static final String COL_HTML_TYPE = "html_type";
    public static final String COL_DICT_TYPE = "dict_type";
    public static final String COL_SORT = "sort";
}