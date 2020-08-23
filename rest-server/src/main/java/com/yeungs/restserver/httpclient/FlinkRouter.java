package com.yeungs.restserver.httpclient;

import com.yeungs.common.enums.RegisterType;
import com.yeungs.restserver.common.FlinkSocketPool;
import com.yeungs.common.domain.ConfigData;
import com.yeungs.common.domain.EventData;
import com.yeungs.restserver.server.RequestDispatcher;

/**
 * @author: Kelvin Yeuung
 * @createdAt: 2020/8/21 20:08
 * @description: 用于路由到对应的flinkjob
 */
public class FlinkRouter {

    private FlinkSocketPool socketPool;

    private RequestDispatcher requestDispatcher;

    public FlinkRouter(RequestDispatcher requestDispatcher) {
        socketPool = new FlinkSocketPool(requestDispatcher);
        socketPool.start();
    }
    public void  decision(EventData data){
      socketPool.send(RegisterType.SOURCE,data.getEventSourceCode(),data);
    }
    public void configRule(ConfigData data){
        socketPool.send(RegisterType.CONFIG,data.getEvnetSourceCode(),data);
    }





}
