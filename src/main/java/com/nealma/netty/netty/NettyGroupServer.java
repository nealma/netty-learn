package com.nealma.netty.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Netty Group Server
 * 多线程 Reactor 模型
 * @author neal.ma
 * @date 2020/10/9
 * @blog nealma.com
 */
public class NettyGroupServer {
    private static final  int PORT = 8000;
    public static void main(String[] args) throws InterruptedException {
        final EventLoopGroup bossGroup = new NioEventLoopGroup();
        final EventLoopGroup workerGroup = new NioEventLoopGroup();

        // 创建启动引导类
        final ServerBootstrap serverBootstrap = new ServerBootstrap();
        // 设置并绑定 Reactor 线程模型，多线程
        serverBootstrap.group(bossGroup, workerGroup);
        // 设置并绑定服务端 channel
        serverBootstrap.channel(NioServerSocketChannel.class);

        serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel channel) throws Exception {
                channel.pipeline().addLast(new NettyServerHandler());
            }
        });
        // 启动服务
        final ChannelFuture channelFuture = serverBootstrap.bind(PORT).sync();
        // 一直等待，直到连接关闭
        channelFuture.channel().closeFuture().sync();
        bossGroup.shutdownGracefully().sync();
        workerGroup.shutdownGracefully().sync();

    }
}
