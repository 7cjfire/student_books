package com.onlinecollege.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 统一返回结果
 *
 * <p>与 {@code teacher-service} 的 Result 保持一致的字段和语义，
 * 后续可提取到独立 common 模块。
 *
 * @param <T> 数据类型
 */
@Data
@NoArgsConstructor
@Schema(description = "统一返回结果")
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "状态码", example = "200")
    private Integer code;

    @Schema(description = "返回消息", example = "操作成功")
    private String message;

    @Schema(description = "返回数据")
    private T data;

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    public static <T> Result<T> error(String message) {
        return error(500, message);
    }

    public static <T> Result<T> badRequest(String message) {
        return error(400, message);
    }

    public static <T> Result<T> notFound(String message) {
        return error(404, message);
    }
}
