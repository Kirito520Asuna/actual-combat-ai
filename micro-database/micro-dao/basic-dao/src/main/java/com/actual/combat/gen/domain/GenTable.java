package com.actual.combat.gen.domain;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.actual.combat.basic.core.constant.gen.GenConstants;
import com.actual.combat.basic.core.constant.table.TableConstants;
import com.actual.combat.basic.utils.other.StringUtils;
import com.actual.combat.mp.pojo.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.actual.combat.gen.mapper.GenTableColumnMapper;
import com.actual.combat.gen.mapper.GenTableMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author minimalism
 * @Date 2024/10/29 上午9:55:25
 * @Description
 */

/**
 * 代码生成业务表
 */
@Schema(description = "代码生成业务表")
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = TableConstants.gen_table)
public class GenTable extends BaseEntity implements Serializable {
    /**
     * 编号
     */
    @TableId(value = "`table_id`", type = IdType.AUTO)
    @Schema(description = "编号")
    private Long tableId;

    /**
     * 表名称
     */
    @TableField(value = "`table_name`")
    @Schema(description = "表名称")
    private String tableName;

    /**
     * 表描述
     */
    @TableField(value = "`table_comment`")
    @Schema(description = "表描述")
    private String tableComment;

    /**
     * 关联子表的表名
     */
    @TableField(value = "`sub_table_name`")
    @Schema(description = "关联子表的表名")
    private String subTableName;

    /**
     * 子表关联的外键名
     */
    @TableField(value = "`sub_table_fk_name`")
    @Schema(description = "子表关联的外键名")
    private String subTableFkName;

    /**
     * 实体类名称
     */
    @TableField(value = "`class_name`")
    @Schema(description = "实体类名称")
    private String className;

    /**
     * 使用的模板（crud单表操作 tree树表操作）
     */
    @TableField(value = "`tpl_category`")
    @Schema(description = "使用的模板（crud单表操作 tree树表操作）")
    private String tplCategory;

    /**
     * 前端模板类型（element-ui模版 element-plus模版）
     */
    @TableField(value = "`tpl_web_type`")
    @Schema(description = "前端模板类型（element-ui模版 element-plus模版）")
    private String tplWebType;

    /**
     * 生成包路径
     */
    @TableField(value = "`package_name`")
    @Schema(description = "生成包路径")
    private String packageName;

    /**
     * 生成模块名
     */
    @TableField(value = "`module_name`")
    @Schema(description = "生成模块名")
    private String moduleName;

    /**
     * 生成业务名
     */
    @TableField(value = "`business_name`")
    @Schema(description = "生成业务名")
    private String businessName;

    /**
     * 生成功能名
     */
    @TableField(value = "`function_name`")
    @Schema(description = "生成功能名")
    private String functionName;

    /**
     * 生成功能作者
     */
    @TableField(value = "`function_author`")
    @Schema(description = "生成功能作者")
    private String functionAuthor;

    /**
     * 生成代码方式（0zip压缩包 1自定义路径）
     */
    @TableField(value = "`gen_type`")
    @Schema(description = "生成代码方式（0zip压缩包 1自定义路径）")
    private String genType;

    /**
     * 生成路径（不填默认项目路径）
     */
    @TableField(value = "`gen_path`")
    @Schema(description = "生成路径（不填默认项目路径）")
    private String genPath;

    /**
     * 其它生成选项
     */
    @TableField(value = "`options`")
    @Schema(description = "其它生成选项")
    private String options;

    /**
     * 主键信息
     */
    @TableField(exist = false)
    private GenTableColumn pkColumn;

    /**
     * 子表信息
     */
    @TableField(exist = false)
    private GenTable subTable;

    /**
     * 表列信息
     */
    @TableField(exist = false)
    @Valid
    private List<GenTableColumn> columns = new ArrayList();
    /**
     * 权限控制 Shiro|Security
     */
    @TableField(exist = false)
    private String usePermissions = "Shiro";

