package com.shinetech.rtd.engine.demo.reactor;

import java.io.IOException;
import java.nio.channels.*;
import java.util.Iterator;

/**
 * @author: Kelvin Yeuung
 * @createdAt: 2020/8/20 14:45
 * @description:
 */
public class Acceptor {
    private Selector[] selectors;
    private SubReactor[] subReactors;
    private int subNum;
    private int reqs;

    public Acceptor(int subNum) throws IOException {
        this.subNum = subNum;
        init();
    }

    public void init() throws IOException {
        selectors = new Selector[subNum];
        subReactors = new SubReactor[subNum];
        for (int i = 0; i < subNum; i++) {
            selectors[i] = Selector.open();
            subReactors[i]= new SubReactor();
            subReactors[i].accept(selectors[i]);
        }

    }

    public void dispatch(Iterator<SelectionKey> keys) throws IOException {
        System.out.println("acceptor dispatch..");
        while (keys.hasNext()) {
            System.out.println("acceptor hasNext.");
            SelectionKey key = keys.next();
            ServerSocketChannel channel = (ServerSocketChannel) key.channel();
            SocketChannel socketChannel = channel.accept();
            socketChannel.configureBlocking(false);
            int j = reqs % subNum;
            Selector selector = selectors[j];
            socketChannel.register(selector, SelectionKey.OP_READ|SelectionKey.OP_WRITE);
            System.out.println("acceptor accepted4 .................");
            //selectors[j].wakeup();
            reqs = ++reqs > subNum ? reqs = 0 : reqs;

        }

    }



}
