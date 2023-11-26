package ru.ashirobokov.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class SimpleClient {
    static final String host = "localhost";
    static final int port = 8007;
//    static final InternalLogger logger = InternalLoggerFactory.getInstance("ru.ashirobokov.client");

    public static void main(String[] args) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            /*
                             * Socket/channel выполняются в байтах. String decoder &
                             * encoder выполняют конверсию между bytes & String.
                             */
                            p.addLast(new StringDecoder());
                            p.addLast(new StringEncoder());
                            p.addLast(new SimpleClientHandler());
                            p.addLast(new LoggingHandler(LogLevel.INFO));
                        }
                    });

            // Start the connection attempt.
            ChannelFuture connectFuture = b.connect(host, port).sync();
/**
 *    wait for the connection future to be completed and the channel closed.
 */
            connectFuture.channel().closeFuture().sync();

        } finally {
            group.shutdownGracefully();
        }

    }
}
