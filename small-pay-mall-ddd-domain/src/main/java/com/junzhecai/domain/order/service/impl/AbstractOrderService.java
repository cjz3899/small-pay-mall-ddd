package com.junzhecai.domain.order.service.impl;

import com.junzhecai.domain.order.adapter.port.IProductPort;
import com.junzhecai.domain.order.adapter.repository.IOrderRepository;
import com.junzhecai.domain.order.model.aggregate.CreateOrderAggregate;
import com.junzhecai.domain.order.model.entity.OrderEntity;
import com.junzhecai.domain.order.model.entity.PayOrderEntity;
import com.junzhecai.domain.order.model.entity.ProductEntity;
import com.junzhecai.domain.order.model.entity.ShopCartEntity;
import com.junzhecai.domain.order.model.valobj.OrderStatusVO;
import com.junzhecai.domain.order.service.IOrderService;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;


@Slf4j
public abstract class AbstractOrderService implements IOrderService {
    protected final IOrderRepository orderRepository;

    protected final IProductPort productPort;

    public AbstractOrderService(IOrderRepository orderRepository, IProductPort productPort) {
        this.orderRepository = orderRepository;
        this.productPort = productPort;
    }

    @Override
    public PayOrderEntity createOrder(ShopCartEntity shopCartEntity) throws Exception {
        //查询未支付订单或者掉单订单
        OrderEntity unpayOrderEntity = orderRepository.queryUnpayOrder(shopCartEntity);
        if (null != unpayOrderEntity && OrderStatusVO.PAY_WAIT.equals(unpayOrderEntity.getOrderStatusVO())) {
            log.info("已存在未支付订单 userId:{} productId:{} orderId:{}", shopCartEntity.getUserId(), shopCartEntity.getProductId(), unpayOrderEntity.getOrderId());
            return PayOrderEntity.builder()
                    .orderId(unpayOrderEntity.getOrderId())
                    .payUrl(unpayOrderEntity.getPayUrl())
                    .build();
        } else if (null != unpayOrderEntity && OrderStatusVO.CREATE.equals(unpayOrderEntity.getOrderStatusVO())) {
            log.info("创建订单-存在，存在未创建支付单订单，创建支付单开始 userId:{} productId:{} orderId:{}", shopCartEntity.getUserId(), shopCartEntity.getProductId(), unpayOrderEntity.getOrderId());
            PayOrderEntity payOrderEntity = doPrepayOrder(shopCartEntity.getUserId(), shopCartEntity.getProductId(), unpayOrderEntity.getProductName(), unpayOrderEntity.getOrderId(), unpayOrderEntity.getTotalAmount());
            return PayOrderEntity.builder()
                    .orderId(payOrderEntity.getOrderId())
                    .payUrl(payOrderEntity.getPayUrl())
                    .build();
        }


        //查询商品
        ProductEntity productEntity = productPort.queryProductByProductId(shopCartEntity.getProductId());

        //创建订单的聚合对象
        OrderEntity orderEntity = CreateOrderAggregate.buildOrderEntity(productEntity.getProductId(), productEntity.getProductName());
        CreateOrderAggregate orderAggregate = CreateOrderAggregate.builder()
                .userId(shopCartEntity.getUserId())
                .productEntity(productEntity)
                .orderEntity(orderEntity)
                .build();

        this.doSaveOrder(orderAggregate);

        //创建支付单
        PayOrderEntity payOrderEntity = doPrepayOrder(shopCartEntity.getUserId(), productEntity.getProductId(), productEntity.getProductName(), orderEntity.getOrderId(), productEntity.getPrice());
        log.info("创建订单-完成，生成支付单。userId: {} orderId: {} payUrl: {}", shopCartEntity.getUserId(), orderEntity.getOrderId(), payOrderEntity.getPayUrl());

        return PayOrderEntity.builder()
                .orderId(orderEntity.getOrderId())
                .payUrl(payOrderEntity.getPayUrl())
                .build();

    }

    protected abstract PayOrderEntity doPrepayOrder(String userId, String productId, String productName, String orderId, BigDecimal totalAmount);


    protected abstract void doSaveOrder(CreateOrderAggregate orderAggregate);


}
