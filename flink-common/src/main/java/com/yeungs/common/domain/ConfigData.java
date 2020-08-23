package com.yeungs.common.domain;

import lombok.Data;

/**
 * @author: Kelvin Yeuung
 * @createdAt: 2020/8/21 20:16
 * @description:
 */
@Data
public class ConfigData<T>  extends FlinkData{
    private String evnetSourceCode;
    private T data;
}
