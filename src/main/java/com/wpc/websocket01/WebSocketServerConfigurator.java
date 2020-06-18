package com.wpc.websocket01;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

/**
 * 由于websocket的协议与Http协议是不同的，所以造成了无法直接拿到session
 * 但是问题总是要解决的，不然这个websocket协议所用的场景也就没了
 * 重写modifyHandshake，HandshakeRequest request可以获取httpSession
 */
public class WebSocketServerConfigurator extends ServerEndpointConfig.Configurator {
    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest req, HandshakeResponse resp) {
        HttpSession httpSession = (HttpSession) req.getHttpSession();
        sec.getUserProperties().put(HttpSession.class.getName(), httpSession);
    }
}
