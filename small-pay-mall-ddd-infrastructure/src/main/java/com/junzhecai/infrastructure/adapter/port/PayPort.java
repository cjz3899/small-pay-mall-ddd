package com.junzhecai.infrastructure.adapter.port;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.junzhecai.domain.auth.adapter.port.IPayPort;
import com.junzhecai.infrastructure.config.AlipayProperties;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PayPort implements IPayPort {

    private final AlipayClient alipayClient;
    private final AlipayProperties alipayProperties;

    public PayPort(AlipayClient alipayClient, AlipayProperties alipayProperties) {
        this.alipayClient = alipayClient;
        this.alipayProperties = alipayProperties;
    }

    @Override
    public String createPayUrl(String userId, String productId, String productName, String orderId, BigDecimal totalAmount) throws Exception {
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(alipayProperties.getNotifyUrl());
        request.setReturnUrl(alipayProperties.getReturnUrl());

        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", orderId);
        bizContent.put("total_amount", totalAmount);
        bizContent.put("subject", productName);
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
        request.setBizContent(bizContent.toJSONString());

        AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
        return response.getBody();
    }
}
