package com.nealma.netty.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

/**
 * Netty Client Handler
 *
 * @author neal.ma
 * @date 2020/10/9
 * @blog nealma.com
 */
@ChannelHandler.Sharable
public class NettyClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
    @Override
    public void channelRead0(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {
        System.out.println(
                Thread.currentThread().getId() + " " +
                "Client read: " +
                        buf.toString(CharsetUtil.UTF_8)
        );
        ctx.writeAndFlush(Unpooled.copiedBuffer("from client " + ctx.channel().id().asShortText(), CharsetUtil.UTF_8));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(
                Thread.currentThread().getId() + " active"
        );
        ctx.writeAndFlush(Unpooled.copiedBuffer("say from client", CharsetUtil.UTF_8));
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println(
                Thread.currentThread().getId() + " register"
        );
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
