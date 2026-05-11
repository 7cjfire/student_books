package com.onlinecollege.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * 降级处理控制器
 */
@RestController
@RequestMapping("/fallback")
public class FallbackController {
    
    @GetMapping("/book-service")
    public Mono<ResponseEntity<Map<String, Object>>> bookServiceFallback() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 503);
        result.put("message", "图书服务暂时不可用，请稍后重试");
        result.put("data", null);
        
        return Mono.just(ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(result));
    }
    
    @GetMapping("/service-provider")
    public Mono<ResponseEntity<Map<String, Object>>> serviceProviderFallback() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 503);
        result.put("message", "服务提供者暂时不可用，请稍后重试");
        result.put("data", null);
        
        return Mono.just(ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(result));
    }
    
    @GetMapping("/service-consumer")
    public Mono<ResponseEntity<Map<String, Object>>> serviceConsumerFallback() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 503);
        result.put("message", "服务消费者暂时不可用，请稍后重试");
        result.put("data", null);
        
        return Mono.just(ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(result));
    }
    
    @GetMapping("/rate-limit")
    public Mono<ResponseEntity<Map<String, Object>>> rateLimitFallback() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 429);
        result.put("message", "请求过于频繁，请稍后重试");
        result.put("data", null);
        
        return Mono.just(ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .contentType(MediaType.APPLICATION_JSON)
                .body(result));
    }
}