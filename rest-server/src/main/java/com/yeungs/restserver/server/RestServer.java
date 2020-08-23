package com.yeungs.restserver.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author: Kelvin Yeuung
 * @createdAt: 2020/8/21 14:12
 * @description:
 */
public class RestServer {

    private EventLoopGroup boss;
    private EventLoopGroup worker;
    private ServerBootstrap server;

    public RestServer(int port) {
        try {
            boss = new NioEventLoopGroup(1);
            worker = new NioEventLoopGroup(10);
            server = new ServerBootstrap();
            server.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .childHandler(new ServerHandlerInitailizer());
            ChannelFuture f = server.bind(port);

            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }

    }


}
