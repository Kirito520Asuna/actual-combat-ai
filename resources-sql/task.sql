
-- ----------------------------
-- 15、定时任务调度表
-- ----------------------------
drop table if exists sys_job;
create table sys_job (
                         job_id              bigint(20)    not null auto_increment    comment '任务ID',
                         job_name            varchar(64)   default ''                 comment '任务名称',
                         job_group           varchar(64)   default 'DEFAULT'          comment '任务组名',
                         invoke_target       varchar(500)  not null                   comment '调用目标字符串',
                         cron_expression     varchar(255)  default ''                 comment 'cron执行表达式',
                         misfire_policy      varchar(20)   default '3'                comment '计划执行错误策略（1立即执行 2执行一次 3放弃执行）',
                         concurrent          char(1)       default '1'                comment '是否并发执行（0允许 1禁止）',
                         status              char(1)       default '0'                comment '状态（0正常 1暂停）',

                         `create_by`         varchar(64)     default ''                    comment '创建者',
                         `create_time`       timestamp       default now()                 comment '创建时间',
                         `update_by`         varchar(64)     default ''                    comment '更新者',
                         `update_time`       timestamp       default now() ON UPDATE now() comment '更新时间',
                         `remark`            varchar(500)    default null                  comment '备注',

                         primary key (job_id, job_name, job_group)
) engine=innodb auto_increment=100 comment = '定时任务调度表';

insert into sys_job values(1, '系统默认（无参）', 'DEFAULT', 'task.noParams',        '0/10 * * * * ?', '3', '1', '1', '', now() , '', now() , '');
insert into sys_job values(2, '系统默认（有参）', 'DEFAULT', 'task.params(\'minm\')',  '0/15 * * * * ?', '3', '1', '1', '', now() ,'', now() , '');
insert into sys_job values(3, '系统默认（多参）', 'DEFAULT', 'task.multipleParams(\'minm\', true, 2000L, 316.50D, 100)',  '0/20 * * * * ?', '3', '1', '1', '', now() , '', now() , '');


-- ----------------------------
-- 16、定时任务调度日志表
-- ----------------------------
drop table if exists sys_job_log;
create table sys_job_log (
                             job_log_id          bigint(20)     not null auto_increment    comment '任务日志ID',
                             job_name            varchar(64)    not null                   comment '任务名称',
                             job_group           varchar(64)    not null                   comment '任务组名',
                             invoke_target       varchar(500)   not null                   comment '调用目标字符串',
                             job_message         varchar(500)                              comment '日志信息',
                             status              char(1)        default '0'                comment '执行状态（0正常 1失败）',
                             exception_info      varchar(2000)  default ''                 comment '异常信息',
                             create_time         timestamp      default now()              comment '创建时间',
                             primary key (job_log_id)
) engine=innodb comment = '定时任务调度日志表';