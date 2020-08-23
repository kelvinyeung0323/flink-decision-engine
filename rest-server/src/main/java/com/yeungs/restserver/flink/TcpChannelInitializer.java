package com.yeungs.restserver.flink;

import com.yeungs.common.flink.serialize.MarshallingCodeFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;

/**
 * @author: Kelvin Yeuung
 * @createdAt: 2020/8/23 19:07
 * @description:
 */
public class TcpChannelInitializer extends ChannelInitializer {

    private TcpClientHandler tcpClientHandler;

    public TcpChannelInitializer(TcpClientHandler tcpClientHandler) {
        this.tcpClientHandler = tcpClientHandler;
    }

    @Override
    protected void initChannel(Channel channel) throws Exception {
        ChannelPipeline p = channel.pipeline();
        p.addLast(MarshallingCodeFactory.getEncoder());
        p.addLast(MarshallingCodeFactory.getDecoder());
        p.addLast(tcpClientHandler);
    }
}
