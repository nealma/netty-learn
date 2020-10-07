package com.nealma.netty.bio;

import java.io.*;
import java.net.Socket;

/**
 * BIO Client
 * 客户端 利用 while(ture) 实现连续通信
 * @author neal.ma
 * @date 2020/10/6
 * @blog nealma.com
 */
public class BioClient1 {
    public static void main(String[] args) {
        final String host = "127.0.0.1";
        final int port = 8000;
        try {
            Socket socket = new Socket(host, port);
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
            String message;
            while (null != (message = reader.readLine()) && !message.isEmpty()) {
                writer.write(message);
                writer.write("\n");
                writer.flush();
            }

            // 关闭 socket
            socket.shutdownInput();
            socket.close();
            System.out.println("socket shutdown");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
