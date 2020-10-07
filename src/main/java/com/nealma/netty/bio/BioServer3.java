package com.nealma.netty.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * BIO Server
 * 服务端 利用 多线程池（Thread Pool） 复用线程资源，降低资源消耗。
 * 如果为每一个客户端请求创建一个线程，当大量用户用来，后果不敢想象。
 *
 * @author neal.ma
 * @date 2020/10/6
 * @blog nealma.com
 */

public class BioServer3 {
    public static void main(String[] args) {
        try {
            // 某商场 8000 窗口（监听 8000 端口）
            int port = 8000;
            // 开一家海底捞，提供服务
            ServerSocket serverSocket = new ServerSocket(port);

            // 创建一个线程池（m:n）
            ExecutorService pool = Executors.newFixedThreadPool(1);

            while(true){
                System.out.println(getThreadInfo() + " 开门营业，等待客户上门就餐");
                final Socket socket = serverSocket.accept();

                // 多线程 同时服务多个客户端
                Runnable runnable = () -> {
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
                };
                // 提交给线程池
                pool.submit(runnable);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getThreadInfo() {
        return "Id = " + Thread.currentThread().getId() + ", Name = " + Thread.currentThread().getName();
    }
}
