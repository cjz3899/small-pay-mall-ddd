-- auto-generated definition
create table pay_order
(
    id           int unsigned auto_increment comment '自增ID'
        primary key,
    user_id      varchar(32)                        not null comment '用户ID',
    product_id   varchar(16)                        not null comment '商品ID',
    product_name varchar(64)                        not null comment '商品名称',
    order_id     varchar(16)                        not null comment '订单ID',
    order_time   datetime                           not null comment '下单时间',
    total_amount decimal(8, 2) unsigned             null comment '订单金额',
    status       varchar(32)                        not null comment '订单状态；create-创建完成、pay_wait-等待支付、pay_success-支付成功、deal_done-交易完成、close-订单关单',
    pay_url      varchar(2014)                      null comment '支付信息',
    pay_time     datetime                           null comment '支付时间',
    create_time  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint uq_order_id
        unique (order_id)
)
    engine = InnoDB;

create index idx_user_id_product_id
    on pay_order (user_id, product_id);
