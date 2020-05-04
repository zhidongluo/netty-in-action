package nia.chapter2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

public class EchoServer {

    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws InterruptedException {
        // if (args.length != 1) {
        //     System.out.println("Usage: " + EchoServer.class.getSimpleName() + "<port>");
        // }
        // int port = Integer.parseInt(args[0]);
        int port = 9999;
        new EchoServer(port).start();
    }

    public void start() throws InterruptedException {
        EventLoopGroup group = null;
        try {
            final EchoServerChannelHandler serverChannelHandler = new EchoServerChannelHandler();
            group = new NioEventLoopGroup();

            ServerBootstrap serverBootstrap = new ServerBootstrap();
            // 指定NioEventLoopGroup接受和处理新的连接
            serverBootstrap.group(group)
                           // 设置Channel的类型为NioServerSocketChannel
                           .channel(NioServerSocketChannel.class)
                           // 绑定端口
                           .localAddress(new InetSocketAddress(port))
                           // 一个新的连接被接受时，一个新的子Channel将会被创建，而ChannelInitializer会把一个你的EchoServerHandler实例添加到该Channel的
                           // ChannelPipeline中
                           .childHandler(new ChannelInitializer<SocketChannel>() {
                               @Override
                               protected void initChannel(SocketChannel ch) throws Exception {
                                   ch.pipeline()
                                     .addLast(serverChannelHandler);
                               }
                           });

            // 异步绑定服务器，调用sync方法阻塞等待直到绑定完成
            ChannelFuture future = serverBootstrap.bind()
                                                  .sync();

            // 获取channel的closeFuture，并阻塞当前线程直到它完成
            future.channel()
                  .closeFuture()
                  .sync();


        } finally {
            // 关闭EventLoopGroup，释放所有资源
            group.shutdownGracefully()
                 .sync();
        }
    }


}
