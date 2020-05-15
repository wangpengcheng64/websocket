package com.wpc.websocket02;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;


//@Configuration
//@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(myWebSocketHandler(), "/chat") //注册 handler,这里的 url 要与页面的 url 一致
                .setAllowedOrigins("*") // 允许请求的域名
                .addInterceptors(new TestHandShakeInterceptor());  //添加握手拦截
        //旧版浏览器可能不支持 websocket，通过 sockjs 模拟 websocket 的行为，所以下面要配 sockjs 支持。
        registry.addHandler(myWebSocketHandler(), "/sockjs").setAllowedOrigins("*")
                .addInterceptors(new TestHandShakeInterceptor()).withSockJS();
    }

    @Bean
    public MyWebSocketHandler myWebSocketHandler() {
        return new MyWebSocketHandler();
    }

}
