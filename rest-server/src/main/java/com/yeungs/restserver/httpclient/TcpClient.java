package com.yeungs.restserver.httpclient;

import com.yeungs.common.flink.serialize.MarshallingCodeFactory;
import com.yeungs.restserver.server.RequestDispatcher;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author: Kelvin Yeuung
 * @createdAt: 2020/8/21 19:21
 * @description:
 */
public class TcpClient {

    private EventLoopGroup worker = new NioEventLoopGroup(10);
    private Bootstrap bootstrap = new Bootstrap();

    private Set<SocketChannel> channels = new CopyOnWriteArraySet<>();

    private RequestDispatcher dispatcher;
    private TcpClientHandler tcpClientHandler;

    public TcpClient(RequestDispatcher dispatcher) {
        this.dispatcher = dispatcher;
        this.tcpClientHandler = new TcpClientHandler();
        this.tcpClientHandler.setRequestDispatcher(dispatcher);
        init();
    }

    private void init() {
        bootstrap.group(worker).channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new TcpChannelInitializer(this.tcpClientHandler));
    }

    public SocketChannel connect(String host, int port) throws Exception {
        EventLoopGroup worker = new NioEventLoopGroup();
        try {

            ChannelFuture f = bootstrap.connect(host, port).sync();
            //f.sync();
            return (SocketChannel) f.channel();

        } finally {
            //worker.shutdownGracefully();
        }
    }

    public void send(Object data) {

    }
}
