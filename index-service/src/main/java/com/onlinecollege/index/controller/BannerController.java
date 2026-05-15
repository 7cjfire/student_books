package com.onlinecollege.index.controller;

import com.onlinecollege.common.Result;
import com.onlinecollege.index.entity.EduBanner;
import com.onlinecollege.index.service.BannerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * banner 管理（图片通过 file-service 上传到 OSS 后，将返回的 URL 存入 imageUrl）
 */
@RestController
@RequestMapping("/banners")
@RequiredArgsConstructor
@Tag(name = "首页 Banner 管理")
public class BannerController {

    private final BannerService bannerService;

    @PostMapping
    @Operation(summary = "新增 banner")
    public Result<EduBanner> add(@Valid @RequestBody EduBanner banner) {
        return Result.success(bannerService.add(banner));
    }

    @PutMapping("/{id}")
    @Operation(summary = "修改 banner")
    public Result<Boolean> update(@PathVariable Long id, @Valid @RequestBody EduBanner banner) {
        return Result.success(bannerService.update(id, banner));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除 banner")
    public Result<Boolean> delete(@Parameter(description = "banner ID") @PathVariable Long id) {
        return Result.success(bannerService.delete(id));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询 banner")
    public Result<EduBanner> getById(@PathVariable Long id) {
        return Result.success(bannerService.getById(id));
    }

    @GetMapping
    @Operation(summary = "后台：所有 banner（含禁用 / 已过期）")
    public Result<List<EduBanner>> listAll() {
        return Result.success(bannerService.listAll());
    }

    @GetMapping("/active")
    @Operation(summary = "C 端：当前生效的 banner（带 Redis 缓存）")
    public Result<List<EduBanner>> listActive() {
        return Result.success(bannerService.listActiveWithCache());
    }
}
