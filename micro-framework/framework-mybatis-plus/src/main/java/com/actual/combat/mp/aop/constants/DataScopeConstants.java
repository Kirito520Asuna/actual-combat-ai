package com.actual.combat.mp.aop.constants;

public interface DataScopeConstants {
    /**
     * 全部数据权限
     */
    String DATA_SCOPE_ALL = "1";

    /**
     * 自定数据权限
     */
    String DATA_SCOPE_CUSTOM = "2";

    /**
     * 部门数据权限
     */
    String DATA_SCOPE_DEPT = "3";

    /**
     * 部门及以下数据权限
     */
    String DATA_SCOPE_DEPT_AND_CHILD = "4";

    /**
     * 仅本人数据权限
     */
    String DATA_SCOPE_SELF = "5";

    /**
     * 数据权限过滤关键字
     */
    String DATA_SCOPE = "dataScope";

/*==========================================================================================================================================*/
    /*角色状态*/
    /**
     * 角色正常状态
     */
    String ROLE_NORMAL = "0";

    /**
     * 角色封禁状态
     */
    String ROLE_DISABLE = "1";
/*==========================================================================================================================================*/
    /*DATA_SCOPE*/
    String IS_CONDITIONS = "IS_CONDITIONS";

    String SCOPE_CUSTOM_IDS = "SCOPE_CUSTOM_IDS";

    String USER_ALIAS = "USER_ALIAS";

    String DEPT_ALIAS = "DEPT_ALIAS";

    String DEPT_ID = "DEPT_ID";

    String DATA_SCOPE_CUSTOM_SCOPE_CUSTOM_IDS = DATA_SCOPE_CUSTOM + SCOPE_CUSTOM_IDS;

    String DATA_SCOPE_CUSTOM_SCOPE_CUSTOM_IDS_TRUE = DATA_SCOPE_CUSTOM_SCOPE_CUSTOM_IDS + true;

    String DATA_SCOPE_CUSTOM_SCOPE_CUSTOM_IDS_FALSE = DATA_SCOPE_CUSTOM_SCOPE_CUSTOM_IDS + false;

    String DATA_SCOPE_SELF_USER_ALIAS = DATA_SCOPE_SELF + USER_ALIAS ;

    String DATA_SCOPE_SELF_USER_ALIAS_TRUE = DATA_SCOPE_SELF_USER_ALIAS + true;

    String DATA_SCOPE_SELF_USER_ALIAS_FALSE = DATA_SCOPE_SELF_USER_ALIAS + false;

    String DATA_SCOPE_DEPT_AND_CHILD_DEPT_ALIAS = DATA_SCOPE_DEPT_AND_CHILD + DEPT_ALIAS;

    String DATA_SCOPE_DEPT_AND_CHILD_DEPT_ALIAS_DEPT_ID = DATA_SCOPE_DEPT_AND_CHILD_DEPT_ALIAS + DEPT_ID;

    String DATA_SCOPE_DEPT_AND_CHILD_DEPT_ALIAS_TRUE = DATA_SCOPE_DEPT_AND_CHILD_DEPT_ALIAS + true;

    String DATA_SCOPE_DEPT_AND_CHILD_DEPT_ALIAS_DEPT_ID_TRUE = DATA_SCOPE_DEPT_AND_CHILD_DEPT_ALIAS_DEPT_ID + true;

    String DATA_SCOPE_DEPT_DEPT_ALIAS = DATA_SCOPE_DEPT + DEPT_ALIAS;

    String DATA_SCOPE_DEPT_DEPT_ALIAS_TRUE = DATA_SCOPE_DEPT_DEPT_ALIAS + true;
/*==========================================================================================================================================*/

}
