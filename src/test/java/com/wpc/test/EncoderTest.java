package com.wpc.test;

import sun.misc.BASE64Encoder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class EncoderTest {

    public static void main(String[] args) throws Exception {
        String magic = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
        String key = "xCo3hMSJjPTGIjGBg1YyKg==";
        key += magic;
        //通过SHA-1算法进行更新
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(key.getBytes(StandardCharsets.UTF_8), 0, key.length());
        //进行Base64加密
        BASE64Encoder encoder = new BASE64Encoder();
        String accept = encoder.encode(md.digest());
        // Yb9MoQBDb87R/PTmeju/u/DwGEo=
        System.out.println(accept);
    }

}
