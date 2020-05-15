package com.wpc.websocket02;

import com.alibaba.fastjson.JSON;
import com.wpc.pojo.MessageFromClient;
import com.wpc.pojo.MessageToClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class MyWebSocketHandler implements WebSocketHandler {
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;
    //创建一个线程安全的map
    private static Map<String, WebSocketSession> users = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
        String username = (String) webSocketSession.getAttributes().get("username");
        users.put(username, webSocketSession);   //加入map中,为了测试方便使用username做key
        addOnlineCount();           //在线数加1
        log.info(username + "加入！当前在线人数为" + getOnlineCount());
        //需要将信息发送给前端，就要用  MessageToClient
        MessageToClient messageToClient = new MessageToClient();
        messageToClient.setContent(username + "上线了！");
        messageToClient.setNames(users.keySet());//将用户列表给所有人更新一下
        //将messageToClient对象变成一个字符串发送给前端
        String jsonStr = JSON.toJSONString(messageToClient);
        this.sendInfo(jsonStr);
    }

    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {
        String message = webSocketMessage.getPayload().toString();
        log.info("来自客户端的消息:" + message);
        //群发消息
        if (StringUtils.isEmpty(message)) {
            return;
        }
        MessageFromClient messageFromClient = JSON.parseObject(message, MessageFromClient.class);
        //将信息发给前端
        MessageToClient messageToClient = new MessageToClient();
        messageToClient.setFrom(messageFromClient.getFrom());
        messageToClient.setContent(messageFromClient.getMsg());//信息
        messageToClient.setNames(users.keySet());//用户列表
        String msg = JSON.toJSONString(messageToClient);
        if ("1".equals(messageFromClient.getType())) {
            sendInfo(msg);//群发消息
        } else {//给特定人员发消息
            String[] split = messageFromClient.getTo().split("-");
            for (String to : split) {
                if (!StringUtils.isEmpty(to)){
                    sendMessageToSomeBody(to, msg);
                }
            }
        }
    }

    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {
        log.error("发生错误 session: " + webSocketSession);
        throwable.printStackTrace();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
        String username = (String) webSocketSession.getAttributes().get("username");
        users.remove(username);  //从set中删除
        subOnlineCount();           //在线数减1
        log.info("一个连接关闭！当前在线人数为" + getOnlineCount());
        // 发送给所有在线用户一个上下线通知
        MessageToClient messageToClient = new MessageToClient();
        messageToClient.setContent(username + "下线了！");
        messageToClient.setNames(users.keySet());
        String jsonStr = JSON.toJSONString(messageToClient);
        this.sendInfo(jsonStr);
    }

    /**
     * 给特定人员发送消息
     *
     * @param username
     * @param message
     * @throws IOException
     */
    private void sendMessageToSomeBody(String username, String message) throws IOException {
        if (users.get(username) == null) {
            return;
        }
        users.get(username).sendMessage(new TextMessage(message));
    }

    /**
     * 群发自定义消息
     */
    private void sendInfo(String message) throws IOException {
        for (WebSocketSession session : users.values()) {
            session.sendMessage(new TextMessage(message));
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    private static synchronized int getOnlineCount() {
        return onlineCount;
    }

    private static synchronized void addOnlineCount() {
        MyWebSocketHandler.onlineCount++;
    }

    private static synchronized void subOnlineCount() {
        MyWebSocketHandler.onlineCount--;
    }

}
