package com.yeungs.decisionengine.source;

import com.yeungs.decisionengine.util.NettyUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import scala.Option;
import scala.Unit;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.URLEncoder;
import java.net.UnknownHostException;

/**
 * @author: Kelvin Yeuung
 * @createdAt: 2020/8/22 17:14
 * @description:
 */
public interface BaseServer {
    default InetSocketAddress start(Integer tryPort,int maxtries,String serviceName) throws Exception {
       return NettyUtil.startServiceOnPort(tryPort, (p) -> startNettyServer(p),maxtries,"serviceName");
    }
    InetSocketAddress startNettyServer(Integer portNotInUse);

    default <T> void register(InetSocketAddress registerServerAddress , T data )  {


    }
}
