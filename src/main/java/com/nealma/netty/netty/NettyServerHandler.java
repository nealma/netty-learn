package com.nealma.netty.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.util.concurrent.TimeUnit;

/**
 * Netty Server Handler
 *
 * @author neal.ma
 * @date 2020/10/9
 * @blog nealma.com
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter{

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println(Thread.currentThread().getId() + " " + ctx.channel().remoteAddress() + " registered.");
    }

    /**
     * 客户端上线
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(Thread.currentThread().getId() + " " + ctx.channel().remoteAddress() + " active.");
    }

    /**
     * 读就绪
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        System.out.println(
                "Server read: " +
                        buf.toString(CharsetUtil.UTF_8)
        );
        System.out.println(Thread.currentThread().getId() + " " + ctx.channel().remoteAddress() + " read.");
    }

    /**
     * 读完成
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);

        ctx.writeAndFlush(Unpooled.copiedBuffer(" from server " +
                        ctx.channel().id().asLongText(), CharsetUtil.UTF_8));
        TimeUnit.SECONDS.sleep(5);
        System.out.println(Thread.currentThread().getId() + " " + ctx.channel().remoteAddress() + " readComplete.");
    }

    /**
     * 异常处理
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