    public boolean isShiro() {
        return isShiro(this.usePermissions);
    }

    public static boolean isShiro(String usePermissions) {
        return !ObjectUtil.equal(usePermissions, "Security");
    }
    public GenTable setUsePermissions(String usePermissions) {
        ArrayList<String> list = CollUtil.newArrayList("Security", "Shiro");
        if (!list.contains(usePermissions)) {
            usePermissions = this.usePermissions;
        }
        this.usePermissions = usePermissions;
        return this;
    }

    public GenTable setSubTable(GenTable subTable) {
        if (ObjectUtil.isNotEmpty(subTable)) {
            this.subTable = subTable;
        } else if (StrUtil.isNotBlank(getTableName())) {
            GenTable genTable = SpringUtil.getBean(GenTableMapper.class)
                    .selectOne(Wrappers.lambdaQuery(GenTable.class).eq(GenTable::getTableName, getTableName()));
            this.subTable = genTable;
        }
        return this;
    }

    public GenTable setPkColumn(GenTableColumn pkColumn) {
        if (ObjectUtil.isNotEmpty(pkColumn)) {
            this.pkColumn = pkColumn;
        } else {
            List<GenTableColumn> columns = getColumns();
            if (CollUtil.isEmpty(columns)) {
                columns.addAll(SpringUtil.getBean(GenTableColumnMapper.class)
                        .selectList(
                                Wrappers.lambdaQuery(GenTableColumn.class)
                                        .eq(GenTableColumn::getTableId, getTableId())
                        ));
            }
            for (GenTableColumn column : columns) {
                if (column.isPk()) {
                    setPkColumn(column);
                    break;
                }
            }
            if (ObjectUtil.isNull(getPkColumn())) {
                setPkColumn(columns.get(0));
            }
            if (GenConstants.TPL_SUB.equals(getTplCategory())) {
                for (GenTableColumn column : getSubTable().getColumns()) {
                    if (column.isPk()) {
                        getSubTable().setPkColumn(column);
                        break;
                    }
                }
                if (ObjectUtil.isNull(getSubTable().getPkColumn())) {
                    getSubTable().setPkColumn(getSubTable().getColumns().get(0));
                }
            }
        }

        return this;
    }

    public boolean isSub() {
        return isSub(this.tplCategory);
    }

    public static boolean isSub(String tplCategory) {
        return tplCategory != null && StringUtils.equals(GenConstants.TPL_SUB, tplCategory);
    }

    public boolean isTree() {
        return isTree(this.tplCategory);
    }

    public static boolean isTree(String tplCategory) {
        return tplCategory != null && StringUtils.equals(GenConstants.TPL_TREE, tplCategory);
    }

    public boolean isCrud() {
        return isCrud(this.tplCategory);
    }

    public static boolean isCrud(String tplCategory) {
        return tplCategory != null && StringUtils.equals(GenConstants.TPL_CRUD, tplCategory);
    }

    private static final long serialVersionUID = 1L;

    public static final String COL_TABLE_ID = "table_id";

    public static final String COL_TABLE_NAME = "table_name";

    public static final String COL_TABLE_COMMENT = "table_comment";

    public static final String COL_SUB_TABLE_NAME = "sub_table_name";

    public static final String COL_SUB_TABLE_FK_NAME = "sub_table_fk_name";

    public static final String COL_CLASS_NAME = "class_name";

    public static final String COL_TPL_CATEGORY = "tpl_category";

    public static final String COL_TPL_WEB_TYPE = "tpl_web_type";

    public static final String COL_PACKAGE_NAME = "package_name";

    public static final String COL_MODULE_NAME = "module_name";

    public static final String COL_BUSINESS_NAME = "business_name";

    public static final String COL_FUNCTION_NAME = "function_name";

    public static final String COL_FUNCTION_AUTHOR = "function_author";

    public static final String COL_GEN_TYPE = "gen_type";

    public static final String COL_GEN_PATH = "gen_path";

    public static final String COL_OPTIONS = "options";
}