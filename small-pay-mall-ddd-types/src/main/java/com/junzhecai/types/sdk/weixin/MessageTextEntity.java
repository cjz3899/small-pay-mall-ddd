package com.junzhecai.types.sdk.weixin;

import lombok.Data;

@Data
public class MessageTextEntity {

    private String toUserName;

    private String fromUserName;

    private String createTime;

    private String msgType;

    private String event;

    private String eventKey;

    private String msgId;

    private String msgID;

    private String status;

    private String ticket;

    private String content;

}
