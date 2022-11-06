create database seckill;

use sekill;

--  显示创建表的语句
--  show create table sekill\G
create table seckill (
    `seckill_id` bigint not null auto_increment comment '库存商品id',
    `name` varchar(255) not null comment '秒杀商品名称',
    `number` int not null comment '商品库存',
    `start_time` timestamp not null default current_timestamp comment '秒杀开始时间',
    `end_time` timestamp not null default current_timestamp comment '秒杀结束时间',
    `create_time` timestamp not null default current_timestamp comment '创建时间',
    primary key (seckill_id),
    key idx_start_time (start_time),
    key idx_end_time (end_time),
    key idx_create_time (create_time)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8 COMMENT='秒杀库存表';

insert into sekill
    (name, number, start_time, end_time)
values
    ('1000元秒杀IPhone8', 100, '2017-11-27 00:00:00', '2017-11-28 00:00:00'),
    ('500元秒杀Ipad3', 200, '2017-11-27 00:00:00', '2017-11-28 00:00:00'),
    ('2000元秒杀vivo Xplay6', 300, '2017-11-27 00:00:00', '2017-11-28 00:00:00'),
    ('3000元秒杀苹果笔记本', 400, '2017-11-27 00:00:00', '2017-11-28 00:00:00');

--  修改字段名称
--  alter table success_seckilled change column sekill_id seckill_id bigint not null;
create table success_seckilled (
    `seckill_id` bigint not null comment '库存商品id',
    `user_phone` bigint not null comment '用户电话',
    `state` tinyint not null default '0' comment '订单状态：-1：无效 0：成功 1：已付款 2：已发货',
    `create_time` timestamp not null default current_timestamp comment '创建时间',
    primary key (sekill_id, user_phone),
    key idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '秒杀成功明细表';