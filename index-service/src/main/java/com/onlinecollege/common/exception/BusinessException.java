package com.onlinecollege.common.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final int code;

    public BusinessException(int code, String message) { super(message); this.code = code; }
    public BusinessException(String message)           { this(500, message); }

    public static BusinessException badRequest(String m) { return new BusinessException(400, m); }
    public static BusinessException notFound(String m)   { return new BusinessException(404, m); }
    public static BusinessException conflict(String m)   { return new BusinessException(409, m); }
}
