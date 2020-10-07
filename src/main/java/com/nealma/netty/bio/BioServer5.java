package com.nealma.netty.bio;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * BIO Server
 * 服务器端返回数据
 *
 * @author neal.ma
 * @date 2020/10/6
 * @blog nealma.com
 */

public class BioServer5 {
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

                         // 关闭输入流
                         socket.shutdownInput();

                         // 返回消息给客户端
                         final DataOutputStream writer = new DataOutputStream(socket.getOutputStream());
                         byte[] serverOfData = "服务器数据".getBytes("UTF-8");
                         writer.writeByte(1);
                         writer.writeInt(serverOfData.length + 5);
                         writer.write(serverOfData);
                         writer.flush();

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
