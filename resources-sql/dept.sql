-- ----------------------------
-- 1、部门表
-- ----------------------------
drop table if exists sys_dept;
create table sys_dept
(
    dept_id     bigint(20) not null auto_increment comment '部门id',
    parent_id   bigint(20)  default null comment '父部门id',
#                           ancestors         varchar(50)     default ''                 comment '祖级列表',
    dept_name   varchar(30) default '' comment '部门名称',
    order_num   int(4)      default 0 comment '显示顺序',
    leader      varchar(20) default null comment '负责人',
    phone       varchar(11) default null comment '联系电话',
    email       varchar(50) default null comment '邮箱',
    status      char(1)     default '0' comment '部门状态（0正常 1停用）',
    del_flag    char(1)     default '0' comment '删除标志（0代表存在 2代表删除）',
    create_by   varchar(64) default '' comment '创建者',
    create_time timestamp   default now() comment '创建时间',
    update_by   varchar(64) default '' comment '更新者',
    update_time timestamp   default now() on update now() comment '更新时间',
    primary key (dept_id)
) engine = innodb
  auto_increment = 200 comment = '部门表';

-- ----------------------------
-- 初始化-部门表数据
-- ----------------------------
insert into sys_dept
values (100, null, /*'0',  */ '若依科技', 0, '若依', '15888888888', 'ry@qq.com', '0', '0', '1', sysdate(), '1', null);
# select @insert1 := LAST_INSERT_ID();
insert into sys_dept
values (101, 100, /*'0,100', */ '深圳总公司', 1, '若依', '15888888888', 'ry@qq.com', '0', '0', '1', sysdate(), '1',
        null);
# select @insert2 := LAST_INSERT_ID();
insert into sys_dept
values (102, 100, /*'0,100', */ '长沙分公司', 2, '若依', '15888888888', 'ry@qq.com', '0', '0', '1', sysdate(), '1',
        null);
# select @insert3 := LAST_INSERT_ID();
insert into sys_dept
values (103, 101, /*'0,100,101',*/ '研发部门', 1, '若依', '15888888888', 'ry@qq.com', '0', '0', '1', sysdate(), '1',
        null);
# select @insert4 := LAST_INSERT_ID();
insert into sys_dept
values (104, 101, /*'0,100,101',*/ '市场部门', 2, '若依', '15888888888', 'ry@qq.com', '0', '0', '1', sysdate(), '1',
        null);
# select @insert5 := LAST_INSERT_ID();
insert into sys_dept
values (105, 101, /*'0,100,101', */ '测试部门', 3, '若依', '15888888888', 'ry@qq.com', '0', '0', '1', sysdate(), '1',
        null);
# select @insert6 := LAST_INSERT_ID();
insert into sys_dept
values (106, 101, /*'0,100,101', */ '财务部门', 4, '若依', '15888888888', 'ry@qq.com', '0', '0', '1', sysdate(), '1',
        null);
# select @insert7 := LAST_INSERT_ID();
insert into sys_dept
values (107, 101, /*'0,100,101', */ '运维部门', 5, '若依', '15888888888', 'ry@qq.com', '0', '0', '1', sysdate(), '1',
        null);
# select @insert8 := LAST_INSERT_ID();
insert into sys_dept
values (108, 102, /*'0,100,102',*/ '市场部门', 1, '若依', '15888888888', 'ry@qq.com', '0', '0', '1', sysdate(), '1',
        null);
# select @insert9 := LAST_INSERT_ID();
insert into sys_dept
values (109, 102, /*'0,100,102',*/ '财务部门', 2, '若依', '15888888888', 'ry@qq.com', '0', '0', '1', sysdate(), '1',
        null);
# select @insert10 := LAST_INSERT_ID();
-- ----------------------------
-- 2、部门祖先表
-- ----------------------------
drop table if exists `sys_dept_ancestor`;
create table `sys_dept_ancestor`
(
    `id`             bigint(20) not null auto_increment comment 'id',
    `dept_id`        bigint(20) not null comment 'ID',
    `dept_parent_id` bigint(20) not null comment '上级ID',
    `level`          bigint(20) not null comment '第几级祖先 从自身往上数 0->',
    primary key (`id`),
    unique key `k_idx_dept_ancestor_id` (`dept_id`, `dept_parent_id`, `level`),
    index `idx_dept_ancestor_id` (`level`,`dept_parent_id`, `dept_id`)
) engine = InnoDB
  default CHARSET = utf8mb4 comment '部门祖先表';

