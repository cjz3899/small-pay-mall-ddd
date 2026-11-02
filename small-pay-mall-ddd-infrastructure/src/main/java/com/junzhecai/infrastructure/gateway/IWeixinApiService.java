package com.junzhecai.infrastructure.gateway;

import com.junzhecai.infrastructure.gateway.dto.WeixinQrCodeRequestDTO;
import com.junzhecai.infrastructure.gateway.dto.WeixinQrCodeResponseDTO;
import com.junzhecai.infrastructure.gateway.dto.WeixinTemplateMessageDTO;
import com.junzhecai.infrastructure.gateway.dto.WeixinTokenResponseDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface IWeixinApiService {
    @GET("cgi-bin/token")
    Call<WeixinTokenResponseDTO> getAccessToken(@Query("appid") String appId,
                                                @Query("secret") String appSecret,
                                                @Query("grant_type") String grantType);

    @POST("cgi-bin/qrcode/create")
    Call<WeixinQrCodeResponseDTO> createQrCode(@Query("access_token") String accessToken, @Body WeixinQrCodeRequestDTO weixinQrCodeReq);

    @POST("cgi-bin/message/template/send")
    Call<Void> sendMessage(@Query("access_token") String accessToken, @Body WeixinTemplateMessageDTO weixinTemplateMessageDTO);


}
 