
-- ----------------------------
-- 1、用户信息表
-- ----------------------------
drop table if exists `sys_user`;
create table `sys_user` (
                          `user_id`           bigint(20)      not null auto_increment       comment '用户ID',
                          `user_name`         varchar(30)     not null                      comment '用户账号',
                          `nick_name`         varchar(30)     not null                      comment '用户昵称',
                          `user_type`         varchar(2)      default '00'                  comment '用户类型（00系统用户）',
                          `email`             varchar(50)     default ''                    comment '用户邮箱',
                          `phone_number`      varchar(11)     default ''                    comment '手机号码',
                          `sex`               char(1)         default '0'                   comment '用户性别（0男 1女 2未知）',
                          `avatar`            varchar(100)    default ''                    comment '头像地址',
                          `password`          varchar(100)    default ''                    comment '密码',
                          `status`            char(1)         default '0'                   comment '帐号状态（0正常 1停用）',
                          `del_flag`          char(1)         default '0'                   comment '删除标志（0代表存在 2代表删除）',
                          `login_ip`          varchar(128)    default ''                    comment '最后登录IP',
                          `login_date`        timestamp                                     comment '最后登录时间',
                          `create_by`         varchar(64)     default ''                    comment '创建者',
                          `create_time`       timestamp       default now()                 comment '创建时间',
                          `update_by`         varchar(64)     default ''                    comment '更新者',
                          `update_time`       timestamp       default now() ON UPDATE now() comment '更新时间',
                          `remark`            varchar(500)    default null                  comment '备注',
                          primary key (`user_id`)
) engine=innodb auto_increment=100 comment = '用户信息表';

-- ----------------------------
-- 初始化-用户信息表数据 password:123456
-- ----------------------------
insert into `sys_user` values(1,'admin', '极简', '00', 'minimalist@163.com', '15888888888', '1', '', '$2a$10$LtDdTn7X3DMbPgWF3bCe1Olf.7GCIDiC0LQk.AGbVle3SnX6R732.', '0', '0', '127.0.0.1', sysdate(), '0', sysdate(), '0', sysdate(), '管理员');
insert into `sys_user` values(2,'minimalism', '极简', '00', 'minimalist@qq.com',  '15666666666', '1', '', '$2a$10$LtDdTn7X3DMbPgWF3bCe1Olf.7GCIDiC0LQk.AGbVle3SnX6R732.', '0', '0', '127.0.0.1', sysdate(), '0', sysdate(), '0', sysdate(), '测试员');

-- ----------------------------
-- 2、角色信息表
-- ----------------------------
drop table if exists `sys_role`;
create table `sys_role` (
                          `role_id`              bigint(20)      not null auto_increment    comment '角色ID',
                          `role_name`            varchar(30)     not null                   comment '角色名称',
                          `role_key`             varchar(100)    not null                   comment '角色权限字符串',
                          `role_sort`            int(4)          not null                   comment '显示顺序',
                          `data_scope`           char(1)         default '1'                comment '数据范围（1：全部数据权限 2：自定数据权限）',
                          `menu_check_strictly`  tinyint(1)      default 1                  comment '菜单树选择项是否关联显示',
                          `status`               char(1)         not null                   comment '角色状态（0正常 1停用）',
                          `del_flag`             char(1)         default '0'                comment '删除标志（0代表存在 2代表删除）',
                          `create_by`         varchar(64)     default ''                    comment '创建者',
                          `create_time`       timestamp       default now()                 comment '创建时间',
                          `update_by`         varchar(64)     default ''                    comment '更新者',
                          `update_time`       timestamp       default now() ON UPDATE now() comment '更新时间',
                          `remark`            varchar(500)    default null                  comment '备注',
                          primary key (`role_id`)
) engine=innodb auto_increment=100 comment = '角色信息表';

-- ----------------------------
-- 初始化-角色信息表数据
-- ----------------------------
insert into `sys_role` values('1', '超级管理员',  'admin',  1, 1, 1, '0', '0', '1', sysdate(), '1', sysdate(), '超级管理员');
insert into `sys_role` values('2', '普通角色',    'user', 2, 2, 1, '0', '0', '1', sysdate(), '1', sysdate(), '普通角色');

