package com.wpc.pojo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserInfoCache {

    /**
     * 用户信息缓存
     */
    private static Map<String, String> userCache = new ConcurrentHashMap<>();

    public static void put(String key, String value){
        userCache.put(key, value);
    }

    public static String get(String key){
        return userCache.get(key);
    }

}
