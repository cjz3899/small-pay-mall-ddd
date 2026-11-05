package com.junzhecai.domain.order.adapter.repository;

import com.junzhecai.domain.order.model.aggregate.CreateOrderAggregate;
import com.junzhecai.domain.order.model.entity.OrderEntity;
import com.junzhecai.domain.order.model.entity.PayOrderEntity;
import com.junzhecai.domain.order.model.entity.ShopCartEntity;

import java.util.List;

public interface IOrderRepository {
    void doSaveOrder(CreateOrderAggregate orderAggregate);

    OrderEntity queryUnpayOrder(ShopCartEntity shopCartEntity);

    void updateOrderPayInfo(PayOrderEntity payOrderEntity);

    void changeOrderPaySuccess(String orderId);

    List<String> queryNoPayNotifyOrder();

    List<String> queryTimeoutCloseOrderList();

    boolean changeOrderClose(String orderId);
}