-- ----------------------------
-- 3、菜单权限表
-- ----------------------------
drop table if exists `sys_menu`;
create table `sys_menu` (
                          `menu_id`           bigint(20)      not null auto_increment       comment '菜单ID',
                          `menu_name`         varchar(50)     not null                      comment '菜单名称',
                          `parent_id`         bigint(20)      default null                     comment '父菜单ID',
                          `order_num`         int(4)          default 0                     comment '显示顺序',
                          `path`              varchar(200)    default ''                    comment '路由地址',
                          `component`         varchar(255)    default null                  comment '组件路径',
                          `query`             varchar(255)    default null                  comment '路由参数',
                          `route_name`        varchar(50)     default ''                    comment '路由名称',
                          `is_frame`          int(1)          default 1                     comment '是否为外链（0是 1否）',
                          `is_cache`          int(1)          default 0                     comment '是否缓存（0缓存 1不缓存）',
                          `menu_type`         char(1)         default ''                    comment '菜单类型（M目录 C菜单 F按钮）',
                          `visible`           char(1)         default 0                     comment '菜单状态（0显示 1隐藏）',
                          `status`            char(1)         default 0                     comment '菜单状态（0正常 1停用）',
                          `perms`             varchar(100)    default null                  comment '权限标识',
                          `icon`              varchar(100)    default '#'                   comment '菜单图标',
                          `create_by`         varchar(64)     default ''                    comment '创建者',
                          `create_time`       timestamp       default now()                 comment '创建时间',
                          `update_by`         varchar(64)     default ''                    comment '更新者',
                          `update_time`       timestamp       default now() ON UPDATE now() comment '更新时间',
                          `remark`            varchar(500)    default null                  comment '备注',
                          primary key (`menu_id`)
) engine=innodb auto_increment=2000 comment = '菜单权限表';
-- ----------------------------
-- 4、菜单权限祖先表
-- ----------------------------
drop table if exists `sys_menu_ancestor`;
create table `sys_menu_ancestor`(
                                `id` bigint(20) not null auto_increment comment 'id',
                                `menu_id` bigint(20) not null comment '菜单ID',
                                `menu_parent_id` bigint(20) not null comment '上级菜单ID',
                                `level` bigint(20) not null comment '第几级祖先 从自身往上数 0->',
                                primary key (`id`),
                                unique key `k_idx_menu_ancestor_id` (`menu_id`, `menu_parent_id`, `level`),
                                index `idx_menu_parent_id` (`menu_parent_id`, `menu_id`, `level`)
)engine=InnoDB default CHARSET=utf8mb4 comment '菜单权限祖先表';

-- ----------------------------
-- 初始化-菜单信息表数据
-- ----------------------------
-- 一级菜单
insert into `sys_menu` values('1', '系统管理', null, '1', 'system',null, '', '', 1, 0, 'M', '0', '0', '', 'system',   '1', sysdate(), '1', sysdate(), '系统管理目录');
insert into `sys_menu` values('2', '系统监控', null, '2', 'monitor',null, '', '', 1, 0, 'M', '0', '0', '', 'monitor',  '1', sysdate(), '1', sysdate(), '系统监控目录');
insert into `sys_menu` values('3', '系统工具', null, '3', 'tool',null, '', '', 1, 0, 'M', '0', '0', '', 'tool',     '1', sysdate(), '1', sysdate(), '系统工具目录');

-- 二级菜单
insert into sys_menu values('100',  '用户管理',       '1',   '1', 'user',       'system/user/index',                 '', '', 1, 0, 'C', '0', '0', 'system:user:list',        'user',          '1', sysdate(), '1', sysdate(), '用户管理菜单');
insert into sys_menu values('101',  '角色管理',       '1',   '2', 'role',       'system/role/index',                 '', '', 1, 0, 'C', '0', '0', 'system:role:list',        'peoples',       '1', sysdate(), '1', sysdate(), '角色管理菜单');
insert into sys_menu values('102',  '菜单管理',       '1',   '3', 'menu',       'system/menu/index',                 '', '', 1, 0, 'C', '0', '0', 'system:menu:list',        'tree-table',    '1', sysdate(), '1', sysdate(), '菜单管理菜单');

