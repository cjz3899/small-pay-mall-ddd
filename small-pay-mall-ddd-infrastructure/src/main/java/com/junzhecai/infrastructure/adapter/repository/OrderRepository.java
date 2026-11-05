package com.junzhecai.infrastructure.adapter.repository;

import com.alibaba.fastjson.JSON;
import com.google.common.eventbus.EventBus;
import com.junzhecai.domain.order.adapter.repository.IOrderRepository;
import com.junzhecai.domain.order.event.PaySuccessMessageEvent;
import com.junzhecai.domain.order.model.aggregate.CreateOrderAggregate;
import com.junzhecai.domain.order.model.entity.OrderEntity;
import com.junzhecai.domain.order.model.entity.PayOrderEntity;
import com.junzhecai.domain.order.model.entity.ProductEntity;
import com.junzhecai.domain.order.model.entity.ShopCartEntity;
import com.junzhecai.domain.order.model.valobj.OrderStatusVO;
import com.junzhecai.infrastructure.dao.IOrderDao;
import com.junzhecai.infrastructure.dao.po.PayOrderPO;
import com.junzhecai.types.event.BaseEvent;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderRepository implements IOrderRepository {

    @Resource
    private IOrderDao orderDao;

    @Resource
    private PaySuccessMessageEvent paySuccessMessageEvent;

    @Resource
    private EventBus eventBus;

    @Override
    public void doSaveOrder(CreateOrderAggregate orderAggregate) {
        String userId = orderAggregate.getUserId();
        ProductEntity productEntity = orderAggregate.getProductEntity();
        OrderEntity orderEntity = orderAggregate.getOrderEntity();

        PayOrderPO order = new PayOrderPO();
        order.setUserId(userId);
        order.setProductId(productEntity.getProductId());
        order.setProductName(productEntity.getProductName());
        order.setOrderId(orderEntity.getOrderId());
        order.setOrderTime(orderEntity.getOrderTime());
        order.setTotalAmount(productEntity.getPrice());
        order.setStatus(orderEntity.getOrderStatusVO().getCode());

        orderDao.insert(order);
    }

    @Override
    public OrderEntity queryUnpayOrder(ShopCartEntity shopCartEntity) {
        PayOrderPO orderReq = new PayOrderPO();
        orderReq.setUserId(shopCartEntity.getUserId());
        orderReq.setProductId(shopCartEntity.getProductId());

        PayOrderPO order = orderDao.queryUnpayOrder(orderReq);
        if (null == order) {
            return null;
        }

        return OrderEntity.builder()
                .productId(order.getProductId())
                .productName(order.getProductName())
                .orderId(order.getOrderId())
                .orderStatusVO(OrderStatusVO.valueOf(order.getStatus()))
                .orderTime(order.getOrderTime())
                .totalAmount(order.getTotalAmount())
                .payUrl(order.getPayUrl())
                .build();
    }

    @Override
    public void updateOrderPayInfo(PayOrderEntity payOrderEntity) {
        PayOrderPO payOrderReq = PayOrderPO.builder()
                .userId(payOrderEntity.getUserId())
                .orderId(payOrderEntity.getOrderId())
                .status(payOrderEntity.getOrderStatus().getCode())
                .payUrl(payOrderEntity.getPayUrl())
                .build();
        orderDao.updateOrderPayInfo(payOrderReq);
    }

    @Override
    public void changeOrderPaySuccess(String orderId) {
        PayOrderPO order = new PayOrderPO();
        order.setOrderId(orderId);
        order.setStatus(OrderStatusVO.PAY_SUCCESS.getCode());
        orderDao.changeOrderPaySuccess(order);

        // 发送MQ消息
        BaseEvent.EventMessage<PaySuccessMessageEvent.PaySuccessMessage> eventMessage = paySuccessMessageEvent.buildEventMessage(PaySuccessMessageEvent.PaySuccessMessage.builder().tradeNo(orderId).build());
        PaySuccessMessageEvent.PaySuccessMessage paySuccessMessage = eventMessage.getData();

        eventBus.post(JSON.toJSONString(paySuccessMessage));

    }

    @Override
    public List<String> queryNoPayNotifyOrder() {
        return orderDao.queryNoPayNotifyOrder();
    }

    @Override
    public List<String> queryTimeoutCloseOrderList() {
        return orderDao.queryTimeoutCloseOrderList();
    }

    @Override
    public boolean changeOrderClose(String orderId) {
        return orderDao.changeOrderPayClose(orderId);
    }
}
