package com.nealma.netty.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Netty Server
 * 单线程 Reactor 模型
 * @author neal.ma
 * @date 2020/10/9
 * @blog nealma.com
 */
public class NettyServer {
    private static final  int PORT = 8000;
    public static void main(String[] args) throws InterruptedException {
        final NioEventLoopGroup group = new NioEventLoopGroup();

        // 创建启动引导类
        final ServerBootstrap serverBootstrap = new ServerBootstrap();
        // 设置并绑定 Reactor 线程模型，单线程
        serverBootstrap.group(group);
        // 设置并绑定服务端 channel
        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.localAddress(PORT);

        serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel channel) throws Exception {
                channel.pipeline().addLast(new NettyServerHandler());
            }
        });
        // 启动服务
        final ChannelFuture channelFuture = serverBootstrap.bind().sync();
        // 一直等待，直到连接关闭
        channelFuture.channel().closeFuture().sync();
        group.shutdownGracefully().sync();

    }
}
