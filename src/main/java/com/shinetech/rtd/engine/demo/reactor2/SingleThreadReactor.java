package com.shinetech.rtd.engine.demo.reactor2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author: Kelvin Yeuung
 * @createdAt: 2020/8/20 23:01
 * @description:
 */
public class SingleThreadReactor implements Runnable{

    private Selector selector;

    private SingleThreadReactor() throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        //ServerSocket sc = ssc.socket();
        ssc.bind(new InetSocketAddress(9999));
        ssc.configureBlocking(false);
        selector = Selector.open();

        SelectionKey key =  ssc.register(selector,SelectionKey.OP_ACCEPT);

        key.attach(new Acceptor(selector,ssc,key));
    }


    @Override
    public void run() {

        try {
            while (!Thread.interrupted()){
                selector.select();
                Set selected = selector.selectedKeys();
                System.out.println("selected key size:"+ selected.size());
                Iterator<SelectionKey> iterator = selected.iterator();
                while (iterator.hasNext()){
                    //iterator.remove();
                    dispatch(iterator.next());
                    iterator.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void dispatch(SelectionKey next) {
        Runnable runnable = (Runnable) next.attachment();
        if(runnable != null){
            runnable.run();
        }

    }

    private class Acceptor implements Runnable {


        private ServerSocketChannel serverSo;
        private SelectionKey selectionKey;
        private Selector selector;
        public Acceptor(Selector selector,ServerSocketChannel serverSo,SelectionKey key ) {
            this.selector = selector;
            this.serverSo = serverSo;
            this.selectionKey = key ;
        }

        @Override
        public void run() {
            try {
                SocketChannel sc = serverSo.accept();
                if(sc != null){
                    new Handler(sc,this.selector,selectionKey).run();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static class Handler implements Runnable{
        public static final int READING  = 0,WRITING = 1;
        int state;

        private SocketChannel sc;
        private Selector selector;
        private SelectionKey selectionKey;


        public Handler(SocketChannel sc, Selector selector,SelectionKey selectionKey) {

            this.sc = sc;
            this.selector = selector;
            this.selectionKey = selectionKey;

        }


        @Override
        public void run() {
            if(state == READING){
                read();
            }else if(state == WRITING){
                write();
            }

        }

        private void read() {
            process();
            ByteBuffer byteBuffer = ByteBuffer.allocate(4);
            try {
                System.out.print("read:");
                while (sc.read(byteBuffer)>0){
                    System.out.print(new String(byteBuffer.array()));
                    byteBuffer.clear();
                };
                System.out.println("\n");

            } catch (IOException e) {
                e.printStackTrace();
            }

            write();


        }
        public void write(){
            process();
            ByteBuffer byteBuffer = ByteBuffer.wrap("hello".getBytes());
            try {
                sc.write(byteBuffer);
                selectionKey.interestOps();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void process(){
            //
        }
    }

    public static void main(String[] args) throws IOException {
        new SingleThreadReactor().run();
    }
}
