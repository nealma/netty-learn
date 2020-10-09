package com.nealma.netty.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * NIO Server
 *
 * @author neal.ma
 * @date 2020/10/7
 * @blog nealma.com
 */
public class NioServer {
    private static final int PORT = 8000;
    private static AtomicInteger count = new AtomicInteger();

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Runnable runnable = () -> {
            try {
                final Selector selector = start();
                while (true) {
                    count.getAndIncrement();
                    // 等待 "就绪" 事件，此处会阻塞；如果不希望阻塞，可以指定 timeout 值,例如: select(1)
                    final int select = selector.select();
                    if (select == 0) {
//                        TimeUnit.SECONDS.sleep(5);
//                        log(">>>>>> 没有'准备就绪'的事件，继续轮询");
                        continue;
                    }
                    // 获取"准备就绪"的事件
                    final Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    final Iterator<SelectionKey> selectionKeyIterator = selectionKeys.iterator();
                    while (selectionKeyIterator.hasNext()) {
                        final SelectionKey selectionKey = selectionKeyIterator.next();
                        log("selectionKey: " + selectionKey.toString());
                        // 删除服务器端的 acceptable 的 selectionKey, 否则会出BUG
                        selectionKeyIterator.remove();
                        // 判断 key 的具体事件
                        if (selectionKey.isAcceptable()) {
                            accept(selectionKey);
                        }
                        if (selectionKey.isReadable()) {
                            read(selectionKey);
                        }
                        if (selectionKey.isWritable()) {
                            write(selectionKey);
                        }
                        if (selectionKey.isConnectable()) {
                            log(">>> 连接就绪");
                        }
                    }
                }
            } catch (Exception ignored) {
                log(ignored.getMessage());
            }
        };

        executorService.submit(runnable);
    }

    public static Selector start() throws IOException {
        Selector selector = Selector.open();
        //1 等待客户端连接
        // 创建一个 TCP 安全套接字通道
        final ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(PORT));
        // 设置通道为 非阻塞
        serverSocketChannel.configureBlocking(false);
        // 想向选择器注册通道
        final SelectionKey selectionKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        return selectionKey.selector();
    }

    public static void accept(SelectionKey selectionKey) throws IOException {
        // 有新客户端连接
        log(">>>>>> 有新客户端连接");
        // 处理"接收就绪"事件，获取客户端连接
        log("is acceptable");
        log(selectionKey.toString() + " removed.");
        ServerSocketChannel serverChannel = (ServerSocketChannel) selectionKey.channel();
        final SocketChannel socketChannel = serverChannel.accept();
        // 设置通道为 非阻塞 模式
        socketChannel.configureBlocking(false);
        // 向选择器注册通道,即将 channel 加入到 selector，并指定为"可读"模式
        socketChannel.register(selectionKey.selector(), SelectionKey.OP_READ);
        log("accept done.");
    }

    public static void write(SelectionKey selectionKey) throws Exception {
        // 获取选择器上的"写就绪"的通道
        log(">>> 写就绪 向客户端发送数据");
        final SocketChannel channel = (SocketChannel) selectionKey.channel();
        // 写数据
        String message = "[" + selectionKey.toString() + "] I am Server at " + LocalDateTime.now().toString();
        final ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.put(message.getBytes());
        channel.write(byteBuffer);
        byteBuffer.compact();
        log("write done.");
        selectionKey.interestOps(selectionKey.interestOps() | SelectionKey.OP_READ);
    }

    public static void read(SelectionKey selectionKey) throws Exception {
        // 获取选择器上的"读就绪"的通道
        log(">>> 读就绪 读取客户端数据");
        final SocketChannel channel = (SocketChannel) selectionKey.channel();
        // 读取数据
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        channel.read(byteBuffer);
        byteBuffer.flip();
        log(
                Charset.defaultCharset().newDecoder().decode(byteBuffer).toString()
        );
        log("read done.");
        selectionKey.interestOps(selectionKey.interestOps() | SelectionKey.OP_WRITE);
    }

    public static void log(String message) {
        System.out.println( getThreadInfo() + message + " count: " + count);
    }

    public static String getThreadInfo() {
        return "[Id=" + Thread.currentThread().getId() + ", Name=" + Thread.currentThread().getName() + "]";
    }
}