insert into sys_menu values('111',  'Sentinel控制台', '2',   '3', 'http://localhost:8718',        '',                '', '', 0, 0, 'C', '0', '0', 'monitor:sentinel:list',   'sentinel',      '1', sysdate(), '1', sysdate(), '流量控制菜单');
insert into sys_menu values('112',  'Nacos控制台',    '2',   '4', 'http://localhost:8848/nacos',  '',                '', '', 0, 0, 'C', '0', '0', 'monitor:nacos:list',      'nacos',         '1', sysdate(), '1', sysdate(), '服务治理菜单');
insert into sys_menu values('113',  'Admin控制台',    '2',   '5', 'http://localhost:9100/login',  '',                '', '', 0, 0, 'C', '0', '0', 'monitor:server:list',     'server',        '1', sysdate(), '1', sysdate(), '服务监控菜单');
insert into sys_menu values('114',  '表单构建',       '3',   '1', 'build',      'tool/build/index',                  '', '', 1, 0, 'C', '0', '0', 'tool:build:list',         'build',         '1', sysdate(), '1', sysdate(), '表单构建菜单');
insert into sys_menu values('115',  '代码生成',       '3',   '2', 'gen',        'tool/gen/index',                    '', '', 1, 0, 'C', '0', '0', 'tool:gen:list',           'code',          '1', sysdate(), '1', sysdate(), '代码生成菜单');
insert into sys_menu values('116',  '系统接口',       '3',   '3', 'http://localhost:8080/swagger-ui/index.html', '', '', '', 0, 0, 'C', '0', '0', 'tool:swagger:list',       'swagger',       '1', sysdate(), '1', sysdate(), '系统接口菜单');

