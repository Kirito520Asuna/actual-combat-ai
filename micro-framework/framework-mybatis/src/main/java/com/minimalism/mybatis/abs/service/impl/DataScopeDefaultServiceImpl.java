package com.minimalism.mybatis.abs.service.impl;


import com.minimalism.mybatis.abs.service.DataScopeService;
import com.minimalism.mybatis.aop.domain.DataScopeAboutTable;

import java.util.LinkedHashMap;
import java.util.Map;

public class DataScopeDefaultServiceImpl implements DataScopeService {

    @Override
    public Map<String, String> fetchDataScopeSqlBuildFormatMap() {
        DataScopeAboutTable dataScopeAboutTable = fetchDataScopeAboutTable();
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

        //String isConditionsValue = " OR %s.`" + deptIdName + "` = 0 ";
        //String dataScopeCustomScopeCustomIdsTrueValue = " OR %s.`" + deptIdName + "` IN ( SELECT `" + roleDeptTableDeptIdName + "` FROM `" +
        //        roleDeptTableName + "` WHERE `" + roleDeptTableRoleIdName + "` in (%s) ) " ;
        //String dataScopeCustomScopeCustomIdsFalseValue = " OR %s.`" + deptIdName + "` IN ( SELECT `" + roleDeptTableDeptIdName + "` FROM `" +
        //        roleDeptTableDeptIdName + "` WHERE `" + roleDeptTableRoleIdName + "`  = %s ) " ;
        //String dataScopeDeptDeptAliasTrueValue = " OR %s.`" + deptIdName + "` = %s " ;
        //String dataScopeDeptAndChildDeptAliasTrueValue = " OR %s.`" + deptIdName + "` IN ( SELECT `" + deptAncestorsParentIdName + "` FROM `" +
        //        deptAncestorsName + "` WHERE `" + deptAncestorsIdName + "` = %s ) " ;
        //String dataScopeSelfUserAliasTrueValue = " OR %s.`" + userIdName + "` = %s " ;

        String isConditionsValue = String.format(" OR key&str.`%s` = 0 ", deptIdName).replace("key&str", "%s");
        String dataScopeCustomScopeCustomIdsTrueValue = String.format(" OR key&str.`%s` IN ( SELECT `%s` FROM `%s` WHERE `%s` in (key&str) ) ",
                deptIdName, roleDeptTableDeptIdName, roleDeptTableName, roleDeptTableName).replace("key&str", "%s");
        String dataScopeCustomScopeCustomIdsFalseValue = String.format(" OR key&str.`%s` IN ( SELECT `%s` FROM `%s` WHERE `%s`  = key&str ) ",
                deptIdName, roleDeptTableDeptIdName, roleDeptTableDeptIdName, roleDeptTableRoleIdName).replace("key&str", "%s");
        String dataScopeDeptDeptAliasTrueValue = String.format(" OR key&str.`%s` = key&str ", deptIdName).replace("key&str", "%s");
        String dataScopeDeptAndChildDeptAliasTrueValue = String.format(" OR key&str.`%s` IN ( SELECT `%s` FROM `%s` WHERE `%s` = key&str ",
                deptIdName, deptAncestorsParentIdName, deptAncestorsName, deptAncestorsIdName).replace("key&str", "%s");
        String dataScopeSelfUserAliasTrueValue = String.format(" OR key&str.`%s` = key&str ",userIdName).replace("key&str", "%s");

        Map<String, String> dataScopeSqlBuildFormatMap = new LinkedHashMap<>();
        dataScopeSqlBuildFormatMap.put(IS_CONDITIONS, isConditionsValue);
        dataScopeSqlBuildFormatMap.put(DATA_SCOPE_CUSTOM_SCOPE_CUSTOM_IDS_TRUE, dataScopeCustomScopeCustomIdsTrueValue);
        dataScopeSqlBuildFormatMap.put(DATA_SCOPE_CUSTOM_SCOPE_CUSTOM_IDS_FALSE, dataScopeCustomScopeCustomIdsFalseValue);
        dataScopeSqlBuildFormatMap.put(DATA_SCOPE_DEPT_DEPT_ALIAS_TRUE, dataScopeDeptDeptAliasTrueValue);
        dataScopeSqlBuildFormatMap.put(DATA_SCOPE_DEPT_AND_CHILD_DEPT_ALIAS_TRUE, dataScopeDeptAndChildDeptAliasTrueValue);
        dataScopeSqlBuildFormatMap.put(DATA_SCOPE_SELF_USER_ALIAS_TRUE, dataScopeSelfUserAliasTrueValue);

        return dataScopeSqlBuildFormatMap;
    }
}
