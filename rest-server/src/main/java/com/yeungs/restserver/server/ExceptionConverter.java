package com.yeungs.restserver.server;

import com.yeungs.restserver.exception.BizException;
import com.yeungs.restserver.exception.ServiceNotFoundException;

/**
 * @author: Kelvin Yeuung
 * @createdAt: 2020/8/21 17:56
 * @description:
 */
public class ExceptionConverter {

    public RestResponse convert(Exception e){
        if(e instanceof ServiceNotFoundException){
            return RestResponse.fail("找不到服务！");
        }
        if(e instanceof BizException){
            return RestResponse.fail(400,e.getMessage());
        }
        return RestResponse.fail(e.getMessage());
    }
}
