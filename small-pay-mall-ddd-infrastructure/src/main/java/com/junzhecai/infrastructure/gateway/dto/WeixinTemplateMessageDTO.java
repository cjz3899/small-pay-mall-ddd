package com.junzhecai.infrastructure.gateway.dto;

import lombok.*;
import org.springframework.beans.factory.annotation.Value;

import java.io.Serial;
import java.util.HashMap;
import java.util.Map;

@Data
public class WeixinTemplateMessageDTO {

    private String touser = "or0Ab6ivwmypESVp_bYuk92T6SvU";
    @Value("${weixin.config.template_id}")
    private String template_id;
    private String url = "https://weixin.qq.com";
    private Map<String, Map<String, String>> data = new HashMap<>();

    public WeixinTemplateMessageDTO(String touser, String template_id) {
        this.touser = touser;
        this.template_id = template_id;
    }

    public void put(TemplateKey key, String value) {
        data.put(key.getCode(), new HashMap<String, String>() {
            @Serial
            private static final long serialVersionUID = 7092338402387318563L;

            {
                put("value", value);
            }
        });
    }

    public static void put(Map<String, Map<String, String>> data, TemplateKey key, String value) {
        data.put(key.getCode(), new HashMap<String, String>() {
            @Serial
            private static final long serialVersionUID = 7092338402387318563L;

            {
                put("value", value);
            }
        });
    }



    @Getter
    public enum TemplateKey {
        USER("user","用户ID")
        ;

        private String code;
        private String desc;

        TemplateKey(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }

    }


}
