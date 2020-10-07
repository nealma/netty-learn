package com.nealma.netty.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * BIO Server
 * 服务端 利用多线程实现同时处理多个客户端
 * A 顾客去吃海底捞，就这样干坐着等了一小时，然后才开始吃火锅。(BIO)
 *
 * @author neal.ma
 * @date 2020/10/6
 * @blog nealma.com
 */

public class BioServer2 {
    public static void main(String[] args) {
        try {
            // 某商场 8000 窗口（监听 8000 端口）
            int port = 8000;
            // 开一家海底捞，提供服务
            ServerSocket serverSocket = new ServerSocket(port);

            while(true){
                System.out.println(getThreadInfo() + " 开门营业，等待客户上门就餐");
                final Socket socket = serverSocket.accept();
                // 多线程 同时服务多个客户端
                new Thread(() -> {
                    try {
                        // 建立好连接后，从 socket 中获取输入流，并建立缓冲区进行读取
                        final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                        // 有客户前来上门吃饭（读取输入数据流并存储到缓冲区）
                        System.out.println(getThreadInfo() + " 有客户端连接，读取输入数据流");
                        String line;
                        // 利用 while 连续服务客户端，readXxx 为阻塞方法，等待输入
                        while (null != (line = reader.readLine())) {
                            System.out.println(getThreadInfo() + " 客户消息: " + line);
                        }

                        // 关闭 socket
                        socket.shutdownInput();
                        socket.close();
                        System.out.println(getThreadInfo() + " socket shutdown");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getThreadInfo() {
        return "Id = " + Thread.currentThread().getId() + ", Name = " + Thread.currentThread().getName();
    }
}
