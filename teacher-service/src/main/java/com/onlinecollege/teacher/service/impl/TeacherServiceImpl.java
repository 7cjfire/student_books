package com.onlinecollege.teacher.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.onlinecollege.common.exception.BusinessException;
import com.onlinecollege.teacher.entity.Teacher;
import com.onlinecollege.teacher.mapper.TeacherMapper;
import com.onlinecollege.teacher.service.TeacherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 教师服务实现类
 *
 * <p>业务校验一律抛 {@link BusinessException}，由 {@code GlobalExceptionHandler}
 * 统一转为 {@code Result<Void>}；基础字段校验（非空 / 长度 / 格式等）放在
 * {@link Teacher} 实体的 Bean Validation 注解上，由 {@code @Valid} 触发。
 */
@Slf4j
@Service
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {

    private static final int DEFAULT_PAGE_NUM = 1;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 100;

    @Override
    public boolean addTeacher(Teacher teacher) {
        // id 由 DB 自增，清理掉前端传入的值
        teacher.setId(null);
        if (teacher.getStatus() == null) {
            teacher.setStatus(1);
        }
        // createTime / updateTime 由 MetaObjectHandler 自动填充
        return this.save(teacher);
    }

    @Override
    public boolean deleteTeacherById(Long id) {
        if (id == null) {
            throw BusinessException.badRequest("教师 ID 不能为空");
        }
        if (this.getById(id) == null) {
            throw BusinessException.notFound("教师不存在，ID: " + id);
        }
        return this.removeById(id);
    }

    @Override
    public boolean updateTeacherById(Teacher teacher) {
        if (teacher.getId() == null) {
            throw BusinessException.badRequest("教师 ID 不能为空");
        }
        if (this.getById(teacher.getId()) == null) {
            throw BusinessException.notFound("教师不存在，ID: " + teacher.getId());
        }
        // 防御性：createTime 不允许被更新接口覆盖
        teacher.setCreateTime(null);
        return this.updateById(teacher);
    }

    @Override
    public Teacher getTeacherById(Long id) {
        if (id == null) {
            throw BusinessException.badRequest("教师 ID 不能为空");
        }
        Teacher teacher = this.getById(id);
        if (teacher == null) {
            throw BusinessException.notFound("教师不存在，ID: " + id);
        }
        return teacher;
    }

    @Override
    public Page<Teacher> getTeacherPage(Integer pageNum, Integer pageSize) {
        int safePageNum = (pageNum == null || pageNum < 1) ? DEFAULT_PAGE_NUM : pageNum;
        int safePageSize = (pageSize == null || pageSize < 1) ? DEFAULT_PAGE_SIZE : pageSize;
        if (safePageSize > MAX_PAGE_SIZE) {
            safePageSize = MAX_PAGE_SIZE;
        }
        Page<Teacher> page = new Page<>(safePageNum, safePageSize);
        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<Teacher>()
                .orderByDesc(Teacher::getId);
        return this.page(page, wrapper);
    }

    @Override
    public List<Teacher> getTeacherList(String teacherName, String department) {
        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(teacherName)) {
            wrapper.like(Teacher::getTeacherName, teacherName);
        }
        if (StringUtils.hasText(department)) {
            wrapper.eq(Teacher::getDepartment, department);
        }
        wrapper.orderByDesc(Teacher::getCreateTime);
        return this.list(wrapper);
    }
}
