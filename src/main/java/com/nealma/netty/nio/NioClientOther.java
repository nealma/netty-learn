package com.nealma.netty.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Nio Client
 *
 * @author neal.ma
 * @date 2020/10/8
 * @blog nealma.com
 */
public class NioClientOther {

    public static final String HOST = "127.0.0.1";
    public static final int PORT = 8000;

    public static void main(String[] args) throws IOException {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Runnable runnable = () -> {
            try {
                SocketChannel channel = connect();
                while (true) {
                    send(channel);
                    receive(channel);
                    TimeUnit.SECONDS.sleep(5);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            }
        };
        executorService.submit(runnable);
    }

    public static SocketChannel connect() throws IOException {
        // 获取通道
        SocketChannel channel = SocketChannel.open();
        // 设置为非阻塞模式
        channel.configureBlocking(false);
        // 尝试与目标主机建立 TCP 连接
        channel.connect(new InetSocketAddress(HOST, PORT));
        while (true) {
            // 如果通道完成连接，退出循环
            if (channel.finishConnect()) {
                break;
            }
        }
        return channel;
    }

    public static void send(SocketChannel channel) throws IOException {
        String message = channel.socket().getInetAddress().getHostAddress() +
                " [other client send] at " +
                LocalDateTime.now().toString();
        channel.write(ByteBuffer.wrap(message.getBytes()));

        log(message);
    }

    public static void receive(SocketChannel channel) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        channel.read(byteBuffer);
        byteBuffer.flip();
        log(
                "[receive] " +
                Charset.defaultCharset().newDecoder().decode(byteBuffer).toString()
        );
    }

    public static void log(String message) {
        System.out.println(message);
    }
}
