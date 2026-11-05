package com.junzhecai.domain.auth.adapter.port;

import java.math.BigDecimal;

public interface IPayPort {

    String createPayUrl(String userId, String productId, String productName,
                        String orderId, BigDecimal totalAmount) throws Exception;
}
