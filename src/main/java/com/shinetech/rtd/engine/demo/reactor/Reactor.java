package com.shinetech.rtd.engine.demo.reactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.*;
import java.util.Iterator;

/**
 * @author: Kelvin Yeuung
 * @createdAt: 2020/8/20 14:28
 * @description:
 */
public class Reactor {

    private ServerSocketChannel serverSocketChannel;
    private ServerSocket socket;
    private Selector selector;
    private Acceptor acceptor;

    public Reactor(int port) throws IOException {
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        socket = serverSocketChannel.socket();
        InetSocketAddress address = new InetSocketAddress(port);
        socket.bind(address);
        selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        acceptor = new Acceptor(10);

    }


    public void run() throws IOException {
        System.out.println("reactor select.");
        for (; ; ) {

            try {
                selector.select();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
            acceptor.dispatch(keys);

        }

    }

    public static void main(String[] args) throws IOException {
        Reactor reactor = new Reactor(9999);
        reactor.run();
    }
}
