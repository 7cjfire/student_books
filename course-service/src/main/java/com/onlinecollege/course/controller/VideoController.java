package com.onlinecollege.course.controller;

import com.onlinecollege.common.Result;
import com.onlinecollege.course.dto.BindVideoDto;
import com.onlinecollege.course.dto.VideoDto;
import com.onlinecollege.course.entity.EduVideo;
import com.onlinecollege.course.service.VideoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 小节（视频）管理
 */
@RestController
@RequestMapping("/courses/{courseId}/videos")
@RequiredArgsConstructor
@Tag(name = "课程小节", description = "小节 = 章节下的二级菜单，挂阿里云 VOD 视频")
public class VideoController {

    private final VideoService videoService;

    @PostMapping
    @Operation(summary = "新增小节（可在新增时直接带 videoId；也可步骤 3 再绑定）")
    public Result<EduVideo> add(@PathVariable Long courseId, @Valid @RequestBody VideoDto dto) {
        return Result.success(videoService.addVideo(courseId, dto));
    }

    @PutMapping("/{videoPk}")
    @Operation(summary = "修改小节")
    public Result<Boolean> update(@PathVariable Long courseId,
                                  @PathVariable Long videoPk,
                                  @Valid @RequestBody VideoDto dto) {
        return Result.success(videoService.updateVideo(videoPk, dto));
    }

    @PostMapping("/{videoPk}/bind-vod")
    @Operation(summary = "步骤3：给已有小节绑定 VOD 视频")
    public Result<Boolean> bindVod(@PathVariable Long courseId,
                                   @PathVariable Long videoPk,
                                   @Valid @RequestBody BindVideoDto dto) {
        return Result.success(videoService.bindVodVideo(videoPk, dto));
    }

    @DeleteMapping("/{videoPk}")
    @Operation(summary = "删除小节")
    public Result<Boolean> delete(@PathVariable Long courseId, @PathVariable Long videoPk) {
        return Result.success(videoService.deleteVideo(videoPk));
    }

    @GetMapping("/by-chapter/{chapterId}")
    @Operation(summary = "按章节查小节列表")
    public Result<List<EduVideo>> listByChapter(@PathVariable Long courseId, @PathVariable Long chapterId) {
        return Result.success(videoService.listByChapterId(chapterId));
    }
}
