package com.onlinecollege.file.controller;

import com.onlinecollege.common.Result;
import com.onlinecollege.file.service.VodService;
import com.onlinecollege.file.service.VodService.PlayAuthResult;
import com.onlinecollege.file.service.VodService.UploadAuthResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 阿里云 VOD 凭证接口
 *
 * <p>说明：本服务不做视频直传，只负责把"上传地址 + 上传凭证"交给前端，由前端 JS SDK
 * 直连 VOD 完成上传，可参考阿里云官方 <i>Web 端上传 SDK</i> 文档。
 */
@RestController
@RequestMapping("/vod")
@RequiredArgsConstructor
@Tag(name = "阿里云 VOD 凭证", description = "获取上传凭证 / 刷新凭证 / 获取播放凭证")
public class VodController {

    private final VodService vodService;

    @PostMapping("/upload-auth")
    @Operation(summary = "申请视频上传凭证", description = "新视频上传时调用")
    public Result<UploadAuthResult> createUploadAuth(@Valid @RequestBody CreateUploadAuthRequest req) {
        return Result.success(
                vodService.createUploadVideo(req.getTitle(), req.getFileName(), req.getDescription(), req.getCateId()));
    }

    @PostMapping("/upload-auth/refresh")
    @Operation(summary = "刷新视频上传凭证", description = "原凭证过期（3000秒）后调用")
    public Result<UploadAuthResult> refreshUploadAuth(@Valid @RequestBody RefreshUploadAuthRequest req) {
        return Result.success(vodService.refreshUploadVideo(req.getVideoId()));
    }

    @GetMapping("/play-auth/{videoId}")
    @Operation(summary = "获取视频播放凭证", description = "播放器用 playAuth 向 VOD 请求播放 m3u8/mp4")
    public Result<PlayAuthResult> getPlayAuth(
            @Parameter(description = "VOD 视频 ID", required = true) @PathVariable String videoId) {
        return Result.success(vodService.getVideoPlayAuth(videoId));
    }

    @Data
    public static class CreateUploadAuthRequest {
        @NotBlank(message = "视频标题不能为空")
        @Size(max = 128, message = "视频标题长度不能超过 128")
        private String title;

        @NotBlank(message = "文件名不能为空")
        @Size(max = 128, message = "文件名长度不能超过 128")
        private String fileName;

        @Size(max = 1024, message = "描述长度不能超过 1024")
        private String description;

        /** 可选：VOD 分类 ID */
        private Long cateId;
    }

    @Data
    public static class RefreshUploadAuthRequest {
        @NotBlank(message = "videoId 不能为空")
        private String videoId;
    }
}
