package com.yeungs.restserver.flink;

import com.yeungs.common.coordinate.RegisterData;
import com.yeungs.common.coordinate.RegisterListener;
import com.yeungs.common.coordinate.ZooRegisterUtil;
import com.yeungs.common.domain.FlinkData;
import com.yeungs.common.enums.RegisterType;
import com.yeungs.restserver.exception.FlinkSocketNotFoundException;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;

import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author: Kelvin Yeuung
 * @createdAt: 2020/8/23 15:01
 * @description:
 */
public class FlinkSocketManager implements RegisterListener {

    public static volatile  FlinkSocketManager INSTANCE;

    private ConcurrentHashMap<String, LinkedBlockingQueue<SocketChannel>> channelMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String,ChannelHandlerContext> contextMap  = new ConcurrentHashMap<>();

    private ZooRegisterUtil register;

    private AtomicBoolean isStarted = new AtomicBoolean(false);

    private  FlinkSocketManager() {
        register = new ZooRegisterUtil();
    }

    public static FlinkSocketManager getInstance(){
        if(INSTANCE == null) {
            synchronized (FlinkSocketManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new FlinkSocketManager();
                }
            }
        }
        return INSTANCE;
    }



    public void start() {
        if(isStarted.get()){
            return;
        }
        synchronized (this){
            if(isStarted.get()){
                return;
            }
            try {
                register.monitor(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            isStarted.compareAndSet(false,true);
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
        TcpClient tcpClient = new TcpClient();
        try {
            //TODO:处理连不上的情况
            //TODO:建立多个连接
            SocketChannel socketChannel = tcpClient.connect(data.getHost(), data.getPort());
            queue.put(socketChannel);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public <T> void  send(ChannelHandlerContext ctx, RegisterType type, String eventSourceCode, FlinkData data){
        String seqNo = UUID.randomUUID().toString().replace("-","");
        data.setSeq(seqNo);
        //获取连接
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

        registerResponseContext(seqNo,ctx);
    }


    /**
     * 请求的序号
     * @param seqNo
     * @param context
     */
    public void registerResponseContext(String seqNo,ChannelHandlerContext context){
        contextMap.put(seqNo,context);
    }


    public <T> void callback(String seqNo,T data){
        ChannelHandlerContext context  = contextMap.get(seqNo);
        if(null == context){
            return;
        }
        //TODO:对返回数据进行封装
        context.writeAndFlush(data).addListener(ChannelFutureListener.CLOSE);
        //TODO:请求超时 对象清理
        contextMap.remove(seqNo);
    }
}
