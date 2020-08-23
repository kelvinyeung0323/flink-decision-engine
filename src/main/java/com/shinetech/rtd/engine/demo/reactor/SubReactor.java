package com.shinetech.rtd.engine.demo.reactor;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: Kelvin Yeuung
 * @createdAt: 2020/8/20 15:01
 * @description:
 */
public class SubReactor {

    private int corePoolSize = 12;
    private int maximumPoolSize = 100;
    private long keepAliveTime = 20000;

    private ThreadPoolExecutor poolExecutor;

    public SubReactor() {

        poolExecutor = new ThreadPoolExecutor(corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(100));

        poolExecutor.prestartAllCoreThreads();
    }


    public void accept(Selector selector){

        poolExecutor.execute(()->{

            for(;;) {

                System.out.println("select adadadad");
                try {
                    selector.select();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("selected");
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    SocketChannel channel = (SocketChannel) key.channel();
                    poolExecutor.submit(new RequestHandler(channel));
                }

            }
        });

    }


}
