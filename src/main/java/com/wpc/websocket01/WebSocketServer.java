package com.wpc.websocket01;

import com.alibaba.fastjson.JSON;
import com.wpc.pojo.MessageFromClient;
import com.wpc.pojo.MessageToClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpSession;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@ServerEndpoint(value = "/chat/{userId}", configurator = WebSocketServerConfigurator.class)
@Component
public class WebSocketServer {
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;
    //创建一个线程安全的map
    private static Map<String, WebSocketServer> users = new ConcurrentHashMap<>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;
    //放入map中的key,用来表示该连接对象
    private String username;

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, EndpointConfig config, @PathParam("userId") String username) {
        // 获取HttpSession
        HttpSession httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        // 从HttpSession中取得当前登录的用户作为当前连接的用户
        Object loginUser = httpSession.getAttribute("LoginUser");
        this.session = session;
        this.username = username;
        users.put(username, this);   //加入map中,为了测试方便使用username做key
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

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        users.remove(this.username);  //从set中删除
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
     * 收到客户端消息后触发的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        //群发消息
        try {
            if (StringUtils.isEmpty(message)) {
                return;
            }
            if ("ping".equals(message)) {
                session.getBasicRemote().sendText("pong");
                return;
            }
            log.info("来自客户端的消息:" + message);
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
                    if (!StringUtils.isEmpty(to)) {
                        sendMessageToSomeBody(to, msg);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误 session: " + session);
        error.printStackTrace();
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
        users.get(username).session.getBasicRemote().sendText(message);
//        this.session.getBasicRemote().sendText(this.username + "@" + username + ": " + message);
//        this.session.getBasicRemote().sendText(message);
    }

    /**
     * 群发自定义消息
     */
    private void sendInfo(String message) {
        for (WebSocketServer item : users.values()) {
            try {
                item.session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static synchronized int getOnlineCount() {
        return onlineCount;
    }

    private static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    private static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }
}
