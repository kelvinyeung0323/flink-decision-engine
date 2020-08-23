package com.yeungs.restserver.flink;

import com.yeungs.common.domain.FlinkData;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author: Kelvin Yeuung
 * @createdAt: 2020/8/21 19:36
 * @description:
 */
@ChannelHandler.Sharable
public class TcpClientHandler extends ChannelInboundHandlerAdapter {



    public TcpClientHandler() {
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof FlinkData){
            FlinkData data = (FlinkData) msg;
            FlinkSocketManager.INSTANCE.callback(data.getSeq(),data);
        }
    }
}
