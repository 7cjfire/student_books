package com.onlinecollege.teacher.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.onlinecollege.teacher.entity.Teacher;

import java.util.List;

/**
 * 教师服务接口
 */
public interface TeacherService extends IService<Teacher> {
    
    /**
     * 新增教师
     *
     * @param teacher 教师信息
     * @return 是否成功
     */
    boolean addTeacher(Teacher teacher);
    
    /**
     * 根据ID删除教师
     *
     * @param id 教师ID
     * @return 是否成功
     */
    boolean deleteTeacherById(Long id);
    
    /**
     * 根据ID修改教师
     *
     * @param teacher 教师信息
     * @return 是否成功
     */
    boolean updateTeacherById(Teacher teacher);
    
    /**
     * 根据ID查询教师
     *
     * @param id 教师ID
     * @return 教师信息
     */
    Teacher getTeacherById(Long id);
    
    /**
     * 分页查询教师列表
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    Page<Teacher> getTeacherPage(Integer pageNum, Integer pageSize);
    
    /**
     * 条件查询教师列表
     *
     * @param teacherName 教师姓名（模糊查询）
     * @param department  所属院系（精确查询）
     * @return 教师列表
     */
    List<Teacher> getTeacherList(String teacherName, String department);
}