package com.onlinecollege.teacher.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.onlinecollege.teacher.entity.Teacher;
import com.onlinecollege.teacher.service.TeacherService;
import com.onlinecollege.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 教师控制器
 */
@RestController
@RequestMapping("/teacher")
@Tag(name = "教师管理", description = "教师管理相关接口")
public class TeacherController {
    
    @Autowired
    private TeacherService teacherService;
    
    @PostMapping("/add")
    @Operation(summary = "新增教师")
    public Result<Boolean> addTeacher(@RequestBody Teacher teacher) {
        boolean result = teacherService.addTeacher(teacher);
        return Result.success(result);
    }
    
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "根据ID删除教师")
    public Result<Boolean> deleteTeacherById(
            @Parameter(description = "教师ID", required = true)
            @PathVariable Long id) {
        boolean result = teacherService.deleteTeacherById(id);
        return Result.success(result);
    }
    
    @PutMapping("/update")
    @Operation(summary = "根据ID修改教师")
    public Result<Boolean> updateTeacherById(@RequestBody Teacher teacher) {
        boolean result = teacherService.updateTeacherById(teacher);
        return Result.success(result);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询教师")
    public Result<Teacher> getTeacherById(
            @Parameter(description = "教师ID", required = true)
            @PathVariable Long id) {
        Teacher teacher = teacherService.getTeacherById(id);
        return Result.success(teacher);
    }
    
    @GetMapping("/list")
    @Operation(summary = "分页查询教师列表")
    public Result<Page<Teacher>> getTeacherPage(
            @Parameter(description = "页码，默认1")
            @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小，默认10")
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Teacher> page = teacherService.getTeacherPage(pageNum, pageSize);
        return Result.success(page);
    }
    
    @GetMapping("/search")
    @Operation(summary = "条件查询教师列表")
    public Result<List<Teacher>> getTeacherList(
            @Parameter(description = "教师姓名（模糊查询）")
            @RequestParam(required = false) String teacherName,
            @Parameter(description = "所属院系（精确查询）")
            @RequestParam(required = false) String department) {
        List<Teacher> teacherList = teacherService.getTeacherList(teacherName, department);
        return Result.success(teacherList);
    }
}