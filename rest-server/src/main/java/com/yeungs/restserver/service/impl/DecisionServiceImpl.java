package com.yeungs.restserver.service.impl;

import com.yeungs.common.domain.EventData;
import com.yeungs.common.enums.RegisterType;
import com.yeungs.restserver.flink.FlinkSocketManager;
import com.yeungs.restserver.server.RestResponse;
import com.yeungs.restserver.service.DecisionService;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author: Kelvin Yeuung
 * @createdAt: 2020/8/21 17:19
 * @description:
 */
public class DecisionServiceImpl implements DecisionService{

    @Override
    public RestResponse makDecision(ChannelHandlerContext ctx,EventData eventData) {
        FlinkSocketManager.INSTANCE.send(ctx, RegisterType.SOURCE,eventData.getEventSourceCode(), eventData);
        return RestResponse.ok("成功");
    }




}
