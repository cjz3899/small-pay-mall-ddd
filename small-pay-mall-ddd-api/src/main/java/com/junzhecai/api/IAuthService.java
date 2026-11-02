package com.junzhecai.api;

import com.junzhecai.api.response.Response;

public interface IAuthService {
    Response<String> weixinQrCodeTicket() throws Exception;

    Response<String> checkLogin(String ticket);
}
