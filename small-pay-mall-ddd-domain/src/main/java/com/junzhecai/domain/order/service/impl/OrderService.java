package com.junzhecai.domain.order.service.impl;

import com.junzhecai.domain.auth.adapter.port.IPayPort;
import com.junzhecai.domain.order.adapter.port.IProductPort;
import com.junzhecai.domain.order.adapter.repository.IOrderRepository;
import com.junzhecai.domain.order.model.aggregate.CreateOrderAggregate;
import com.junzhecai.domain.order.model.entity.PayOrderEntity;
import com.junzhecai.domain.order.model.valobj.OrderStatusVO;
import com.junzhecai.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
public class OrderService extends AbstractOrderService {

    private final IPayPort payPort;

    public OrderService(IOrderRepository orderRepository, IProductPort productPort, IPayPort payPort) {
        super(orderRepository, productPort);
        this.payPort = payPort;
    }

    @Override
    protected PayOrderEntity doPrepayOrder(String userId, String productId, String productName, String orderId, BigDecimal totalAmount) {
        try {
            String payUrl = payPort.createPayUrl(userId, productId, productName, orderId, totalAmount);
            PayOrderEntity payOrderEntity = PayOrderEntity.builder()
                    .userId(userId)
                    .orderId(orderId)
                    .payUrl(payUrl)
                    .orderStatus(OrderStatusVO.PAY_WAIT)
                    .build();
            orderRepository.updateOrderPayInfo(payOrderEntity);
            return payOrderEntity;
        } catch (Exception e) {
            throw new AppException("创建支付订单失败", e);
        }
    }

    @Override
    protected void doSaveOrder(CreateOrderAggregate orderAggregate) {
        orderRepository.doSaveOrder(orderAggregate);
    }

    @Override
    public void changeOrderPaySuccess(String orderId) {
        orderRepository.changeOrderPaySuccess(orderId);
    }

    @Override
    public List<String> queryNoPayNotifyOrder() {
        return orderRepository.queryNoPayNotifyOrder();
    }

    @Override
    public List<String> queryTimeoutCloseOrderList() {
        return orderRepository.queryTimeoutCloseOrderList();
    }

    @Override
    public boolean changeOrderClose(String orderId) {
        return orderRepository.changeOrderClose(orderId);
    }
}
