package com.onlinecollege.auth.controller;

import com.onlinecollege.auth.service.AuthService;
import com.onlinecollege.auth.service.AuthService.LoginResult;
import com.onlinecollege.auth.service.AuthService.UserVo;
import com.onlinecollege.common.Result;
import com.onlinecollege.common.exception.BusinessException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * 认证相关接口
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "认证", description = "登录 / 当前用户 / 登出")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "登录")
    public Result<LoginResult> login(@Valid @RequestBody LoginRequest req) {
        return Result.success(authService.login(req.getUsername(), req.getPassword()));
    }

    /**
     * 通过网关转发后，当前登录用户 ID 会放在请求头 {@code X-User-Id} 里。
     * 这里读取并返回用户详情。
     */
    @GetMapping("/me")
    @Operation(summary = "当前登录用户")
    public Result<UserVo> me(@RequestHeader(value = "X-User-Id", required = false) String userId) {
        if (userId == null || userId.isBlank()) {
            throw BusinessException.unauthorized("未登录");
        }
        try {
            return Result.success(authService.me(Long.parseLong(userId)));
        } catch (NumberFormatException e) {
            throw BusinessException.unauthorized("X-User-Id 非法");
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "登出（无状态 JWT：前端删除 token 即可，本接口仅返回 200）")
    public Result<Void> logout() {
        return Result.success();
    }

    @Data
    public static class LoginRequest {
        @NotBlank(message = "用户名不能为空")
        private String username;
        @NotBlank(message = "密码不能为空")
        private String password;
    }
}
