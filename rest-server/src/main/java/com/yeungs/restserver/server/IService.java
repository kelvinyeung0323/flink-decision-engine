package com.yeungs.restserver.server;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author: Kelvin Yeuung
 * @createdAt: 2020/8/21 15:13
 * @description:
 */
public interface IService<T> {

    RestResponse doService(FullHttpRequest request, FullHttpResponse response);

}
