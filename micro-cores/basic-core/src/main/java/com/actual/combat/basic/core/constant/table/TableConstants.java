package com.actual.combat.basic.core.constant.table;

public interface TableConstants {
    /*==============================table-start==============================*/
    //命名 规范：简化表名小写
    String user = "sys_user";
    String user_role = "sys_user_role";
    String role = "sys_role";
    String role_menu = "sys_role_menu";
    String menu = "sys_menu";
    String menu_ancestor = "sys_menu_ancestor";
    /*=======================================================================*/
    String dept = "sys_dept";
    String user_dept = "sys_user_dept";
    String role_dept = "sys_role_dept";
    String dept_ancestor = "sys_dept_ancestor";
    /*=======================================================================*/
    String dict_data = "sys_dict_data";
    String dict_type = "sys_dict_type";
    /*=======================================================================*/
    String gen_table = "gen_table";
    String gen_table_column = "gen_table_column";
    /*=======================================================================*/
    String file_info = "file_info";
    String file_part = "file_part";
    /*=======================================================================*/
    String job = "sys_job";
    String job_log = "sys_job_log";
    /*=======================================================================*/
    String apply = "apply";
    String friend = "friend";
    String message = "message";

    String chat_message = "chat_message";
    String chat_user = "chat_user";
    String chat_window = "chat_window";
    /*===============================table-end===============================*/
    /*=======================================================================*/
    /*=======================================================================*/
    /*===========================table-column-start==========================*/
    //命名 规范：简化表名大写_COL_列名大写
    //user
    String USER_COL_USER_ID = "user_id";
    String USER_COL_USER_NAME = "user_name";
    String USER_COL_NICK_NAME = "nick_name";
    String USER_COL_USER_TYPE = "user_type";
    String USER_COL_EMAIL = "email";
    String USER_COL_PHONE_NUMBER = "phone_number";
    String USER_COL_SEX = "sex";
    String USER_COL_AVATAR = "avatar";
    String USER_COL_PASSWORD = "password";
    String USER_COL_STATUS = "status";
    String USER_COL_DEL_FLAG = "del_flag";
    String USER_COL_LOGIN_IP = "login_ip";
    String USER_COL_LOGIN_DATE = "login_date";
    /*=======================================================================*/
    //dept
    String DEPT_COL_DEPT_ID = "dept_id";
    /*=======================================================================*/
    //role_dept
    String ROLE_DEPT_COL_ROLE_ID = "role_id";
    String ROLE_DEPT_COL_DEPT_ID = "dept_id";
    /*=======================================================================*/
    //dept_ancestor
    String DEPT_ANCESTOR_COL_DEPT_ID = "dept_id";
    String DEPT_ANCESTOR_COL_DEPT_PARENT_ID = "dept_parent_id";
    /*=======================================================================*/
    //menu
    String MENU_COL_MENU_ID = "menu_id";
    String MENU_COL_MENU_NAME = "menu_name";
    String MENU_COL_PARENT_ID = "parent_id";
    String MENU_COL_ORDER_NUM = "order_num";
    String MENU_COL_PATH = "path";
    String MENU_COL_COMPONENT = "component";
    String MENU_COL_QUERY = "query";
    String MENU_COL_ROUTE_NAME = "route_name";
    String MENU_COL_IS_FRAME = "is_frame";
    String MENU_COL_IS_CACHE = "is_cache";
    String MENU_COL_MENU_TYPE = "menu_type";
    String MENU_COL_VISIBLE = "visible";
    String MENU_COL_STATUS = "status";
    String MENU_COL_PERMS = "perms";
    String MENU_COL_ICON = "icon";
    /*=======================================================================*/
    //menu_ancestor 
    String MENU_ANCESTOR_COL_ID = "id";
    String MENU_ANCESTOR_COL_MENU_ID = "menu_id";
    String MENU_ANCESTOR_COL_MENU_PARENT_ID = "menu_parent_id";
    String MENU_ANCESTOR_COL_LEVEL = "level";
    /*=======================================================================*/
    //role 
    String ROLE_COL_ROLE_ID = "role_id";
    String ROLE_COL_ROLE_NAME = "role_name";
    String ROLE_COL_ROLE_KEY = "role_key";
    String ROLE_COL_ROLE_SORT = "role_sort";
    String ROLE_COL_DATA_SCOPE = "data_scope";
    String ROLE_COL_MENU_CHECK_STRICTLY = "menu_check_strictly";
    String ROLE_COL_STATUS = "status";
    String ROLE_COL_DEL_FLAG = "del_flag";
    /*=======================================================================*/
    //role_menu
    String ROLE_MENU_COL_ROLE_ID = "role_id";
    String ROLE_MENU_COL_MENU_ID = "menu_id";
    /*=======================================================================*/
    /*=======================================================================*/
    /*=======================================================================*/
    /*=======================================================================*/
    /*============================table-column-end===========================*/
}