-- ----------------------------
-- 3、角色和部门关联表  角色1-N部门
-- ----------------------------
drop table if exists sys_role_dept;
create table sys_role_dept
(
    id      bigint(20) not null auto_increment comment 'id',
    role_id bigint(20) not null comment '角色ID',
    dept_id bigint(20) not null comment '部门ID',
    primary key (id),
    unique `k_idx_role_dept_id` (`role_id`, `dept_id`),
    index `idx_dept_role_id` (`dept_id`, `role_id`)
) engine = innodb comment = '角色和部门关联表';

-- ----------------------------
-- 初始化-角色和部门关联表数据
-- ----------------------------
insert into sys_role_dept(role_id, dept_id)
values ('2', '100');
insert into sys_role_dept(role_id, dept_id)
values ('2', '101');
insert into sys_role_dept(role_id, dept_id)
values ('2', '105');

-- ----------------------------
-- 4、用户部门关联表
-- ----------------------------
drop table if exists `sys_user_dept`;
create table `sys_user_dept`
(
    `id`      bigint(20) not null auto_increment comment 'id',
    `user_id` bigint(20) not null comment '用户id',
    `dept_id` bigint(20) not null comment '部门id',
    primary key (`id`),
    unique key `k_idx_user_dept_id` (`user_id`, `dept_id`),
    index `idx_dept_user_id` (`dept_id`, `user_id`)
) engine = InnoDB
  default CHARSET = utf8mb4 comment '用户部门关联表';

