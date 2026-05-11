package com.onlinecollege.teacher.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.onlinecollege.teacher.entity.Teacher;
import com.onlinecollege.teacher.mapper.TeacherMapper;
import com.onlinecollege.teacher.service.TeacherService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

/**
 * 教师服务实现类
 */
@Service
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {
    
    @Override
    public boolean addTeacher(Teacher teacher) {
        // 验证必填字段
        if (!StringUtils.hasText(teacher.getTeacherName())) {
            throw new IllegalArgumentException("教师姓名不能为空");
        }
        if (!StringUtils.hasText(teacher.getTeacherNo())) {
            throw new IllegalArgumentException("教师工号不能为空");
        }
        
        // 设置默认值
        if (teacher.getStatus() == null) {
            teacher.setStatus(1); // 默认状态为启用
        }
        teacher.setCreateTime(LocalDate.now());
        teacher.setUpdateTime(LocalDate.now());
        
        return this.save(teacher);
    }
    
    @Override
    public boolean deleteTeacherById(Long id) {
        // 检查教师是否存在
        Teacher teacher = this.getById(id);
        if (teacher == null) {
            throw new IllegalArgumentException("教师不存在，ID: " + id);
        }
        
        return this.removeById(id);
    }
    
    @Override
    public boolean updateTeacherById(Teacher teacher) {
        if (teacher.getId() == null) {
            throw new IllegalArgumentException("教师ID不能为空");
        }
        
        // 检查教师是否存在
        Teacher existingTeacher = this.getById(teacher.getId());
        if (existingTeacher == null) {
            throw new IllegalArgumentException("教师不存在，ID: " + teacher.getId());
        }
        
        // 验证必填字段
        if (!StringUtils.hasText(teacher.getTeacherName())) {
            throw new IllegalArgumentException("教师姓名不能为空");
        }
        if (!StringUtils.hasText(teacher.getTeacherNo())) {
            throw new IllegalArgumentException("教师工号不能为空");
        }
        
        teacher.setUpdateTime(LocalDate.now());
        return this.updateById(teacher);
    }
    
    @Override
    public Teacher getTeacherById(Long id) {
        Teacher teacher = this.getById(id);
        if (teacher == null) {
            throw new IllegalArgumentException("教师不存在，ID: " + id);
        }
        return teacher;
    }
    
    @Override
    public Page<Teacher> getTeacherPage(Integer pageNum, Integer pageSize) {
        // 设置默认值
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        
        Page<Teacher> page = new Page<>(pageNum, pageSize);
        return this.page(page);
    }
    
    @Override
    public List<Teacher> getTeacherList(String teacherName, String department) {
        LambdaQueryWrapper<Teacher> queryWrapper = new LambdaQueryWrapper<>();
        
        // 按教师姓名模糊查询
        if (StringUtils.hasText(teacherName)) {
            queryWrapper.like(Teacher::getTeacherName, teacherName);
        }
        
        // 按所属院系精确查询
        if (StringUtils.hasText(department)) {
            queryWrapper.eq(Teacher::getDepartment, department);
        }
        
        // 按创建时间倒序排列
        queryWrapper.orderByDesc(Teacher::getCreateTime);
        
        return this.list(queryWrapper);
    }
}