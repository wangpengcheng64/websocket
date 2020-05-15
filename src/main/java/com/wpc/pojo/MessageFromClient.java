package com.wpc.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MessageFromClient {
    // 具体的聊天信息
    private String msg;

    // 聊天类别: 1表示群聊 2表示私聊
    private String type;

    // 发送方
    private String from;

    // 私聊的对象username
    private String to;
}

