package com.junzhecai.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "alipay.config", ignoreInvalidFields = true)
public class AlipayProperties {

    private String appId;

    private String merchantPrivateKey;

    private String alipayPublicKey;

    private String gatewayUrl = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";

    private String signType = "RSA2";

    private String notifyUrl;

    private String returnUrl;

    private Integer connectTimeout = 10000;

    private Integer readTimeout = 30000;
}
