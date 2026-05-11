package com.onlinecollege.course.controller;

import com.onlinecollege.common.Result;
import com.onlinecollege.course.dto.ChapterDto;
import com.onlinecollege.course.entity.EduChapter;
import com.onlinecollege.course.service.ChapterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 章节（一级菜单）管理
 */
@RestController
@RequestMapping("/courses/{courseId}/chapters")
@RequiredArgsConstructor
@Tag(name = "课程章节", description = "章节 = 课程下的一级菜单")
public class ChapterController {

    private final ChapterService chapterService;

    @PostMapping
    @Operation(summary = "新增章节")
    public Result<EduChapter> add(@PathVariable Long courseId, @Valid @RequestBody ChapterDto dto) {
        return Result.success(chapterService.addChapter(courseId, dto));
    }

    @PutMapping("/{chapterId}")
    @Operation(summary = "修改章节标题/排序")
    public Result<Boolean> update(@PathVariable Long courseId,
                                  @PathVariable Long chapterId,
                                  @Valid @RequestBody ChapterDto dto) {
        return Result.success(chapterService.updateChapter(chapterId, dto));
    }

    @DeleteMapping("/{chapterId}")
    @Operation(summary = "删除章节（含该章节下所有小节）")
    public Result<Integer> delete(@PathVariable Long courseId, @PathVariable Long chapterId) {
        return Result.success(chapterService.deleteChapter(chapterId));
    }

    @GetMapping
    @Operation(summary = "查询课程的所有章节")
    public Result<List<EduChapter>> list(@PathVariable Long courseId) {
        return Result.success(chapterService.listByCourseId(courseId));
    }
}
