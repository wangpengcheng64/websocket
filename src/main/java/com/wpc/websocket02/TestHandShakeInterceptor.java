package com.wpc.websocket02;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

public class TestHandShakeInterceptor extends HttpSessionHandshakeInterceptor {

    private final Logger LOGGER = LoggerFactory.getLogger(TestHandShakeInterceptor.class);

    /*
     * 在WebSocket连接建立之前的操作，以鉴权为例
     * 用于建立连接前的握手拦截处理，如拦截非法连接，保存连接客户端信息等。
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
            WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        LOGGER.info("Handle before webSocket connected. ");
        // 获取url传递的参数，通过attributes在Interceptor处理结束后传递给WebSocketHandler
        // WebSocketHandler可以通过WebSocketSession的getAttributes()方法获取参数
        ServletServerHttpRequest serverRequest = (ServletServerHttpRequest) request;
        String username = serverRequest.getServletRequest().getParameter("username");

        if (!StringUtils.isEmpty(username)) {
            LOGGER.info("Validation passed. WebSocket connecting.... ");
            attributes.put("username", username);
            return super.beforeHandshake(request, response, wsHandler, attributes);
        } else {
            LOGGER.error("Validation failed. WebSocket will not connect. ");
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
            WebSocketHandler wsHandler, Exception ex) {
        //握手后的操作
        super.afterHandshake(request, response, wsHandler, ex);
    }

}
