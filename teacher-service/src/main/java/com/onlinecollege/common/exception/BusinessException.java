package com.onlinecollege.common.exception;

import lombok.Getter;

/**
 * 业务异常
 *
 * <p>语义：由 Service 层主动抛出的可预期业务错误，由全局异常处理器统一封装为 {@code Result} 返回，
 * 不会污染日志里的 ERROR 级别堆栈。
 */
@Getter
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(String message) {
        this(500, message);
    }

    public static BusinessException badRequest(String message) {
        return new BusinessException(400, message);
    }

    public static BusinessException notFound(String message) {
        return new BusinessException(404, message);
    }

    public static BusinessException conflict(String message) {
        return new BusinessException(409, message);
    }
}
