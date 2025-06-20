drop table if exists `file_info`;
create table `file_info`
(
    `file_id`     bigint(20) not null auto_increment comment '编号',
    `url`         varchar(255) default null comment '资源路径',
    `name`        varchar(128) default null comment '资源原始名称',
    `file_name`   varchar(128) default null comment '资源名称',
    `part_code`      varchar(50)  not null comment 'code用于合并文件',

    `suffix`      varchar(20)  default null COMMENT '后缀名',
    `is_img`      tinyint(1)   default null COMMENT '是否图片',
    `size`        bigint       default null COMMENT '尺寸',
    `type`        varchar(10)  default null COMMENT '文件展示类型，非后缀名',
    `is_dir`      tinyint(1)   default null COMMENT '是否目录',
    `is_local`    tinyint(1)   default 0 comment '是否本地资源',
    `upload_dir`     varchar(255) default null comment '上传目录',
    `parent_id`   bigint       default null,

    `create_by`   varchar(64)  default '' comment '创建者',
    `create_time` timestamp    default now() comment '创建时间',
    `update_by`   varchar(64)  default '' comment '更新者',
    `update_time` timestamp    default now() ON UPDATE now() comment '更新时间',
    `remark`      varchar(500) default null comment '备注',
    primary key (`file_id`)
) engine = innodb
  auto_increment = 1 comment = '文件信息表';

drop table if exists `file_part`;
create table `file_part`
(
    `part_id`        bigint(20)  not null auto_increment comment '编号',
    `part_code`      varchar(50) not null comment 'code用于合并文件',
    `file_id`        bigint(20)   default null comment '文件编号',
    `url`            varchar(255) default null comment '资源路径',
    `local_resource` varchar(255) default null comment '本地资源路径',
    `is_local`       tinyint(1)   default 0 comment '是否本地资源',
    `upload_dir`     varchar(255) default null comment '上传目录',

    `part_size`      bigint       default 0 comment '分片大小',
    `part_sort`      int          default 0 comment '分片顺序',
    `merge_delete`   tinyint(1)   default 0 comment '合并后是否删除分片',
    `os_type`        varchar(20)  default 'linux' comment '操作系统类型 linux,win,mac',
    primary key (`part_id`)
) engine = innodb
  auto_increment = 1 comment = '文件分片表';
#
# ALTER TABLE `file_part`
#     ADD COLUMN os_type VARCHAR(20) DEFAULT 'linux' comment '操作系统类型 linux,win,mac';

 ALTER TABLE `file_part`
         ADD COLUMN  `upload_dir` VARCHAR(255) DEFAULT null comment '上传目录';
ALTER TABLE `file_info`
    ADD COLUMN  `part_code` VARCHAR(255) not null comment 'code用于合并文件';