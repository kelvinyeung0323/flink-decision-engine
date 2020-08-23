package com.shinetech.rtd.engine.demo.netty;

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
 * @createdAt: 2020/8/19 14:03
 * @description:
 */
public class NioServer {


    public void serve(int port) throws IOException{
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        ServerSocket ss = serverSocketChannel.socket();
        InetSocketAddress address = new InetSocketAddress(port);
        ss.bind(address);

        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        final ByteBuffer msg = ByteBuffer.wrap("Hi!\r\n".getBytes());

        for(;;){

            try {

                selector.select();
            }catch (IOException ex){
                ex.printStackTrace();
                break;
            }

            Set<SelectionKey> readKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = readKeys.iterator();

            while (iterator.hasNext()){
                SelectionKey key = iterator.next();
                iterator.remove();

                try {
                    if(key.isAcceptable()){
                        ServerSocketChannel server= (ServerSocketChannel) key.channel();
                        SocketChannel client = server.accept();
                        client.configureBlocking(false);
                        client.register(selector,SelectionKey.OP_WRITE|SelectionKey.OP_READ,msg.duplicate());
                        System.out.println("Accept conntion from " + client);
                    }
                    if(key.isWritable()){
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer  buffer = (ByteBuffer)key.attachment();
                        while(buffer.hasRemaining()){
                            if(client.write(buffer) == 0 ){
                                if(client.write(buffer) == 0){
                                    break;
                                }
                            }
                        }
                        client.close();
                    }

                }catch (IOException ex){
                    key.cancel();
                    try {
                        key.channel().close();
                    }catch (Exception exc){

                    }
                }


            }


        }
    }
}
