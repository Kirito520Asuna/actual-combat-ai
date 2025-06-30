package com.minimalism.mybatis.aop.domain;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.mybatis.abs.service.DataScopeService;
import com.minimalism.mybatis.aop.constants.DataScopeConstants;
import com.minimalism.mybatis.utils.MpObjectUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@SuperBuilder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class DataScopeRole implements DataScopeConstants {
    @Schema(description = "数据权限")
    private String dataScope;
    @Schema(description = "角色id")
    private String roleId;
    @Schema(description = "角色权限key")
    private String roleKey;
    @Schema(description = "角色状态")
    private String status;
    @Schema(description = "权限")
    private Set<String> permissions;

    /**
     * 构建数据权限(特殊处理)
     *
     * @param sqlBuild
     * @param deptAlias
     * @return
     */
    public static StringBuilder buildDataScopeConditions(StringBuilder sqlBuild, String deptAlias) {
        return new DataScopeRole().conditionsBuildDataScope(sqlBuild, deptAlias);
    }

    /**
     * 构建数据权限
     *
     * @param sqlBuild
     * @param deptAlias
     * @return
     */
    public StringBuilder conditionsBuildDataScope(StringBuilder sqlBuild, String deptAlias) {
        return buildDataScope(sqlBuild, null, null, deptAlias, null, null, null, true, null);
    }

    /**
     * 构建数据权限
     *
     * @return
     */
    public StringBuilder scopeCustomIdsBuildDataScope(StringBuilder sqlBuild, List<String> scopeCustomIds, String deptAlias) {
        return buildDataScope(sqlBuild, null, scopeCustomIds, deptAlias, null, null, null, false, null);
    }

    /**
     * 构建数据权限
     * @param sqlBuild
     * @param deptAlias
     * @param deptId
     * @return
     */
    public StringBuilder deptBuildDataScope(StringBuilder sqlBuild, String deptAlias, String deptId) {
        return buildDataScope(sqlBuild, null, null, deptAlias, deptId, null, null, false, null);
    }
    /**
     * 构建数据权限
     * @param sqlBuild
     * @param userAlias
     * @param userId
     * @return
     */

    public StringBuilder selfBuildDataScope(StringBuilder sqlBuild, String userAlias, String userId) {
        return buildDataScope(sqlBuild, null, null, null, null, userAlias, userId, false, null);
    }

    /**
     * 构建数据权限
     * @param sqlBuild
     * @param deptAlias
     * @param deptId
     * @return
     */
    public StringBuilder deptAndChildBuildDataScope(StringBuilder sqlBuild, String deptAlias, String deptId) {
        return buildDataScope(sqlBuild, null, null, deptAlias, deptId, null, null, false, null);
    }

    /**
     * 基础构建
     *
     * @param sqlBuild       sql
     * @param scopeCustomIds 自定义ids
     * @param deptAlias      部门别名
     * @param deptId         部门id
     * @param userAlias      用户别名
     * @param userId         用户id
     * @param isConditions   是否条件
     * @param conditions     条件
     * @return
     */
    protected StringBuilder buildDataScope(StringBuilder sqlBuild, String dataScope, List<String> scopeCustomIds, String deptAlias, String deptId, String userAlias, String userId, boolean isConditions, List<String> conditions) {
        DataScopeService dataScopeService = SpringUtil.getBean(DataScopeService.class);
        //获取所有模板
        Map<String, String> dataScopeSqlBuildFormatMap = dataScopeService.fetchDataScopeSqlBuildFormatMap();
        if (dataScopeSqlBuildFormatMap == null) {
            dataScopeSqlBuildFormatMap = new LinkedHashMap<>();
        }
        //获取数据权限表信息
        DataScopeAboutTable dataScopeAboutTable = dataScopeService.fetchDataScopeAboutTable();
        if (dataScopeAboutTable == null) {
            dataScopeAboutTable = new DataScopeAboutTable();
        }

        String userIdName = dataScopeAboutTable.getUserIdName();
        String deptIdName = dataScopeAboutTable.getDeptIdName();

        String roleDeptTableName = dataScopeAboutTable.getRoleDeptTableName();
        String roleDeptTableRoleIdName = dataScopeAboutTable.getRoleDeptTableRoleIdName();
        String roleDeptTableDeptIdName = dataScopeAboutTable.getRoleDeptTableDeptIdName();

        String deptAncestorsName = dataScopeAboutTable.getDeptAncestorsName();
        String deptAncestorsIdName = dataScopeAboutTable.getDeptAncestorsIdName();
        String deptAncestorsParentIdName = dataScopeAboutTable.getDeptAncestorsParentIdName();

        // 角色都不包含传递过来的权限字符，这个时候sqlString也会为空，所以要限制一下,不查询任何数据
        if (isConditions && CollUtil.isEmpty(conditions)) {
            //String formatDefaultValue = " OR %s.dept_id = 0 ";
            String formatDefaultValue = " OR %s.`" + deptIdName + "` = 0 ";
            String format = dataScopeSqlBuildFormatMap.get(IS_CONDITIONS);
            format = MpObjectUtils.defaultIfEmpty(format, formatDefaultValue);
            sqlBuild.append(String.format(format, deptAlias));
            return sqlBuild;
        }

        if (StrUtil.isBlank(dataScope)) {
            dataScope = getDataScope();
        }
        if (MpObjectUtils.equals(DATA_SCOPE_ALL, dataScope)) {
            sqlBuild = new StringBuilder();
        } else if (MpObjectUtils.equals(DATA_SCOPE_CUSTOM, dataScope)) {
            if (CollUtil.isNotEmpty(scopeCustomIds)) {
                // 多个自定数据权限使用in查询，避免多次拼接。
                String format = dataScopeSqlBuildFormatMap.get(DATA_SCOPE_CUSTOM_SCOPE_CUSTOM_IDS_TRUE);
                //String formatDefaultValue = " OR %s.dept_id IN ( SELECT dept_id FROM sys_role_dept WHERE role_id in (%s) ) ";
                String formatDefaultValue = " OR %s.`" + deptIdName + "` IN ( SELECT `" + roleDeptTableDeptIdName + "` FROM `" +
                        roleDeptTableName + "` WHERE `" + roleDeptTableRoleIdName + "` in (%s) ) ";
                format = MpObjectUtils.defaultIfEmpty(format, formatDefaultValue);
                sqlBuild.append(String.format(format, deptAlias, String.join(",", scopeCustomIds)));
            } else {
                String format = dataScopeSqlBuildFormatMap.get(DATA_SCOPE_CUSTOM_SCOPE_CUSTOM_IDS_FALSE);
                //String formatDefaultValue = " OR %s.dept_id IN ( SELECT dept_id FROM sys_role_dept WHERE role_id = %s ) ";
                String formatDefaultValue = " OR %s.`" + deptIdName + "` IN ( SELECT `" + roleDeptTableDeptIdName + "` FROM `" +
                        roleDeptTableDeptIdName + "` WHERE `" + roleDeptTableRoleIdName + "`  = %s ) ";
                format = MpObjectUtils.defaultIfEmpty(format, formatDefaultValue);
                sqlBuild.append(String.format(format, deptAlias, getRoleId()));
            }
        } else {
            if (MpObjectUtils.equals(DATA_SCOPE_DEPT, dataScope)) {
                //String formatDefaultValue = " OR %s.dept_id = %s ";
                String formatDefaultValue = " OR %s.`" + deptIdName + "` = %s ";
                String format = dataScopeSqlBuildFormatMap.get(DATA_SCOPE_DEPT_DEPT_ALIAS_TRUE);
                format = MpObjectUtils.defaultIfEmpty(format, formatDefaultValue);
                sqlBuild.append(String.format(format, deptAlias, deptId));
            } else if (MpObjectUtils.equals(DATA_SCOPE_DEPT_AND_CHILD, dataScope)) {
                //String formatDefaultValue = " OR %s.dept_id IN ( SELECT dept_id FROM sys_dept WHERE dept_id = %s or find_in_set( %s , ancestors ) )";
                //String formatDefaultValue = " OR %s.dept_id IN ( SELECT dept_parent_id FROM sys_dept_ancestors WHERE dept_id = %s )";
                String formatDefaultValue = " OR %s.`" + deptIdName + "` IN ( SELECT `" + deptAncestorsParentIdName + "` FROM `" +
                        deptAncestorsName + "` WHERE `" + deptAncestorsIdName + "` = %s ) ";
                String format = dataScopeSqlBuildFormatMap.get(DATA_SCOPE_DEPT_AND_CHILD_DEPT_ALIAS_TRUE);
                format = MpObjectUtils.defaultIfEmpty(format, formatDefaultValue);
                sqlBuild.append(String.format(format, deptAlias, deptId, deptId));
            } else if (MpObjectUtils.equals(DATA_SCOPE_SELF, dataScope)) {
                if (StrUtil.isNotBlank(userAlias)) {
                    //String formatDefaultValue = " OR %s.user_id = %s ";
                    String formatDefaultValue = " OR %s.`" + userIdName + "` = %s ";
                    String format = dataScopeSqlBuildFormatMap.get(DATA_SCOPE_SELF_USER_ALIAS_TRUE);
                    format = MpObjectUtils.defaultIfEmpty(format, formatDefaultValue);
                    sqlBuild.append(String.format(format, userAlias, userId));
                } else {
                    // 数据权限为仅本人且没有userAlias别名不查询任何数据
                    //String formatDefaultValue = " OR %s.dept_id = 0 ";
                    String formatDefaultValue = " OR %s.`" + deptIdName + "` = 0 ";
                    String format = dataScopeSqlBuildFormatMap.get(IS_CONDITIONS);
                    format = MpObjectUtils.defaultIfEmpty(format, formatDefaultValue);
                    sqlBuild.append(String.format(format, deptAlias));
                }
            }
        }
        return sqlBuild;
    }
}
