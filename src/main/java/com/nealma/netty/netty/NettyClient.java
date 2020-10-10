package com.nealma.netty.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * Netty CLient
 * 单线程 Reactor 模型
 * @author neal.ma
 * @date 2020/10/9
 * @blog nealma.com
 */
public class NettyClient {
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 8000;
    public static void main(String[] args) throws InterruptedException {

        final NioEventLoopGroup group = new NioEventLoopGroup();

        // 创建启动引导类
        final Bootstrap bootstrap = new Bootstrap();
        // 设置并绑定 Reactor 线程模型
        bootstrap.group(group);
        // 设置并绑定服务端 channel
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.remoteAddress(new InetSocketAddress(HOST, PORT));

        bootstrap.handler(new NettyClientHandler());
        while (true) {
            // 启动客户端
            final ChannelFuture channelFuture = bootstrap.connect().sync();
            // 一直等待，直到连接关闭
            channelFuture.channel().closeFuture().sync();
            TimeUnit.SECONDS.sleep(5);
        }
//        group.shutdownGracefully().sync();

    }
}
