package com.wpc.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Getter
@Setter
@ToString
public class MessageToClient {
    //聊天的内容
    private String content;
    // 发送人
    private String from;
    //服务端所有登录的用户列表，前一个String是SessionID（QQ号），后一个为用户。
    private Set<String> names;

}
