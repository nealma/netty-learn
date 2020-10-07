package com.nealma.netty.bio;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * BIO Client
 * 客户端 发送一次消息必须重启一次服务器
 * @author neal.ma
 * @date 2020/10/6
 * @blog nealma.com
 */
public class BioClient {
    public static void main(String[] args) {
        final String host = "127.0.0.1";
        final int port = 8000;
        try {
            Socket socket = new Socket(host, port);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
            String message = "我是客户端 9527.";
            writer.write(message);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
