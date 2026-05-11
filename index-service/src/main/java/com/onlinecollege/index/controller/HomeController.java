package com.onlinecollege.index.controller;

import com.onlinecollege.common.Result;
import com.onlinecollege.index.service.HomeService;
import com.onlinecollege.index.vo.HomeDataVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 首页聚合 API。
 *
 * <p>C 端直接调 {@code GET /home}，index-service 会用 Redis 缓存命中率优化。
 */
@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
@Tag(name = "首页数据")
public class HomeController {

    private final HomeService homeService;

    @GetMapping
    @Operation(summary = "首页聚合数据（banner + 热门课程 + 最新课程 + 推荐讲师）")
    public Result<HomeDataVo> getHome() {
        return Result.success(homeService.getHomeData());
    }

    @PostMapping("/cache/evict")
    @Operation(summary = "后台手动失效首页缓存")
    public Result<Void> evict() {
        homeService.invalidate();
        return Result.success();
    }
}
