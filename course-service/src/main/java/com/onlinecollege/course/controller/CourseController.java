package com.onlinecollege.course.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.onlinecollege.common.Result;
import com.onlinecollege.course.client.FileServiceClient;
import com.onlinecollege.course.dto.CourseInfoDto;
import com.onlinecollege.course.entity.EduCourse;
import com.onlinecollege.course.service.CourseService;
import com.onlinecollege.course.vo.CourseDetailVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

/**
 * 课程主表 + 多步骤发布 REST
 *
 * <p>直连：{@code http://localhost:9090/course-service/courses/...}
 * 经网关：{@code http://localhost:8080/api/course/...}
 */
@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
@Tag(name = "课程管理", description = "课程 CRUD + 多步骤发布")
public class CourseController {

    private final CourseService courseService;
    private final FileServiceClient fileServiceClient;

    // ------- 多步骤发布 -------

    @PostMapping("/step-info")
    @Operation(summary = "步骤1 创建：保存课程基本信息（状态=DRAFT）")
    public Result<EduCourse> step1Create(@Valid @RequestBody CourseInfoDto dto) {
        return Result.success(courseService.saveStepInfo(dto));
    }

    @PutMapping("/{id}/step-info")
    @Operation(summary = "步骤1 更新：修改课程基本信息")
    public Result<Boolean> step1Update(@PathVariable Long id, @Valid @RequestBody CourseInfoDto dto) {
        return Result.success(courseService.updateStepInfo(id, dto));
    }

    @PostMapping("/{id}/step-advance/video")
    @Operation(summary = "步骤2→3：章节结构完成，推进到视频绑定阶段")
    public Result<Boolean> step2Done(@PathVariable Long id) {
        return Result.success(courseService.advanceToVideo(id));
    }

    @PostMapping("/{id}/step-advance/review")
    @Operation(summary = "步骤3→4：所有视频绑定完成，提交审核")
    public Result<Boolean> step3Done(@PathVariable Long id) {
        return Result.success(courseService.advanceToReview(id));
    }

    @PostMapping("/{id}/review")
    @Operation(summary = "步骤4：审核 publish=true 通过(PUBLISHED) / false 驳回(UNPUBLISHED)")
    public Result<Boolean> step4Review(
            @PathVariable Long id,
            @Parameter(description = "true=发布, false=驳回")
            @RequestParam boolean publish) {
        return Result.success(courseService.finishReview(id, publish));
    }

    // ------- 查询 / 管理 -------

    @GetMapping("/{id}")
    @Operation(summary = "根据 ID 查询课程（基础信息）")
    public Result<EduCourse> getById(@PathVariable Long id) {
        return Result.success(courseService.getById(id));
    }

    @GetMapping("/{id}/detail")
    @Operation(summary = "课程完整结构（课程 + 章节 + 小节）")
    public Result<CourseDetailVo> getDetail(@PathVariable Long id) {
        return Result.success(courseService.getCourseDetail(id));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询课程（可按 status 过滤）")
    public Result<Page<EduCourse>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String status) {
        return Result.success(courseService.page(pageNum, pageSize, status));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "根据 ID 删除课程（已发布课程需先下架）")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(courseService.deleteById(id));
    }

    // ------- 播放凭证中转（方便前端只对接 course-service） -------

    @GetMapping("/videos/{videoId}/play-auth")
    @Operation(summary = "获取视频播放凭证（经 Feign 调 file-service）")
    public Result<Map<String, Object>> getPlayAuth(@PathVariable String videoId) {
        Result<Map<String, Object>> r = fileServiceClient.getPlayAuth(videoId);
        return r;
    }
}
