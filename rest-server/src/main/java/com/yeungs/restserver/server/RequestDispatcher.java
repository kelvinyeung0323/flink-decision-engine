package com.yeungs.restserver.server;

import com.alibaba.fastjson.JSONObject;
import com.yeungs.common.domain.EventData;
import com.yeungs.common.domain.FlinkData;
import com.yeungs.restserver.exception.ServiceNotFoundException;
import com.yeungs.restserver.service.DecisionService;
import com.yeungs.restserver.service.impl.DecisionServiceImpl;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: Kelvin Yeuung
 * @createdAt: 2020/8/21 15:09
 * @description:
 */
public class RequestDispatcher{

    Map<String, Object[]> pathMap = new ConcurrentHashMap<>();


    public RequestDispatcher() {
        initServiceMap();
    }


    public Object dispatch(ChannelHandlerContext ctx, FullHttpRequest request) {
        String uri = request.uri();
        HttpMethod httpMethod = request.method();

        uri = httpMethod.name() + ":" + uri;

        Object[] seviceMethod = pathMap.get(uri);
        if (seviceMethod == null) {
            throw new ServiceNotFoundException();
        }
        Object service = seviceMethod[0];
        Method method = (Method) seviceMethod[1];
        Class[] types = method.getParameterTypes();
        Object[] params = new Object[method.getParameterCount()];
        //装配参数
        for (int i = 0; i < types.length; i++) {

            //TODO:一些对象自动装配，区分GET/POST/PUT等方法
            if (types[i] == FullHttpRequest.class) {
                params[i] = request;
            } else if(ChannelHandlerContext.class.isAssignableFrom(types[i])){
                params[i] = ctx;

            }else if (FlinkData.class.isAssignableFrom(types[i])) {
                FlinkData flinkData = JSONObject.parseObject(request.content().array(), types[i]);
                params[i] = flinkData;
            } else {
                //TODO:
                params[i] = null;
            }
        }
        Object result = null;
        try {
            result = method.invoke(service, params);

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return result;
    }


    public <T> Map<String, Object[]> initServiceMap() {
        String key = HttpMethod.POST.name() + ":" + "/decision";

        DecisionService decisionService = new DecisionServiceImpl();
        try {
            Object[] serviceMethd = new Object[]{decisionService, decisionService.getClass().getMethod("makDecision", EventData.class)};
            //TODO:可改为自动注入
            pathMap.put(key, serviceMethd);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        return this.pathMap;
    }


}
