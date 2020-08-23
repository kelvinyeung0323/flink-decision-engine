package com.shinetech.rtd.engine.demo;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @author: Kelvin Yeuung
 * @createdAt: 2019/8/6 21:01
 * @description:
 */
public class Demo01 {

    public static void main(String[] args){
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<String> input = env.readTextFile("file:///path/to/file");

        DataStream<Integer> parsed = input.map(new MapFunction<String, Integer>() {
            @Override
            public Integer map(String value) {
                return Integer.parseInt(value);
            }
        });


    }
}
