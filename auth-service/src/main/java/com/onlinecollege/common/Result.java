package com.onlinecollege.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@Schema(description = "统一返回结果")
public class Result<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "状态码", example = "200") private Integer code;
    @Schema(description = "返回消息", example = "操作成功") private String message;
    @Schema(description = "返回数据") private T data;

    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.setCode(200); r.setMessage("操作成功"); r.setData(data);
        return r;
    }
    public static <T> Result<T> success() { return success(null); }
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> r = new Result<>(); r.setCode(code); r.setMessage(message); return r;
    }
    public static <T> Result<T> error(String message) { return error(500, message); }
    public static <T> Result<T> badRequest(String message) { return error(400, message); }
    public static <T> Result<T> unauthorized(String message) { return error(401, message); }
    public static <T> Result<T> notFound(String message) { return error(404, message); }
}
