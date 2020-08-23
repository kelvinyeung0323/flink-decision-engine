package com.yeungs.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: Kelvin Yeuung
 * @createdAt: 2020/8/21 17:21
 * @description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventData<T> extends FlinkData{

    private String eventSourceCode;
    private String requestId;
    private T data;

}
