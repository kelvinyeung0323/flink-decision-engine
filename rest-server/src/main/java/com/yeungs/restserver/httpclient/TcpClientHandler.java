package com.yeungs.restserver.httpclient;

import com.yeungs.common.domain.FlinkData;
import com.yeungs.restserver.server.RequestDispatcher;
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

    private RequestDispatcher requestDispatcher;

    public RequestDispatcher getRequestDispatcher() {
        return requestDispatcher;
    }

    public void setRequestDispatcher(RequestDispatcher requestDispatcher) {
        this.requestDispatcher = requestDispatcher;
    }

    public TcpClientHandler() {
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof FlinkData){
            FlinkData data = (FlinkData) msg;
            requestDispatcher.onResponse(data);
        }
    }
}
