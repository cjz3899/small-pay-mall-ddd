package com.junzhecai.infrastructure.dao;

import com.junzhecai.infrastructure.dao.po.PayOrderPO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IOrderDao {
    void insert(PayOrderPO payOrder);

    PayOrderPO queryUnpayOrder(PayOrderPO payOrder);

    void updateOrderPayInfo(PayOrderPO payOrder);

    void changeOrderPaySuccess(PayOrderPO payOrder);

    boolean changeOrderPayClose(String orderId);

    List<String> queryNoPayNotifyOrder();

    List<String> queryTimeoutCloseOrderList();


}
