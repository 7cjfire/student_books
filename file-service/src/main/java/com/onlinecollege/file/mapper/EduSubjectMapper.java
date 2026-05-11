package com.onlinecollege.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.onlinecollege.file.entity.EduSubject;
import org.apache.ibatis.annotations.Mapper;

/**
 * 课程分类 Mapper（仅用于 Excel 导入写库）
 */
@Mapper
public interface EduSubjectMapper extends BaseMapper<EduSubject> {
}