-- 三级菜单
-- 用户管理按钮
insert into sys_menu values('1000', '用户查询', '100', '1',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:query',          '#', '1', sysdate(), '1', sysdate(), '');
insert into sys_menu values('1001', '用户新增', '100', '2',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:add',            '#', '1', sysdate(), '1', sysdate(), '');
insert into sys_menu values('1002', '用户修改', '100', '3',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:edit',           '#', '1', sysdate(), '1', sysdate(), '');
insert into sys_menu values('1003', '用户删除', '100', '4',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:remove',         '#', '1', sysdate(), '1', sysdate(), '');
insert into sys_menu values('1004', '用户导出', '100', '5',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:export',         '#', '1', sysdate(), '1', sysdate(), '');
insert into sys_menu values('1005', '用户导入', '100', '6',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:import',         '#', '1', sysdate(), '1', sysdate(), '');
insert into sys_menu values('1006', '重置密码', '100', '7',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:resetPwd',       '#', '1', sysdate(), '1', sysdate(), '');
-- 角色管理按钮
insert into sys_menu values('1007', '角色查询', '101', '1',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:query',          '#', '1', sysdate(), '1', sysdate(), '');
insert into sys_menu values('1008', '角色新增', '101', '2',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:add',            '#', '1', sysdate(), '1', sysdate(), '');
insert into sys_menu values('1009', '角色修改', '101', '3',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:edit',           '#', '1', sysdate(), '1', sysdate(), '');
insert into sys_menu values('1010', '角色删除', '101', '4',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:remove',         '#', '1', sysdate(), '1', sysdate(), '');
insert into sys_menu values('1011', '角色导出', '101', '5',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:export',         '#', '1', sysdate(), '1', sysdate(), '');
-- 菜单管理按钮
insert into sys_menu values('1012', '菜单查询', '102', '1',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:menu:query',          '#', '1', sysdate(), '1', sysdate(), '');
insert into sys_menu values('1013', '菜单新增', '102', '2',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:menu:add',            '#', '1', sysdate(), '1', sysdate(), '');
insert into sys_menu values('1014', '菜单修改', '102', '3',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:menu:edit',           '#', '1', sysdate(), '1', sysdate(), '');
insert into sys_menu values('1015', '菜单删除', '102', '4',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:menu:remove',         '#', '1', sysdate(), '1', sysdate(), '');
-- 代码生成按钮
insert into sys_menu values('1055', '生成查询', '115', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:query',            '#', '1', sysdate(), '1', sysdate(), '');
insert into sys_menu values('1056', '生成修改', '115', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:edit',             '#', '1', sysdate(), '1', sysdate(), '');
insert into sys_menu values('1057', '生成删除', '115', '3', '#', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:remove',           '#', '1', sysdate(), '1', sysdate(), '');
insert into sys_menu values('1058', '导入代码', '115', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:import',           '#', '1', sysdate(), '1', sysdate(), '');
insert into sys_menu values('1059', '预览代码', '115', '4', '#', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:preview',          '#', '1', sysdate(), '1', sysdate(), '');
insert into sys_menu values('1060', '生成代码', '115', '5', '#', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:code',             '#', '1', sysdate(), '1', sysdate(), '');

SELECT @parentId := '0';
SELECT @queryId := '0';
SELECT @addId := '0';
SELECT @editId := '0';
SELECT @removeId := '0';
SELECT @exportId := '0';
SELECT @importId := '0';
-- 插入 sys_menu_ancestor 记录
INSERT INTO sys_menu_ancestor (menu_id, menu_parent_id, level)
SELECT menu_id, parent_id, level
FROM (
         SELECT @parentId as menu_id, @parentId as parent_id, 0 as level
         UNION ALL
         -- 递归查询所有父级菜单 ID
         (WITH RECURSIVE ParentMenus AS (
             SELECT menu_id, parent_id, 1 AS level
             FROM sys_menu
             WHERE menu_id = @parentId
             UNION ALL
             SELECT m.menu_id, m.parent_id, pm.level + 1
             FROM sys_menu m
                      INNER JOIN ParentMenus pm ON m.menu_id = pm.parent_id
         )
          SELECT @parentId as menu_id, parent_id, level
          FROM ParentMenus
          WHERE parent_id != 0)

         UNION ALL
         SELECT @queryId as menu_id, @queryId as parent_id, 0 as level
         UNION ALL
         -- 递归查询所有父级菜单 ID
         (WITH RECURSIVE ParentMenus AS (
             SELECT menu_id, parent_id, 1 AS level
             FROM sys_menu
             WHERE menu_id = @queryId
             UNION ALL
             SELECT m.menu_id, m.parent_id, pm.level + 1
             FROM sys_menu m
                      INNER JOIN ParentMenus pm ON m.menu_id = pm.parent_id
         )
          SELECT @queryId as menu_id, parent_id, level
          FROM ParentMenus
          WHERE parent_id != 0)

         UNION ALL
         SELECT @addId as menu_id, @addId as parent_id, 0 as level
         UNION ALL
         -- 递归查询所有父级菜单 ID
         (WITH RECURSIVE ParentMenus AS (
             SELECT menu_id, parent_id, 1 AS level
             FROM sys_menu
             WHERE menu_id = @addId
             UNION ALL
             SELECT m.menu_id, m.parent_id, pm.level + 1
             FROM sys_menu m
                      INNER JOIN ParentMenus pm ON m.menu_id = pm.parent_id
         )
          SELECT @addId as menu_id, parent_id, level
          FROM ParentMenus
          WHERE parent_id != 0)

         UNION ALL
         SELECT @editId as menu_id, @editId as parent_id, 0 as level
         UNION ALL
         -- 递归查询所有父级菜单 ID
         (WITH RECURSIVE ParentMenus AS (
             SELECT menu_id, parent_id, 1 AS level
             FROM sys_menu
             WHERE menu_id = @editId
             UNION ALL
             SELECT m.menu_id, m.parent_id, pm.level + 1
             FROM sys_menu m
                      INNER JOIN ParentMenus pm ON m.menu_id = pm.parent_id
         )
          SELECT @editId as menu_id, parent_id, level
          FROM ParentMenus
          WHERE parent_id != 0)

         UNION ALL
         SELECT @removeId as menu_id, @removeId as parent_id, 0 as level
         UNION ALL
         -- 递归查询所有父级菜单 ID
         (WITH RECURSIVE ParentMenus AS (
             SELECT menu_id, parent_id, 1 AS level
             FROM sys_menu
             WHERE menu_id = @removeId
             UNION ALL
             SELECT m.menu_id, m.parent_id, pm.level + 1
             FROM sys_menu m
                      INNER JOIN ParentMenus pm ON m.menu_id = pm.parent_id
         )
          SELECT @removeId as menu_id, parent_id, level
          FROM ParentMenus
          WHERE parent_id != 0)

         UNION ALL
         SELECT @exportId as menu_id, @exportId as parent_id, 0 as level
         UNION ALL
         -- 递归查询所有父级菜单 ID
         (WITH RECURSIVE ParentMenus AS (
             SELECT menu_id, parent_id, 1 AS level
             FROM sys_menu
             WHERE menu_id = @exportId
             UNION ALL
             SELECT m.menu_id, m.parent_id, pm.level + 1
             FROM sys_menu m
                      INNER JOIN ParentMenus pm ON m.menu_id = pm.parent_id
         )
          SELECT @exportId as menu_id, parent_id, level
          FROM ParentMenus
          WHERE parent_id != 0)

         UNION ALL
         SELECT @importId as menu_id, @exportId as parent_id, 0 as level
         UNION ALL
         -- 递归查询所有父级菜单 ID
         (WITH RECURSIVE ParentMenus AS (
             SELECT menu_id, parent_id, 1 AS level
             FROM sys_menu
             WHERE menu_id = @exportId
             UNION ALL
             SELECT m.menu_id, m.parent_id, pm.level + 1
             FROM sys_menu m
                      INNER JOIN ParentMenus pm ON m.menu_id = pm.parent_id
         )
          SELECT @exportId as menu_id, parent_id, level
          FROM ParentMenus
          WHERE parent_id != 0)
     ) AS subquery;

-- 菜单权限祖先 关联
insert into `sys_menu_ancestor`(`menu_id`, `menu_parent_id`, `level`)
values ('1','1','0'),('2','2','0'),('3','3','0'),

       ('100','100','0'),('101','101','0'),('102','102','0'),
       ('100','1','1'),('101','1','1'),('102','1','1'),

       ('111','111','0'),('112','112','0'),('113','113','0'),('114','114','0'),('115','115','0'),('116','116','0'),
       ('111','2','1'),('112','2','1'),('113','2','1'),('114','3','1'),('115','3','1'),('116','3','1'),

       ('1000','1000','0'),('1001','1001','0'),('1002','1002','0'),('1003','1003','0'),('1004','1004','0'),('1005','1005','0'),
       ('1006','1006','0'),('1007','1007','0'),('1008','1008','0'),('1009','1009','0'),('1010','1010','0'),('1011','1011','0'),
       ('1012','1012','0'),('1013','1013','0'),('1014','1014','0'),('1015','1015','0'),
       ('1000','100','1'),('1001','100','1'),('1002','100','1'),('1003','100','1'),('1004','100','1'),('1005','100','1'),
       ('1006','100','1'),('1007','101','1'),('1008','101','1'),('1009','101','1'),('1010','101','1'),('1011','101','1'),
       ('1012','102','1'),('1013','102','1'),('1014','102','1'),('1015','102','1'),
       ('1000','1','2'),('1001','1','2'),('1002','1','2'),('1003','1','2'),('1004','1','2'),('1005','1','2'),
       ('1006','1','2'),('1007','1','2'),('1008','1','2'),('1009','1','2'),('1010','1','2'),('1011','1','2'),
       ('1012','1','2'),('1013','1','2'),('1014','1','2'),('1015','1','2');

-- ----------------------------
-- 5、用户和角色关联表  用户N-1角色
-- ----------------------------
drop table if exists `sys_user_role`;
create table `sys_user_role` (
                               `user_id`   bigint(20) not null comment '用户ID',
                               `role_id`   bigint(20) not null comment '角色ID',
                               primary key(`user_id`, `role_id`)
) engine=innodb comment = '用户和角色关联表';

-- ----------------------------
-- 初始化-用户和角色关联表数据
-- ----------------------------
insert into `sys_user_role` values ('1', '1');
insert into `sys_user_role` values ('2', '2');


-- ----------------------------
-- 6、角色和菜单关联表  角色1-N菜单
-- ----------------------------
drop table if exists `sys_role_menu`;
create table `sys_role_menu` (
                               `role_id`   bigint(20) not null comment '角色ID',
                               `menu_id`   bigint(20) not null comment '菜单ID',
                               primary key(`role_id`, `menu_id`)
) engine=innodb comment = '角色和菜单关联表';

-- ----------------------------
-- 初始化-角色和菜单关联表数据
-- ----------------------------
insert into sys_role_menu values ('2', '1');
insert into sys_role_menu values ('2', '2');
insert into sys_role_menu values ('2', '3');
insert into sys_role_menu values ('2', '100');
insert into sys_role_menu values ('2', '101');
insert into sys_role_menu values ('2', '102');
insert into sys_role_menu values ('2', '1000');
insert into sys_role_menu values ('2', '1001');
insert into sys_role_menu values ('2', '1002');
insert into sys_role_menu values ('2', '1003');
insert into sys_role_menu values ('2', '1004');
insert into sys_role_menu values ('2', '1005');
insert into sys_role_menu values ('2', '1006');
insert into sys_role_menu values ('2', '1007');
insert into sys_role_menu values ('2', '1008');
insert into sys_role_menu values ('2', '1009');
insert into sys_role_menu values ('2', '1010');
insert into sys_role_menu values ('2', '1011');
insert into sys_role_menu values ('2', '1012');
insert into sys_role_menu values ('2', '1013');
insert into sys_role_menu values ('2', '1014');
insert into sys_role_menu values ('2', '1015');
