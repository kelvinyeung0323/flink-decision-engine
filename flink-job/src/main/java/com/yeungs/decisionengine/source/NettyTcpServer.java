package com.yeungs.decisionengine.source;

import com.yeungs.common.flink.serialize.MarshallingCodeFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author: Kelvin Yeuung
 * @createdAt: 2020/8/22 14:23
 * @description:
 */
public class NettyTcpServer implements BaseServer {

    private static final Logger logger = LoggerFactory.getLogger(NettyTcpServer.class);


    private int tryPort;
    private SourceFunction.SourceContext<String> ctx;
    private ServerBootstrap tcpOpts;
    private int threadNum;
    private int maxFrameLen;
    private LogLevel logLevel;

    //-------------
    private EventLoopGroup bossGroup ;
    private EventLoopGroup workerGroup;
    private AtomicBoolean isRunning ;
    private InetSocketAddress currentAddr= null;

    public NettyTcpServer(int tryPort, SourceFunction.SourceContext<String> ctx, ServerBootstrap tcpOpts, Integer threadNum, int maxFrameLen, LogLevel logLevel) {
        this.tryPort = tryPort;
        this.ctx = ctx;
        this.tcpOpts = tcpOpts;
        this.threadNum = threadNum;
        this.maxFrameLen = maxFrameLen;
        this.logLevel = logLevel;
     //--------------------------
        if(null ==threadNum){
            threadNum = Runtime.getRuntime().availableProcessors();
        }
        bossGroup = new NioEventLoopGroup(threadNum);
        workerGroup = new NioEventLoopGroup();
        isRunning = new AtomicBoolean(false);


    }

    @Override
    public InetSocketAddress startNettyServer(Integer portNotInUse) {

        if(!isRunning.get()){
            ServerBootstrap server = new  ServerBootstrap();

            server.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY,true)
                    .option(ChannelOption.SO_KEEPALIVE,true)
            .handler(new LoggingHandler())
            .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline p = ch.pipeline();
                    p.addLast(MarshallingCodeFactory.getEncoder());
                    p.addLast(MarshallingCodeFactory.getDecoder());
                    //p.addLast(new TcpHandler(ctx));
                }
            });

            //start the server
            ChannelFuture f = server.bind(portNotInUse);
            f.syncUninterruptibly();
            currentAddr  = (InetSocketAddress) f.channel().localAddress();
            logger.info("start tcp server on address: {}",currentAddr);
            isRunning.set(true);

            //注册
            //register(currentAddr, callbackUrl);
            try {
                f.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return currentAddr;


        }else{
            logger.info("server is running on address: {}, no need repeat start it",currentAddr);
            return currentAddr;
        }

    }





}
