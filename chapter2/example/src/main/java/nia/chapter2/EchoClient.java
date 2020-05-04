package nia.chapter2;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

public class EchoClient {

    private final String host;
    private final int port;

    public EchoClient(String host,
                      int port) {
        this.port = port;
        this.host = host;
    }

    public void start() throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
             .channel(NioSocketChannel.class)
             .localAddress(new InetSocketAddress(6789))
             .remoteAddress(new InetSocketAddress(host,port))
             .handler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 protected void initChannel(SocketChannel ch) throws Exception {
                     ch.pipeline()
                       .addLast(new EchoClientHandler());
                 }
             });

            ChannelFuture future = b.connect()
                                  .sync();

            future.channel()
                  .closeFuture()
                  .sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // if (args.length != 2) {
        //     System.out.println("usage: " + EchoClient.class.getSimpleName() + "<host> <port>");
        // }
        // String host = args[0];
        // int    port    = Integer.parseInt(args[1]);
        String host = "localhost";
        int port = 9999;
        new EchoClient(host,port).start();
    }
}
