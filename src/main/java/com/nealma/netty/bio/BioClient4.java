package com.nealma.netty.bio;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * BIO Client
 * 客户端 利用 while(ture) 实现连续通信
 * @author neal.ma
 * @date 2020/10/6
 * @blog nealma.com
 */
public class BioClient4 {
    public static void main(String[] args) {
        final String host = "127.0.0.1";
        final int port = 8000;
        try {
            Socket socket = new Socket(host, port);
            Scanner reader = new Scanner(System.in);
            DataOutputStream writer = new DataOutputStream(socket.getOutputStream());

            while (reader.hasNextLine()) {
                String message = reader.nextLine();
                byte[] data = message.getBytes();
                byte type = 1;
                int len = data.length + 5;
                // 设置数据类型
                writer.writeByte(type);
                // 设置数据长度
                writer.writeInt(len);
                // 设置数据内容
                writer.write(data);
                // 为了确保数据完全发送，通过调用flush()方法刷新缓冲区
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