--
-- 可通过gen 中的 生成祖先表插入sql 接口 生成
--
INSERT INTO `sys_dept_ancestor` (`dept_id`, `dept_parent_id`, `level`)
SELECT `dept_id`, `parent_id`, `level`
FROM (SELECT 100 as `dept_id`, 100 as `parent_id`, 0 as `level`
      UNION ALL
      (WITH RECURSIVE ParentMenus AS (SELECT `dept_id`, `parent_id`, 1 AS `level`
                                      FROM `sys_dept`
                                      WHERE `dept_id` = 100
                                      UNION ALL
                                      SELECT m.`dept_id`, m.`parent_id`, pm.`level` + 1
                                      FROM `sys_dept` m
                                               INNER JOIN ParentMenus pm ON m.`dept_id` = pm.`parent_id`)
       SELECT 100 as `dept_id`, `parent_id`, `level`
       FROM ParentMenus
       WHERE `parent_id` != 0)
      UNION ALL
      SELECT 101 as `dept_id`, 101 as `parent_id`, 0 as `level`
      UNION ALL
      (WITH RECURSIVE ParentMenus AS (SELECT `dept_id`, `parent_id`, 1 AS `level`
                                      FROM `sys_dept`
                                      WHERE `dept_id` = 101
                                      UNION ALL
                                      SELECT m.`dept_id`, m.`parent_id`, pm.`level` + 1
                                      FROM `sys_dept` m
                                               INNER JOIN ParentMenus pm ON m.`dept_id` = pm.`parent_id`)
       SELECT 101 as `dept_id`, `parent_id`, `level`
       FROM ParentMenus
       WHERE `parent_id` != 0)
      UNION ALL
      SELECT 102 as `dept_id`, 102 as `parent_id`, 0 as `level`
      UNION ALL
      (WITH RECURSIVE ParentMenus AS (SELECT `dept_id`, `parent_id`, 1 AS `level`
                                      FROM `sys_dept`
                                      WHERE `dept_id` = 102
                                      UNION ALL
                                      SELECT m.`dept_id`, m.`parent_id`, pm.`level` + 1
                                      FROM `sys_dept` m
                                               INNER JOIN ParentMenus pm ON m.`dept_id` = pm.`parent_id`)
       SELECT 102 as `dept_id`, `parent_id`, `level`
       FROM ParentMenus
       WHERE `parent_id` != 0)
      UNION ALL
      SELECT 103 as `dept_id`, 103 as `parent_id`, 0 as `level`
      UNION ALL
      (WITH RECURSIVE ParentMenus AS (SELECT `dept_id`, `parent_id`, 1 AS `level`
                                      FROM `sys_dept`
                                      WHERE `dept_id` = 103
                                      UNION ALL
                                      SELECT m.`dept_id`, m.`parent_id`, pm.`level` + 1
                                      FROM `sys_dept` m
                                               INNER JOIN ParentMenus pm ON m.`dept_id` = pm.`parent_id`)
       SELECT 103 as `dept_id`, `parent_id`, `level`
       FROM ParentMenus
       WHERE `parent_id` != 0)
      UNION ALL
      SELECT 104 as `dept_id`, 104 as `parent_id`, 0 as `level`
      UNION ALL
      (WITH RECURSIVE ParentMenus AS (SELECT `dept_id`, `parent_id`, 1 AS `level`
                                      FROM `sys_dept`
                                      WHERE `dept_id` = 104
                                      UNION ALL
                                      SELECT m.`dept_id`, m.`parent_id`, pm.`level` + 1
                                      FROM `sys_dept` m
                                               INNER JOIN ParentMenus pm ON m.`dept_id` = pm.`parent_id`)
       SELECT 104 as `dept_id`, `parent_id`, `level`
       FROM ParentMenus
       WHERE `parent_id` != 0)
      UNION ALL
      SELECT 105 as `dept_id`, 105 as `parent_id`, 0 as `level`
      UNION ALL
      (WITH RECURSIVE ParentMenus AS (SELECT `dept_id`, `parent_id`, 1 AS `level`
                                      FROM `sys_dept`
                                      WHERE `dept_id` = 105
                                      UNION ALL
                                      SELECT m.`dept_id`, m.`parent_id`, pm.`level` + 1
                                      FROM `sys_dept` m
                                               INNER JOIN ParentMenus pm ON m.`dept_id` = pm.`parent_id`)
       SELECT 105 as `dept_id`, `parent_id`, `level`
       FROM ParentMenus
       WHERE `parent_id` != 0)
      UNION ALL
      SELECT 106 as `dept_id`, 106 as `parent_id`, 0 as `level`
      UNION ALL
      (WITH RECURSIVE ParentMenus AS (SELECT `dept_id`, `parent_id`, 1 AS `level`
                                      FROM `sys_dept`
                                      WHERE `dept_id` = 106
                                      UNION ALL
                                      SELECT m.`dept_id`, m.`parent_id`, pm.`level` + 1
                                      FROM `sys_dept` m
                                               INNER JOIN ParentMenus pm ON m.`dept_id` = pm.`parent_id`)
       SELECT 106 as `dept_id`, `parent_id`, `level`
       FROM ParentMenus
       WHERE `parent_id` != 0)
      UNION ALL
      SELECT 107 as `dept_id`, 107 as `parent_id`, 0 as `level`
      UNION ALL
      (WITH RECURSIVE ParentMenus AS (SELECT `dept_id`, `parent_id`, 1 AS `level`
                                      FROM `sys_dept`
                                      WHERE `dept_id` = 107
                                      UNION ALL
                                      SELECT m.`dept_id`, m.`parent_id`, pm.`level` + 1
                                      FROM `sys_dept` m
                                               INNER JOIN ParentMenus pm ON m.`dept_id` = pm.`parent_id`)
       SELECT 107 as `dept_id`, `parent_id`, `level`
       FROM ParentMenus
       WHERE `parent_id` != 0)
      UNION ALL
      SELECT 108 as `dept_id`, 108 as `parent_id`, 0 as `level`
      UNION ALL
      (WITH RECURSIVE ParentMenus AS (SELECT `dept_id`, `parent_id`, 1 AS `level`
                                      FROM `sys_dept`
                                      WHERE `dept_id` = 108
                                      UNION ALL
                                      SELECT m.`dept_id`, m.`parent_id`, pm.`level` + 1
                                      FROM `sys_dept` m
                                               INNER JOIN ParentMenus pm ON m.`dept_id` = pm.`parent_id`)
       SELECT 108 as `dept_id`, `parent_id`, `level`
       FROM ParentMenus
       WHERE `parent_id` != 0)
      UNION ALL
      SELECT 109 as `dept_id`, 109 as `parent_id`, 0 as `level`
      UNION ALL
      (WITH RECURSIVE ParentMenus AS (SELECT `dept_id`, `parent_id`, 1 AS `level`
                                      FROM `sys_dept`
                                      WHERE `dept_id` = 109
                                      UNION ALL
                                      SELECT m.`dept_id`, m.`parent_id`, pm.`level` + 1
                                      FROM `sys_dept` m
                                               INNER JOIN ParentMenus pm ON m.`dept_id` = pm.`parent_id`)
       SELECT 109 as `dept_id`, `parent_id`, `level`
       FROM ParentMenus
       WHERE `parent_id` != 0)) AS subquery;