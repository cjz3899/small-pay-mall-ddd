package com.junzhecai.trigger.job;

import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.junzhecai.domain.order.service.IOrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class NoPayNotifyOrderJob {

    @Resource
    private IOrderService orderService;
    @Resource
    private AlipayClient alipayClient;

    @Scheduled(cron = "0/3 * * * * ?")
    public void exec() {
        try {
            List<String> orderIds = orderService.queryNoPayNotifyOrder();
            if (null == orderIds || orderIds.isEmpty()) return;

            for (String orderId : orderIds) {
                AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
                AlipayTradeQueryModel bizModel = new AlipayTradeQueryModel();
                bizModel.setOutTradeNo(orderId);
                request.setBizModel(bizModel);


                AlipayTradeQueryResponse alipayTradeQueryResponse = alipayClient.execute(request);
                // 交易不存在属于未支付订单的正常状态，直接跳过
                if (!alipayTradeQueryResponse.isSuccess()) {
                    continue;
                }
                // 查询成功不代表已支付，需确认交易状态
                String tradeStatus = alipayTradeQueryResponse.getTradeStatus();
                if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
                    log.info("定时任务，检测到已支付未回调订单 orderId: {} tradeStatus: {}", orderId, tradeStatus);
                    orderService.changeOrderPaySuccess(orderId);
                }

            }
        } catch (Exception e) {
            log.error("检测未接收到或未正确处理的支付回调通知失败", e);
        }
    }

}

