package com.nealma.netty.bio;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
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
    public static void main(String[] args) throws IOException {
        // 某商场 8000 窗口（监听 8000 端口）
        int port = 8000;
        // 开一家海底捞，提供服务
        ServerSocket serverSocket = new ServerSocket(port);

        // 创建一个线程池（m:n）
        ExecutorService pool = Executors.newFixedThreadPool(1);

        while (true) {
            // accept 是阻塞的，直到有客户端连接
            final Socket socket = serverSocket.accept();
            System.out.println(getThreadInfo() + " ###### 有新客户端连接 ###### " + socket.getPort());

            // 多线程 同时服务多个客户端
            Runnable runnable = () -> {
                while (true) {
                    // 当客户端 close 时，存在无限循环的BUG。该方法不可用。（处理 Broker Pipe 异常）
                    System.out.println("isClosed=" + socket.isClosed() +
                            ", isConnected=" + socket.isConnected() +
                            ", isBound=" + socket.isBound() +
                            ", isOutputShutdown=" + socket.isOutputShutdown() +
                            ", isInputShutdown=" + socket.isInputShutdown());
                    if (socket.isClosed() || socket.isOutputShutdown() || socket.isInputShutdown()) {
                        break;
                    }
                    // 接收数据
                    try {
                        receive(socket);
                        // 发送数据
                        send(socket, LocalDateTime.now().toString());
                    } catch (Exception e) {
                        try {
                            socket.shutdownInput();
                            socket.shutdownOutput();
                            socket.close();
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }
                }
            };

            // 提交给线程池
            pool.submit(runnable);
        }
    }

    public static String getThreadInfo() {
        return "[Id=" + Thread.currentThread().getId() + ", Name=" + Thread.currentThread().getName() + "]";
    }

    public static void send(Socket socket,String message) throws IOException {
        System.out.println(getThreadInfo() + " ###### 发送数据 ###### ");
        System.out.println(getThreadInfo() + " >>> " + message);
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
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
    }

    public static void receive(Socket socket) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

        // input.read 是 阻塞的
        byte type = dataInputStream.readByte();
        int len = dataInputStream.readInt();
        byte[] data = new byte[len - 5];
        dataInputStream.readFully(data);

        String message = new String(data);
        System.out.println(getThreadInfo() + " ###### 收到数据 ###### ");
        System.out.println(getThreadInfo() + " <<< 数据 类型: " + type + "，长度: " + len + "，内容: " + message);
    }
}
