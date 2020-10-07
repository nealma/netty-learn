package com.nealma.netty.bio;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * BIO Client
 * 接收服务器端返回的数据
 *
 * @author neal.ma
 * @date 2020/10/6
 * @blog nealma.com
 */
public class BioClient5 {
    public static void main(String[] args) throws Exception {
        final String host = "127.0.0.1";
        final int port = 8000;
        Socket socket = new Socket(host, port);

        // 控制台标准输入
        Scanner reader = new Scanner(System.in);

        // 虽然可以连续通信，但是不能关闭 socket，需要加入控制字符串 quit
        String message = null;
        while (reader.hasNextLine()) {
            message = reader.nextLine();
            if ("quit".equals(message)) {
                System.out.println(getThreadInfo() + " ###### 结束通信 ヾ(ToT)Bye~Bye~ ###### ");
                socket.shutdownOutput();
                socket.shutdownInput();
                socket.close();
                break;
            }
            // 发送数据
            send(socket, message);

            TimeUnit.SECONDS.sleep(2L);

            // 接收数据
            receive(socket);
        }
        // 关闭连接
        socket.close();
    }

    public static String getThreadInfo() {
        return "[Id=" + Thread.currentThread().getId() + ", Name=" + Thread.currentThread().getName() + "]";
    }

    public static void send(Socket socket,String message) {
        DataOutputStream dataOutputStream = null;
        try {
            System.out.println(getThreadInfo() + " ###### 发送数据 ###### ");
            System.out.println(getThreadInfo() + " >>> " + message);
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            byte[] data = message.getBytes();
            byte type = 1;
            int len = data.length + 5;
            // 设置数据类型
            dataOutputStream.writeByte(type);
            // 设置数据长度
            dataOutputStream.writeInt(len);
            // 设置数据内容
            dataOutputStream.write(data);
            // 为了确保数据完全发送，通过调用 flush() 方法刷新缓冲区
            dataOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void receive(Socket socket) {
        DataInputStream dataInputStream = null;
        try {
            dataInputStream = new DataInputStream(socket.getInputStream());
            byte type = dataInputStream.readByte();
            int len = dataInputStream.readInt();
            byte[] data = new byte[len - 5];
            dataInputStream.readFully(data);

            String message = new String(data);
            System.out.println(getThreadInfo() + " ###### 收到数据 ###### ");
            System.out.println(getThreadInfo() + " <<< 数据 类型: " + type + "，长度: " + len + "，内容: " + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
