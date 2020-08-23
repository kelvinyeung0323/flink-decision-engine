package com.yeungs.restserver.common;

import com.yeungs.restserver.httpclient.TcpChannelInitializer;
import com.yeungs.restserver.httpclient.TcpClientHandler;

/**
 * @author: Kelvin Yeuung
 * @createdAt: 2020/8/23 19:10
 * @description:
 */
public class DecisionContext {

    private TcpClientHandler tcpClientHandler;
    private TcpChannelInitializer tcpChannelInitializer;
    private FlinkSocketPool flinkSocketPool;

}
