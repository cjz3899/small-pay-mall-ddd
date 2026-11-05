package com.junzhecai.trigger.http;

import com.alipay.api.internal.util.AlipaySignature;
import com.junzhecai.api.IPayService;
import com.junzhecai.api.dto.CreatePayRequestDTO;
import com.junzhecai.api.response.Response;
import com.junzhecai.domain.order.model.entity.PayOrderEntity;
import com.junzhecai.domain.order.model.entity.ShopCartEntity;
import com.junzhecai.domain.order.service.IOrderService;
import com.junzhecai.types.enums.ResponseCode;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/alipay/")
public class AlipayController implements IPayService {

    @Value("${alipay.config.alipay_public_key}")
    private String alipayPublicKey;
    @Resource
    private IOrderService orderService;

    @PostMapping("create_pay_order")
    public Response<String> createPayOrder(@RequestBody CreatePayRequestDTO createPayRequestDTO) {
        try {
            log.info("商品下单，根据商品ID创建支付单开始 userId:{} productId:{}", createPayRequestDTO.getUserId(), createPayRequestDTO.getUserId());
            String userId = createPayRequestDTO.getUserId();
            String productId = createPayRequestDTO.getProductId();
            // 下单
            PayOrderEntity payOrderEntity = orderService.createOrder(ShopCartEntity.builder()
                    .userId(userId)
                    .productId(productId)
                    .build());

            log.info("商品下单，根据商品ID创建支付单完成 userId:{} productId:{} orderId:{}", userId, productId, payOrderEntity.getOrderId());
            return Response.<String>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(payOrderEntity.getPayUrl())
                    .build();
        } catch (Exception e) {
            log.error("商品下单，根据商品ID创建支付单失败 userId:{} productId:{}", createPayRequestDTO.getUserId(), createPayRequestDTO.getUserId(), e);
            return Response.<String>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @PostMapping({"pay_notify", "pay_notify_url", "alipay_notify_url"})
    public String payNotify(HttpServletRequest request) {
        try {
            String tradeStatus = request.getParameter("trade_status");
            log.info("支付回调，消息接收 {}", tradeStatus);
            if (!"TRADE_SUCCESS".equals(tradeStatus)) {
                return "success";
            }

            Map<String, String> params = new HashMap<>();
            Map<String, String[]> requestParams = request.getParameterMap();
            for (String name : requestParams.keySet()) {
                params.put(name, request.getParameter(name));
            }

            String tradeNo = params.get("out_trade_no");

            String sign = params.get("sign");
            String content = AlipaySignature.getSignCheckContentV1(params);
            boolean checkSignature = AlipaySignature.rsa256CheckContent(content, sign, alipayPublicKey, "UTF-8"); // 验证签名
            // 支付宝验签
            if (!checkSignature) {
                log.warn("支付回调，验签失败 outTradeNo: {}", tradeNo);
                return "failure";
            }

            // 验签通过
            log.info("支付回调，交易名称: {}", params.get("subject"));
            log.info("支付回调，交易状态: {}", params.get("trade_status"));
            log.info("支付回调，支付宝交易凭证号: {}", params.get("trade_no"));
            log.info("支付回调，商户订单号: {}", params.get("out_trade_no"));
            log.info("支付回调，交易金额: {}", params.get("total_amount"));
            log.info("支付回调，买家在支付宝唯一id: {}", params.get("buyer_id"));
            log.info("支付回调，买家付款时间: {}", params.get("gmt_payment"));
            log.info("支付回调，买家付款金额: {}", params.get("buyer_pay_amount"));
            log.info("支付回调，支付回调，更新订单 {}", tradeNo);
            // 更新订单未已支付
            orderService.changeOrderPaySuccess(tradeNo);
            return "success";
        } catch (Exception e) {
            log.error("支付回调，处理失败", e);
            return "failure";
        }
    }
}
