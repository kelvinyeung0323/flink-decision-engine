package com.yeungs.restserver.service.impl;

import com.yeungs.common.domain.EventData;
import com.yeungs.restserver.httpclient.FlinkRouter;
import com.yeungs.restserver.server.OnResponseListener;
import com.yeungs.restserver.server.RestResponse;
import com.yeungs.restserver.service.DecisionService;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: Kelvin Yeuung
 * @createdAt: 2020/8/21 17:19
 * @description:
 */
public class DecisionServiceImpl implements DecisionService , OnResponseListener<EventData> {

    private FlinkRouter flinkRouter;
    private Map<String,ChannelHandlerContext> ctxMap= new ConcurrentHashMap<>();

    public FlinkRouter getFlinkRouter() {
        return flinkRouter;
    }

    public void setFlinkRouter(FlinkRouter flinkRouter) {
        this.flinkRouter = flinkRouter;
    }

    @Override
    public RestResponse makDecision(ChannelHandlerContext ctx,EventData eventData) {
        String seq = UUID.randomUUID().toString().replace("-","");
        eventData.setSeq(seq);
        ctxMap.put(seq,ctx);
        flinkRouter.decision(eventData);
        return RestResponse.ok("成功");
    }

    @Override
    public void onResponse(EventData data) {
        ChannelHandlerContext ctx = ctxMap.get(data.getSeq());
        ctx.writeAndFlush(data).addListener(ChannelFutureListener.CLOSE);
    }


}
