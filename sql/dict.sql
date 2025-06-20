-- ----------------------------
-- 11、字典类型表
-- ----------------------------
drop table if exists sys_dict_type;
create table sys_dict_type
(
    dict_id     bigint(20) not null auto_increment comment '字典主键',
    dict_name   varchar(100) default '' comment '字典名称',
    dict_type   varchar(100) default '' comment '字典类型',
    status      char(1)      default '0' comment '状态（0正常 1停用）',
    create_by   varchar(64)  default '' comment '创建者',
    create_time timestamp    default now() comment '创建时间',
    update_by   varchar(64)  default '' comment '更新者',
    update_time timestamp    default now() ON UPDATE now() comment '更新时间',
    remark      varchar(500) default null comment '备注',
    primary key (dict_id),
    unique (dict_type)
) engine = innodb
  auto_increment = 100 comment = '字典类型表';

insert into sys_dict_type
values (1, '用户性别', 'sys_user_sex', '0', '1', now(), '', null, '用户性别列表');
insert into sys_dict_type
values (2, '菜单状态', 'sys_show_hide', '0', '1', now(), '', null, '菜单状态列表');
insert into sys_dict_type
values (3, '系统开关', 'sys_normal_disable', '0', '1', now(), '', null, '系统开关列表');
insert into sys_dict_type
values (4, '任务状态', 'sys_job_status', '0', '1', now(), '', null, '任务状态列表');
insert into sys_dict_type
values (5, '任务分组', 'sys_job_group', '0', '1', now(), '', null, '任务分组列表');
insert into sys_dict_type
values (6, '系统是否', 'sys_yes_no', '0', '1', now(), '', null, '系统是否列表');
insert into sys_dict_type
values (7, '通知类型', 'sys_notice_type', '0', '1', now(), '', null, '通知类型列表');
insert into sys_dict_type
values (8, '通知状态', 'sys_notice_status', '0', '1', now(), '', null, '通知状态列表');
insert into sys_dict_type
values (9, '操作类型', 'sys_oper_type', '0', '1', now(), '', null, '操作类型列表');
insert into sys_dict_type
values (10, '系统状态', 'sys_common_status', '0', '1', now(), '', null, '登录状态列表');


-- ----------------------------
-- 12、字典数据表
-- ----------------------------
drop table if exists sys_dict_data;
create table sys_dict_data
(
    dict_code   bigint(20) not null auto_increment comment '字典编码',
    dict_sort   int(4)       default 0 comment '字典排序',
    dict_label  varchar(100) default '' comment '字典标签',
    dict_value  varchar(100) default '' comment '字典键值',
    dict_type   varchar(100) default '' comment '字典类型',
    css_class   varchar(100) default null comment '样式属性（其他样式扩展）',
    list_class  varchar(100) default null comment '表格回显样式',
    is_default  char(1)      default 'N' comment '是否默认（Y是 N否）',
    status      char(1)      default '0' comment '状态（0正常 1停用）',
    create_by   varchar(64)  default '' comment '创建者',
    create_time timestamp default now()  comment '创建时间',
    update_by   varchar(64)  default '' comment '更新者',
    update_time timestamp default now() ON UPDATE now() comment '更新时间',
    remark      varchar(500) default null comment '备注',
    primary key (dict_code)
) engine = innodb
  auto_increment = 100 comment = '字典数据表';

