package com.yeungs.restserver.common;

import com.yeungs.common.coordinate.RegisterData;
import com.yeungs.common.coordinate.RegisterListener;
import com.yeungs.common.coordinate.ZooRegisterUtil;
import com.yeungs.common.enums.RegisterType;
import com.yeungs.restserver.exception.FlinkSocketNotFoundException;
import com.yeungs.restserver.httpclient.TcpClient;
import com.yeungs.restserver.server.RequestDispatcher;
import io.netty.channel.socket.SocketChannel;
import org.apache.flink.runtime.akka.RemoteAddressExtension;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author: Kelvin Yeuung
 * @createdAt: 2020/8/23 15:01
 * @description:
 */
public class FlinkSocketPool implements RegisterListener {

    private ConcurrentHashMap<String, LinkedBlockingQueue<SocketChannel>> channelMap = new ConcurrentHashMap<>();


    private RequestDispatcher requestDispatcher;
    private ZooRegisterUtil register;

    public FlinkSocketPool(RequestDispatcher requestDispatcher) {
        this.requestDispatcher = requestDispatcher;
    }

    public void start() {
        try {
            register.monitor(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onChanged(RegisterData data) {


    }

    @Override
    public void onDeleted(RegisterData data) {
        String key = data.getEventSourceCode() + ":" + data.getType();
        if (!channelMap.contains(key)) {
           return;
        }
        LinkedBlockingQueue queue = channelMap.get(key);
        for (Object o : queue.toArray()) {
            SocketChannel channel = (SocketChannel) o;
            InetSocketAddress addr = channel.remoteAddress();
            if(addr.getPort() == data.getPort() && addr.getHostString().equals(data.getHost())){
                queue.remove(channel);
            }
        }
    }

    @Override
    public void onCreated(RegisterData data) {
        String key = data.getEventSourceCode() + ":" + data.getType();
        if (!channelMap.contains(key)) {
            LinkedBlockingQueue queue = new LinkedBlockingQueue();
            channelMap.putIfAbsent(key, queue);
        }
        LinkedBlockingQueue queue = channelMap.get(key);
        //建立连接
        TcpClient tcpClient = new TcpClient(requestDispatcher);
        try {
            //TODO:处理连不上的情况
            //TODO:建立多个连接
            SocketChannel socketChannel = tcpClient.connect(data.getHost(), data.getPort());
            queue.put(socketChannel);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public SocketChannel poll(String key){
         LinkedBlockingQueue<SocketChannel> queue = channelMap.get(key);
        if(queue != null){
            return queue.poll();
        }
        return null;
    }

    public <T> void  send(RegisterType type,String eventSourceCode,T data){

        LinkedBlockingQueue<SocketChannel> queue = channelMap.get(eventSourceCode+":"+ type);
        if(queue == null){
            throw new FlinkSocketNotFoundException("获取不到Flink Source连接!");
        }
        SocketChannel socketChannel = queue.poll();

        if(socketChannel == null){
            throw new FlinkSocketNotFoundException("获取不到Flink Source连接!");
        }
        socketChannel.writeAndFlush(data);
        try {
            queue.put(socketChannel);
        } catch (InterruptedException e) {
            e.printStackTrace();
            //TODO:处理
        }

    }
}
