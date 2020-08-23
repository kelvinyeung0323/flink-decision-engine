package com.yeungs.common.coordinate;

import com.yeungs.common.enums.RegisterType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.InetAddress;

/**
 * @author: Kelvin Yeuung
 * @createdAt: 2020/8/22 18:33
 * @description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterData {
    private String eventSourceCode;
    private RegisterType type;
    private String host;
    private Integer port;
}
