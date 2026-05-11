package com.onlinecollege.subject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.onlinecollege.subject.entity.EduSubject;
import org.apache.ibatis.annotations.Mapper;

/**
 * 课程分类Mapper接口
 */
@Mapper
public interface SubjectMapper extends BaseMapper<EduSubject> {
}