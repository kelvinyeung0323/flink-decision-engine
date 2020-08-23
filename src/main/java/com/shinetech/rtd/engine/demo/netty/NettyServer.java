package com.shinetech.rtd.engine.demo.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.apache.flink.shaded.akka.org.jboss.netty.channel.socket.oio.OioServerSocketChannelFactory;

/**
 * @author: Kelvin Yeuung
 * @createdAt: 2020/8/19 12:36
 * @description:
 */
public class NettyServer {

    public static void main(String[] arg){

        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup work = new NioEventLoopGroup(2);


        ServerBootstrap b = new ServerBootstrap();
        //b.group(boss).channel(OioServerSocketChannelFactory)



    }
}
