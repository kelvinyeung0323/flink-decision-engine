package com.yeungs.restserver.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * @author: Kelvin Yeuung
 * @createdAt: 2020/8/21 14:23
 * @description:
 */
public class ServerHandlerInitailizer extends ChannelInitializer {
    private HttpRequestHandler httpRequestHandler = new HttpRequestHandler();

    @Override
    protected void initChannel(Channel ch) throws Exception {
       ChannelPipeline p =  ch.pipeline();
       p.addLast(new HttpServerCodec());
       p.addLast("httpAggregator",new HttpObjectAggregator(512*1024));//http聚合器
        p.addLast(httpRequestHandler);//请求处理器

    }
}
