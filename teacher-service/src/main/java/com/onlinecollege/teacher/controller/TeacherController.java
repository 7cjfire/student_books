package com.onlinecollege.teacher.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.onlinecollege.common.Result;
import com.onlinecollege.teacher.entity.Teacher;
import com.onlinecollege.teacher.service.TeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 教师 REST 接口
 *
 * <p>直连：{@code http://localhost:8084/teacher-service/teacher/...}<br>
 * 经网关：{@code http://localhost:8080/api/teacher/...}（StripPrefix=1）
 */
@RestController
@RequestMapping("/teacher")
@RequiredArgsConstructor
@Tag(name = "教师管理", description = "教师管理相关接口")
public class TeacherController {

    private final TeacherService teacherService;

    @PostMapping("/add")
    @Operation(summary = "新增教师")
    public Result<Boolean> addTeacher(@Valid @RequestBody Teacher teacher) {
        return Result.success(teacherService.addTeacher(teacher));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "根据ID删除教师")
    public Result<Boolean> deleteTeacherById(
            @Parameter(description = "教师ID", required = true)
            @PathVariable Long id) {
        return Result.success(teacherService.deleteTeacherById(id));
    }

    @PutMapping("/update")
    @Operation(summary = "根据ID修改教师")
    public Result<Boolean> updateTeacherById(@Valid @RequestBody Teacher teacher) {
        return Result.success(teacherService.updateTeacherById(teacher));
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询教师")
    public Result<Teacher> getTeacherById(
            @Parameter(description = "教师ID", required = true)
            @PathVariable Long id) {
        return Result.success(teacherService.getTeacherById(id));
    }

    @GetMapping("/list")
    @Operation(summary = "分页查询教师列表")
    public Result<Page<Teacher>> getTeacherPage(
            @Parameter(description = "页码，默认1")
            @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小，默认10，最大100")
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(teacherService.getTeacherPage(pageNum, pageSize));
    }

    @GetMapping("/search")
    @Operation(summary = "条件查询教师列表")
    public Result<List<Teacher>> getTeacherList(
            @Parameter(description = "教师姓名（模糊查询）")
            @RequestParam(required = false) String teacherName,
            @Parameter(description = "所属院系（精确查询）")
            @RequestParam(required = false) String department) {
        return Result.success(teacherService.getTeacherList(teacherName, department));
    }
}
