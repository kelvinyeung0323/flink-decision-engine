package com.yeungs.common.coordinate;

import com.yeungs.common.coordinate.RegisterData;

/**
 * @author: Kelvin Yeuung
 * @createdAt: 2020/8/23 14:44
 * @description:
 */
public interface RegisterListener {

    void onChanged(RegisterData data);
    void onDeleted(RegisterData data);
    void onCreated(RegisterData data);
}
