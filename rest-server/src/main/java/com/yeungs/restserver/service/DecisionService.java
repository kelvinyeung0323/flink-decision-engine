package com.yeungs.restserver.service;

import com.yeungs.common.domain.EventData;
import com.yeungs.restserver.server.RestResponse;
import io.netty.channel.ChannelHandlerContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: Kelvin Yeuung
 * @createdAt: 2020/8/21 17:19
 * @description:
 */
public interface DecisionService {



    public RestResponse makDecision(ChannelHandlerContext ctx,EventData eventData);
}
