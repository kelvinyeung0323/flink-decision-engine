package com.shinetech.rtd.engine.demo.reactor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author: Kelvin Yeuung
 * @createdAt: 2020/8/20 15:13
 * @description:
 */
public class RequestHandler implements Runnable {

    private SocketChannel socketChannel;

    public RequestHandler(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public void run() {
        ByteBuffer buffer = ByteBuffer.allocate(20);
        try {

            System.out.println("handle");
            socketChannel.read(buffer);
            System.out.println("read:"+new String(buffer.array()));


            buffer.wrap("return".getBytes());
            socketChannel.write(buffer);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
