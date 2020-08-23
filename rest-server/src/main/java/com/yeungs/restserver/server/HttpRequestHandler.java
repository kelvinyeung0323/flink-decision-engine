package com.yeungs.restserver.server;

import com.alibaba.fastjson.JSONObject;
import com.yeungs.common.domain.FlinkData;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.EventExecutorGroup;

import static io.netty.handler.codec.http.HttpUtil.is100ContinueExpected;

/**
 * @author: Kelvin Yeuung
 * @createdAt: 2020/8/21 14:30
 * @description:
 */

/**
 * 共享没问题，没有 成员变量
 **/
@ChannelHandler.Sharable
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {


    private RequestDispatcher dispatcher;
    private ExceptionConverter exceptionConverter;

    public HttpRequestHandler() {
        this.dispatcher = new RequestDispatcher();
        this.exceptionConverter = new ExceptionConverter();
    }


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {

        //100 Continue含义
        //HTTP客户端程序有一个实体的主体部分要发送给服务器，但希望在发送之前查看下服务器是否会接受这个实体，所以在发送实体之前先发送了一个携带100 Continue的Expect请求首部的请求。
        //服务器在收到这样的请求后，应该用 100 Continue或一条错误码来进行响应。
        if (is100ContinueExpected(request)) {
            ctx.write(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE));
        }
        //设置头信息
        try {
            dispatcher.dispatch(ctx, request);
        } catch (Exception e) {
            e.printStackTrace();
            RestResponse r = exceptionConverter.convert(e);
            //创建http响应
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(r.getStatus()), Unpooled.copiedBuffer(JSONObject.toJSONString(r), CharsetUtil.UTF_8));
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }


    }

}
