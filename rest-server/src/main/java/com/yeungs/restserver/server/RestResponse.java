package com.yeungs.restserver.server;

/**
 * @author: Kelvin Yeuung
 * @createdAt: 2020/8/21 15:13
 * @description:
 */
public class RestResponse<T> {
    private int status;
    private String msg;
    private T data;

    public RestResponse(int status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static RestResponse OK(){
        return new RestResponse(202,"sucess",null);
    }

    public static <R> RestResponse<R> ok(R data){
        return new RestResponse(202,"sucess",data);
    }

    public static <R> RestResponse<R> fail(){
        return new RestResponse<>(500,"fail",null);
    }
    public static <R> RestResponse<R> fail(String msg){
        return new RestResponse<>(500,msg,null);
    }

    public static <R> RestResponse<R> fail(int status,String msg){
        return new RestResponse<>(status,msg,null);
    }
}
