package com.junzhecai.trigger.job;

import com.junzhecai.domain.order.service.IOrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class TimeoutCloseOrderJob {

    @Resource
    private IOrderService orderService;

    @Scheduled(cron = "0 0/10 * * * ?")
    public void exec() {
        try {
            List<String> orderIds = orderService.queryTimeoutCloseOrderList();
            if (null == orderIds || orderIds.isEmpty()) {
                log.info("定时任务，超时30分钟订单关闭，暂无超时未支付订单 orderIds is null");
                return;
            }
            for (String orderId : orderIds) {
                boolean status = orderService.changeOrderClose(orderId);
                log.info("定时任务，超时30分钟订单关闭 orderId: {} status：{}", orderId, status);
            }
        } catch (Exception e) {
            log.error("定时任务，超时15分钟订单关闭失败", e);
        }
    }

}

