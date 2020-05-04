package nia.chapter2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.Charset;

@ChannelHandler.Sharable
public class EchoServerChannelHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String clientId = ctx.channel()
                             .id()
                             .toString();
        System.out.println("连接：" + clientId);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String clientId = ctx.channel()
                             .id()
                             .toString();
        System.out.println("中断连接：" + clientId);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx,
                            Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        System.out.println("接收消息：" + in.toString(Charset.defaultCharset()));
        ctx.write(in);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
           .addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,
                                Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
