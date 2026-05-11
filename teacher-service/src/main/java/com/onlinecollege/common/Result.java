package com.onlinecollege.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 统一返回结果类
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
    
    /**
     * 成功返回结果
     *
     * @param data 返回数据
     * @param <T>  数据类型
     * @return Result
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }
    
    /**
     * 成功返回结果（无数据）
     *
     * @return Result
     */
    public static <T> Result<T> success() {
        return success(null);
    }
    
    /**
     * 失败返回结果
     *
     * @param code    状态码
     * @param message 错误信息
     * @return Result
     */
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
    
    /**
     * 失败返回结果
     *
     * @param message 错误信息
     * @return Result
     */
    public static <T> Result<T> error(String message) {
        return error(500, message);
    }
    
    /**
     * 参数错误返回结果
     *
     * @param message 错误信息
     * @return Result
     */
    public static <T> Result<T> badRequest(String message) {
        return error(400, message);
    }
    
    /**
     * 未授权返回结果
     *
     * @param message 错误信息
     * @return Result
     */
    public static <T> Result<T> unauthorized(String message) {
        return error(401, message);
    }
    
    /**
     * 禁止访问返回结果
     *
     * @param message 错误信息
     * @return Result
     */
    public static <T> Result<T> forbidden(String message) {
        return error(403, message);
    }
    
    /**
     * 资源不存在返回结果
     *
     * @param message 错误信息
     * @return Result
     */
    public static <T> Result<T> notFound(String message) {
        return error(404, message);
    }
}