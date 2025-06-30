package com.minimalism.mybatis.aop.aspect;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.minimalism.mybatis.aop.constants.DataScopeConstants;
import com.minimalism.mybatis.aop.dataScope.DataScope;
import com.minimalism.mybatis.aop.domain.DataScopeRole;
import com.minimalism.mybatis.aop.domain.DataScopeUser;
import com.minimalism.mybatis.pojo.BaseEntity;
import com.minimalism.mybatis.utils.MpObjectUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据过滤处理
 *
 * @author ruoyi
 */
@Aspect
@Component
public class DataScopeAspect implements DataScopeConstants {


    @Before("@annotation(dataScope)")
    public void doBefore(JoinPoint point, DataScope dataScope) throws Throwable {
        clearDataScope(point);
        handleDataScope(point, dataScope);
    }

    protected void handleDataScope(JoinPoint point, DataScope dataScope) {
        DataScopeUser dataScopeUser = new DataScopeUser();
        if (MpObjectUtils.isNotNull(dataScopeUser) && !dataScopeUser.isAdmin()) {
            String Permission = dataScopeUser.getRoleKey();
            String permission = MpObjectUtils.defaultIfEmpty(dataScope.permission(), Permission);
            dataScopeFilter(point, dataScopeUser, dataScope.deptAlias(), dataScope.userAlias(), permission);
        }
    }

    /**
     * 数据范围过滤
     *
     * @param joinPoint  切点
     * @param user       用户
     * @param deptAlias  部门别名
     * @param userAlias  用户别名
     * @param permission 权限字符
     */
    public static void dataScopeFilter(JoinPoint joinPoint, DataScopeUser user, String deptAlias, String userAlias, String permission) {
        StringBuilder sqlBuild = new StringBuilder();
        List<String> conditions = new ArrayList<>();
        List<String> scopeCustomIds = new ArrayList<>();

        String userId = user.getUserId();
        String deptId = user.getDeptId();
        List<DataScopeRole> roles = user.getRoles();

        roles.stream().forEach(role -> {
            if (MpObjectUtils.equals(role.getDataScope(), DATA_SCOPE_CUSTOM)
                    && MpObjectUtils.equals(role.getStatus(), ROLE_NORMAL)
                    && CollUtil.contains(role.getPermissions(), permission)) {
                scopeCustomIds.add(role.getRoleId());
            }
        });

        for (DataScopeRole role : roles) {
            String dataScope = role.getDataScope();
            if (CollUtil.contains(conditions, dataScope) || MpObjectUtils.equals(role.getStatus(), ROLE_DISABLE)) {
                continue;
            }
            if (!CollUtil.contains(role.getPermissions(), permission)) {
                continue;
            }
            if (MpObjectUtils.equals(DATA_SCOPE_ALL, dataScope)) {
                sqlBuild = new StringBuilder();
                conditions.add(dataScope);
                break;
            } else if (MpObjectUtils.equals(DATA_SCOPE_CUSTOM, dataScope)) {
                sqlBuild = role.scopeCustomIdsBuildDataScope(sqlBuild, scopeCustomIds, deptAlias);
            } else {
                if (MpObjectUtils.equals(DATA_SCOPE_DEPT, dataScope)) {
                    sqlBuild = role.deptBuildDataScope(sqlBuild, deptAlias, deptId);
                } else if (MpObjectUtils.equals(DATA_SCOPE_DEPT_AND_CHILD, dataScope)) {
                    sqlBuild = role.deptAndChildBuildDataScope(sqlBuild, deptAlias, deptId);
                } else if (MpObjectUtils.equals(DATA_SCOPE_SELF, dataScope)) {
                    if (StrUtil.isNotBlank(userAlias)) {
                        sqlBuild = role.selfBuildDataScope(sqlBuild, userAlias, userId);
                    } else {
                        sqlBuild = role.conditionsBuildDataScope(sqlBuild, deptAlias);
                    }
                }
            }

        }

        // 角色都不包含传递过来的权限字符，这个时候sqlBuild也会为空，所以要限制一下,不查询任何数据
        if (CollUtil.isEmpty(conditions)) {
            sqlBuild = DataScopeRole.buildDataScopeConditions(sqlBuild, deptAlias);
        }

        if (StrUtil.isNotBlank(sqlBuild.toString())) {
            Object params = joinPoint.getArgs()[0];
            if (MpObjectUtils.isNotNull(params) && params instanceof BaseEntity) {
                BaseEntity baseEntity = (BaseEntity) params;
                baseEntity.getParams().put(DATA_SCOPE, " AND (" + sqlBuild.substring(4) + ")");
            }
        }

    }

    /**
     * 拼接权限sql前先清空params.dataScope参数防止注入
     */
    private void clearDataScope(final JoinPoint joinPoint) {
        Object params = joinPoint.getArgs()[0];
        if (null != params && params instanceof BaseEntity) {
            BaseEntity baseEntity = (BaseEntity) params;
            baseEntity.getParams().put(DATA_SCOPE, "");
        }
    }
}
