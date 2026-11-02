package com.junzhecai.infrastructure.gateway.dto;

import lombok.Data;

@Data
public class WeixinQrCodeResponseDTO {

    private String ticket;
    private Long expire_seconds;
    private String url;

}
