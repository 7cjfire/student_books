package com.onlinecollege.subject.controller;

import com.onlinecollege.common.Result;
import com.onlinecollege.subject.entity.EduSubject;
import com.onlinecollege.subject.service.SubjectService;
import com.onlinecollege.subject.vo.SubjectTreeVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 课程分类 REST 接口
 *
 * <p>直连：{@code http://localhost:8085/subject-service/api/subjects/...}<br>
 * 经网关：{@code http://localhost:8080/api/subjects/...}（需在 api-gateway 中补路由）
 */
@RestController
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
@Tag(name = "课程分类管理", description = "课程分类 CRUD / 树形结构 / 路径查询")
public class SubjectController {

    private final SubjectService subjectService;

    @PostMapping
    @Operation(summary = "新增分类")
    public Result<Boolean> addSubject(@Valid @RequestBody EduSubject subject) {
        return Result.success(subjectService.addSubject(subject));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "根据 ID 删除分类")
    public Result<Boolean> deleteSubject(
            @Parameter(description = "分类 ID", required = true)
            @PathVariable Long id) {
        return Result.success(subjectService.deleteSubjectById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "根据 ID 修改分类")
    public Result<Boolean> updateSubject(
            @Parameter(description = "分类 ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody EduSubject subject) {
        subject.setId(id);
        return Result.success(subjectService.updateSubjectById(subject));
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据 ID 查询分类")
    public Result<EduSubject> getSubject(
            @Parameter(description = "分类 ID", required = true)
            @PathVariable Long id) {
        return Result.success(subjectService.getSubjectById(id));
    }

    @GetMapping("/first-level")
    @Operation(summary = "查询所有一级分类")
    public Result<List<EduSubject>> getFirstLevel() {
        return Result.success(subjectService.getFirstLevelSubjects());
    }

    @GetMapping("/children/{parentId}")
    @Operation(summary = "根据父级 ID 查询子分类")
    public Result<List<EduSubject>> getChildren(
            @Parameter(description = "父级 ID", required = true)
            @PathVariable Long parentId) {
        return Result.success(subjectService.getSubjectsByParentId(parentId));
    }

    @GetMapping("/tree")
    @Operation(summary = "查询分类树形结构")
    public Result<List<SubjectTreeVo>> getTree() {
        return Result.success(subjectService.getSubjectTree());
    }

    @GetMapping("/path/{id}")
    @Operation(summary = "根据分类 ID 查询完整路径（从根到当前）")
    public Result<List<EduSubject>> getPath(
            @Parameter(description = "分类 ID", required = true)
            @PathVariable Long id) {
        return Result.success(subjectService.getSubjectPath(id));
    }
}
