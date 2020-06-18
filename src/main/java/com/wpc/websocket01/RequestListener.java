package com.wpc.websocket01;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;

/**
 * 配置监听器，将所有request请求都携带上httpSession
 * 用于webSocket取Session
 *
 */
@WebListener
public class RequestListener implements ServletRequestListener {
    @Override
    public void requestInitialized(ServletRequestEvent sre)  {
        //将所有request请求都携带上httpSession
        ((HttpServletRequest) sre.getServletRequest()).getSession();

    }
    public RequestListener() {}

    @Override
    public void requestDestroyed(ServletRequestEvent arg0)  {}
}
