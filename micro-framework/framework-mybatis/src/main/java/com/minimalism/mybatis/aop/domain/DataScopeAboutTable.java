package com.minimalism.mybatis.aop.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 数据权限关于表 设置默认值
 */
@Data @Accessors(chain = true)
@NoArgsConstructor @SuperBuilder
@AllArgsConstructor
public class DataScopeAboutTable {
    /**
     * 用户表 相关
     */
    @Schema(description = "用户表 id名")
    private String userIdName = "user_id";
    /**
     * 部门表 相关
     */
    @Schema(description = "部门表 id名")
    private String deptIdName = "dept_id";
    /**
     * 权限用户表 相关
     */
    @Schema(description = "权限部门表名")
    private String roleDeptTableName = "sys_role_dept";
    @Schema(description = "权限部门表 部门id名")
    private String roleDeptTableDeptIdName = "dept_id";
    @Schema(description = "权限部门表 角色id名")
    private String roleDeptTableRoleIdName = "role_id";

    /**
     * 部门祖先表 相关
     */
    @Schema(description = "部门祖先表名")
    private String deptAncestorsName = "sys_dept_ancestors";
    @Schema(description = "部门祖先表 部门id名")
    private String deptAncestorsIdName = "dept_id";
    @Schema(description = "部门祖先表 部门祖先id名")
    private String deptAncestorsParentIdName = "dept_parent_id";
}
