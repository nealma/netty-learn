package com.nealma.netty.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * BIO Server
 * 服务端 利用 while 实现连续通信
 * A 顾客去吃海底捞，就这样干坐着等了一小时，然后才开始吃火锅。(BIO)
 *
 * @author neal.ma
 * @date 2020/10/6
 * @blog nealma.com
 */

public class BioServer1 {
    public static void main(String[] args) {
        try {
            // 某商场 8000 窗口（监听 8000 端口）
            int port = 8000;
            // 开一家海底捞，提供服务
            ServerSocket serverSocket = new ServerSocket(port);

            // 开门营业，等待客户前来就餐（同步、阻塞）
            // 接待上门的客户，客户向服务员订餐（接收数据）
            // 有空桌，直接就做（新建 线程，跟客户端保持连接以及后续的服务）
            // 如果满座，需排号等待叫号（没有可用的 线程）
            System.out.println("开门营业，等待客户上门就餐");
            final Socket socket = serverSocket.accept();

            // 建立好连接后，从 socket 中获取输入流，并建立缓冲区进行读取
            final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // 有客户前来上门吃饭（读取输入数据流并存储到缓冲区）
            System.out.println("有客户端连接，读取输入数据流");
            String line;
            while (null != (line = reader.readLine())) {

                System.out.println("客户消息: " + line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
