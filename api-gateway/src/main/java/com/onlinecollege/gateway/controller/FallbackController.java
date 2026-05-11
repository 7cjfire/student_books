package com.onlinecollege.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * 降级处理控制器
 *
 * <p>与 Spring Cloud Gateway 熔断过滤器 {@code fallbackUri: forward:/fallback/xxx} 配合，
 * 各下游服务不可用时返回 503 JSON 响应。
 *
 * <p>Gateway 的 forward 可能是 GET / POST / PUT / DELETE 中的任意一种（取决于原始请求），
 * 所以这里用 {@code @RequestMapping(method = {GET, POST, PUT, DELETE})} 全部接管。
 */
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @RequestMapping(value = "/book-service",
            method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public Mono<ResponseEntity<Map<String, Object>>> bookServiceFallback() {
        return serviceUnavailable("图书服务暂时不可用，请稍后重试");
    }

    @RequestMapping(value = "/teacher-service",
            method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public Mono<ResponseEntity<Map<String, Object>>> teacherServiceFallback() {
        return serviceUnavailable("教师服务暂时不可用，请稍后重试");
    }

    @RequestMapping(value = "/subject-service",
            method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public Mono<ResponseEntity<Map<String, Object>>> subjectServiceFallback() {
        return serviceUnavailable("课程分类服务暂时不可用，请稍后重试");
    }

    @RequestMapping(value = "/file-service",
            method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public Mono<ResponseEntity<Map<String, Object>>> fileServiceFallback() {
        return serviceUnavailable("文件服务暂时不可用，请稍后重试");
    }

    @RequestMapping(value = "/course-service",
            method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public Mono<ResponseEntity<Map<String, Object>>> courseServiceFallback() {
        return serviceUnavailable("课程服务暂时不可用，请稍后重试");
    }

    @RequestMapping(value = "/index-service",
            method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public Mono<ResponseEntity<Map<String, Object>>> indexServiceFallback() {
        return serviceUnavailable("首页服务暂时不可用，请稍后重试");
    }

    @RequestMapping(value = "/service-provider",
            method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public Mono<ResponseEntity<Map<String, Object>>> serviceProviderFallback() {
        return serviceUnavailable("服务提供者暂时不可用，请稍后重试");
    }

    @RequestMapping(value = "/service-consumer",
            method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public Mono<ResponseEntity<Map<String, Object>>> serviceConsumerFallback() {
        return serviceUnavailable("服务消费者暂时不可用，请稍后重试");
    }

    @RequestMapping(value = "/rate-limit",
            method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public Mono<ResponseEntity<Map<String, Object>>> rateLimitFallback() {
        Map<String, Object> body = buildBody(429, "请求过于频繁，请稍后重试");
        return Mono.just(ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body));
    }

    private static Mono<ResponseEntity<Map<String, Object>>> serviceUnavailable(String message) {
        Map<String, Object> body = buildBody(503, message);
        return Mono.just(ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body));
    }

    private static Map<String, Object> buildBody(int code, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("code", code);
        body.put("message", message);
        body.put("data", null);
        return body;
    }
}
