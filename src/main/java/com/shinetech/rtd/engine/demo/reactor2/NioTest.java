package com.shinetech.rtd.engine.demo.reactor2;

import org.apache.flink.shaded.guava18.com.google.common.collect.Sets;

import javax.swing.text.html.HTMLDocument;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

/**
 * @author: Kelvin Yeuung
 * @createdAt: 2020/8/21 11:55
 * @description:
 */
public class NioTest {

    public static void main(String[] args){
        Set set = new HashSet();
        set.add("a");
        set.add("b");
        set.add("c");
        Iterator it = set.iterator();
        while (it.hasNext()){

            it.next();

            it.remove();
        }

        System.out.println(set);

    }


    public static void test(String[] args) throws IOException {

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(9999));
        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);


        for (; ; ) {
            //System.out.println("start select.");
            selector.select();
            //System.out.println("selected.");
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();

            while (it.hasNext()) {
                SelectionKey key = it.next();
                process(key);
                it.remove();
            }


        }

    }


    public static void process(SelectionKey key) {

        if (key.isAcceptable()) {
            ServerSocketChannel channel = (ServerSocketChannel) key.channel();
            try {
                SocketChannel ch = channel.accept();
                ch.configureBlocking(false);
                ch.register(key.selector(), SelectionKey.OP_READ | SelectionKey.OP_WRITE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (key.isReadable()) {
            System.out.println("is read......");
            SocketChannel ch = (SocketChannel) key.channel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(2);
            try {
                int count = ch.read(byteBuffer);
                if (count > 0) {
                    System.out.println(byteBuffer.array());
                    byteBuffer.clear();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        //if (key.isWritable()) {
        //    ByteBuffer byteBuffer = ByteBuffer.wrap("hello".getBytes());
        //    SocketChannel ch = (SocketChannel) key.channel();
        //    try {
        //        ch.write(byteBuffer);
        //    } catch (IOException e) {
        //        e.printStackTrace();
        //    }
        //}
    }
}
