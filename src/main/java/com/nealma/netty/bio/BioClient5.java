package com.nealma.netty.bio;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/**
 * BIO Client
 * 接收服务器端返回的数据
 *
 * @author neal.ma
 * @date 2020/10/6
 * @blog nealma.com
 */
public class BioClient5 {
    public static void main(String[] args) {
        final String host = "127.0.0.1";
        final int port = 8000;
        try {
            Socket socket = new Socket(host, port);
            Scanner reader = new Scanner(System.in);
            DataOutputStream writer = new DataOutputStream(socket.getOutputStream());

            // 结束通信
            boolean stop = false;

            while (reader.hasNextLine() && !stop) {
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
                // 为了确保数据完全发送，通过调用 flush() 方法刷新缓冲区
                writer.flush();
                if ("stop".equals(message)) {
                    stop = true;
                }
            }

            // 关闭 socket
            socket.shutdownOutput();

            final DataInputStream readerOfServer = new DataInputStream(socket.getInputStream());

            // 有客户前来上门吃饭（读取输入数据流并存储到缓冲区）
            System.out.println(getThreadInfo() + " 有客户端连接，读取输入数据流");
            byte type = readerOfServer.readByte();
            int len = readerOfServer.readInt();
            byte[] data = new byte[len - 5];
            readerOfServer.readFully(data);
            String message = new String(data, "UTF-8");

            System.out.println(getThreadInfo() + " 获取服务端的数据类型: " + type);
            System.out.println(getThreadInfo() + " 获取服务端的数据长度: " + len);
            System.out.println(getThreadInfo() + " 获取服务端的数据内容: " + message);

            socket.close();
            System.out.println("socket close");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getThreadInfo() {
        return "Id = " + Thread.currentThread().getId() + ", Name = " + Thread.currentThread().getName();
    }
}
