package com.yeungs.restserver.exception;

/**
 * @author: Kelvin Yeuung
 * @createdAt: 2020/8/23 15:59
 * @description:
 */
public class FlinkSocketNotFoundException extends RuntimeException {

    public FlinkSocketNotFoundException() {
    }

    public FlinkSocketNotFoundException(String message) {
        super(message);
    }

    public FlinkSocketNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public FlinkSocketNotFoundException(Throwable cause) {
        super(cause);
    }

    public FlinkSocketNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
