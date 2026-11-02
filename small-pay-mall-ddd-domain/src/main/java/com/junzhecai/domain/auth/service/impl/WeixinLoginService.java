package com.junzhecai.domain.auth.service.impl;

import com.google.common.cache.Cache;
import com.junzhecai.domain.auth.adapter.port.ILoginPort;
import com.junzhecai.domain.auth.service.ILoginService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class WeixinLoginService implements ILoginService {
    @Resource
    private ILoginPort loginPort;
    @Resource
    private Cache<String, String> openidToken;

    @Override
    public String createQrCodeTicket() throws Exception {
        return loginPort.createQrCodeTicket();
    }

    @Override
    public String checkLogin(String ticket) {
        return openidToken.getIfPresent(ticket);
    }

    @Override
    public void saveLoginState(String ticket, String openid) throws IOException {
        // 保存登录信息
        openidToken.put(ticket, openid);
        // 发送模板消息
        loginPort.sendLoginTemplate(openid);
    }


}
