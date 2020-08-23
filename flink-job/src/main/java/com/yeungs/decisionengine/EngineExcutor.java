package com.yeungs.decisionengine;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @author: Kelvin Yeuung
 * @createdAt: 2020/8/22 14:17
 * @description:
 */
public class EngineExcutor {

    StreamExecutionEnvironment env ;
    public static void main(String[] args) {


    }

    public EngineExcutor() {
        env =  StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(10);
        //env.addSource();
    }
}
