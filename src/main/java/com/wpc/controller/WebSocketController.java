package com.wpc.controller;

import com.wpc.pojo.UserInfoCache;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Sky
 */
@Controller
public class WebSocketController {

    @RequestMapping("/page/{page}")
    public String page(@PathVariable String page){
        if (StringUtils.isEmpty(page)){
            page = "index";
        }
        return page;
    }

    @PostMapping("/login")
    @ResponseBody
    public String login(String username, String password) {
//        String pwd =  UserInfoCache.get(username);
//        if (StringUtils.isEmpty(pwd) || !pwd.equals(password)){
//            return "";
//        }
        UserInfoCache.put(username, password);
        return username;
    }

    @PostMapping("/register")
    public String register(String username, String password) {
        UserInfoCache.put(username, password);
        return "index";
    }

}
