package com.nealma.netty.bio;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * BIO Server
 * 服务端 利用 多线程池（Thread Pool） 复用线程资源，降低资源消耗。
 * 如果为每一个客户端请求创建一个线程，当大量用户用来，后果不敢想象。
 *
 * 在实际应用中，socket发送的数据并不是按照一行一行发送的，比如我们常见的报文，那么我们就不能要求每发送一次数据，
 * 都在增加一个“\n”标识，这是及其不专业的，
 * 在实际应用中，通过是采用"数据长度+类型+数据"的方式，在我们常接触的热Redis就是采用这种方式。
 *
 * @author neal.ma
 * @date 2020/10/6
 * @blog nealma.com
 */

public class BioServer4 {
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
                         final DataInputStream reader = new DataInputStream(socket.getInputStream());

                         // 有客户前来上门吃饭（读取输入数据流并存储到缓冲区）
                         System.out.println(getThreadInfo() + " 有客户端连接，读取输入数据流");
                         byte type = reader.readByte();
                         int len = reader.readInt();
                         byte[] data = new byte[len - 5];
                         reader.readFully(data);
                         String message = new String(data);

                         System.out.println(getThreadInfo() + " 获取的数据类型: " + type);
                         System.out.println(getThreadInfo() + " 获取的数据长度: " + len);
                         System.out.println(getThreadInfo() + " 获取的数据内容: " + message);

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