insert into sys_dict_data
values (1, 1, '男', '0', 'sys_user_sex', '', '', 'Y', '0', '1', now(), '', null, '性别男');
insert into sys_dict_data
values (2, 2, '女', '1', 'sys_user_sex', '', '', 'N', '0', '1', now(), '', null, '性别女');
insert into sys_dict_data
values (3, 3, '未知', '2', 'sys_user_sex', '', '', 'N', '0', '1', now(), '', null, '性别未知');
insert into sys_dict_data
values (4, 1, '显示', '0', 'sys_show_hide', '', 'primary', 'Y', '0', '1', now(), '', null, '显示菜单');
insert into sys_dict_data
values (5, 2, '隐藏', '1', 'sys_show_hide', '', 'danger', 'N', '0', '1', now(), '', null, '隐藏菜单');
insert into sys_dict_data
values (6, 1, '正常', '0', 'sys_normal_disable', '', 'primary', 'Y', '0', '1', now(), '', null, '正常状态');
insert into sys_dict_data
values (7, 2, '停用', '1', 'sys_normal_disable', '', 'danger', 'N', '0', '1', now(), '', null, '停用状态');
insert into sys_dict_data
values (8, 1, '正常', '0', 'sys_job_status', '', 'primary', 'Y', '0', '1', now(), '', null, '正常状态');
insert into sys_dict_data
values (9, 2, '暂停', '1', 'sys_job_status', '', 'danger', 'N', '0', '1', now(), '', null, '停用状态');
insert into sys_dict_data
values (10, 1, '默认', 'DEFAULT', 'sys_job_group', '', '', 'Y', '0', '1', now(), '', null, '默认分组');
insert into sys_dict_data
values (11, 2, '系统', 'SYSTEM', 'sys_job_group', '', '', 'N', '0', '1', now(), '', null, '系统分组');
insert into sys_dict_data
values (12, 1, '是', 'Y', 'sys_yes_no', '', 'primary', 'Y', '0', '1', now(), '', null, '系统默认是');
insert into sys_dict_data
values (13, 2, '否', 'N', 'sys_yes_no', '', 'danger', 'N', '0', '1', now(), '', null, '系统默认否');
insert into sys_dict_data
values (14, 1, '通知', '1', 'sys_notice_type', '', 'warning', 'Y', '0', '1', now(), '', null, '通知');
insert into sys_dict_data
values (15, 2, '公告', '2', 'sys_notice_type', '', 'success', 'N', '0', '1', now(), '', null, '公告');
insert into sys_dict_data
values (16, 1, '正常', '0', 'sys_notice_status', '', 'primary', 'Y', '0', '1', now(), '', null, '正常状态');
insert into sys_dict_data
values (17, 2, '关闭', '1', 'sys_notice_status', '', 'danger', 'N', '0', '1', now(), '', null, '关闭状态');
insert into sys_dict_data
values (18, 99, '其他', '0', 'sys_oper_type', '', 'info', 'N', '0', '1', now(), '', null, '其他操作');
insert into sys_dict_data
values (19, 1, '新增', '1', 'sys_oper_type', '', 'info', 'N', '0', '1', now(), '', null, '新增操作');
insert into sys_dict_data
values (20, 2, '修改', '2', 'sys_oper_type', '', 'info', 'N', '0', '1', now(), '', null, '修改操作');
insert into sys_dict_data
values (21, 3, '删除', '3', 'sys_oper_type', '', 'danger', 'N', '0', '1', now(), '', null, '删除操作');
insert into sys_dict_data
values (22, 4, '授权', '4', 'sys_oper_type', '', 'primary', 'N', '0', '1', now(), '', null, '授权操作');
insert into sys_dict_data
values (23, 5, '导出', '5', 'sys_oper_type', '', 'warning', 'N', '0', '1', now(), '', null, '导出操作');
insert into sys_dict_data
values (24, 6, '导入', '6', 'sys_oper_type', '', 'warning', 'N', '0', '1', now(), '', null, '导入操作');
insert into sys_dict_data
values (25, 7, '强退', '7', 'sys_oper_type', '', 'danger', 'N', '0', '1', now(), '', null, '强退操作');
insert into sys_dict_data
values (26, 8, '生成代码', '8', 'sys_oper_type', '', 'warning', 'N', '0', '1', now(), '', null, '生成操作');
insert into sys_dict_data
values (27, 9, '清空数据', '9', 'sys_oper_type', '', 'danger', 'N', '0', '1', now(), '', null, '清空操作');
insert into sys_dict_data
values (28, 1, '成功', '0', 'sys_common_status', '', 'primary', 'N', '0', '1', now(), '', null, '正常状态');
insert into sys_dict_data
values (29, 2, '失败', '1', 'sys_common_status', '', 'danger', 'N', '0', '1', now(), '', null, '停用状态');

