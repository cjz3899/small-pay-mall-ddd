package com.junzhecai.infrastructure.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AlipayProperties.class)
public class AlipayClientConfig {

    @Bean
    public AlipayClient alipayClient(AlipayProperties properties) {
        return DefaultAlipayClient.builder(
                        properties.getGatewayUrl(),
                        properties.getAppId(),
                        properties.getMerchantPrivateKey())
                .format("json")
                .charset("UTF-8")
                .signType(properties.getSignType())
                .alipayPublicKey(properties.getAlipayPublicKey())
                .connectTimeout(properties.getConnectTimeout())
                .readTimeout(properties.getReadTimeout())
                .readTimeout(properties.getReadTimeout())
                .build();


    }
}
