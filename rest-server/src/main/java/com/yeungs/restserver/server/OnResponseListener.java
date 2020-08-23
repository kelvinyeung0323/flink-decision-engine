package com.yeungs.restserver.server;

/**
 * @author: Kelvin Yeuung
 * @createdAt: 2020/8/23 16:19
 * @description:
 */
public interface OnResponseListener<T> {

    void onResponse(T data);
}
