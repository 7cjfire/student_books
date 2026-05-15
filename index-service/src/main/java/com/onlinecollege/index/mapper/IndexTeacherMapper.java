package com.onlinecollege.index.mapper;

import com.onlinecollege.index.vo.HomeTeacherItemVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 跨服务读 teacher-service 的表（教学简化）
 */
@Mapper
public interface IndexTeacherMapper {

    @Select("SELECT id, teacher_name AS teacherName, title, department " +
            "  FROM teacher " +
            " WHERE status = 1 " +
            " ORDER BY id DESC " +
            " LIMIT #{limit}")
    List<HomeTeacherItemVo> selectRecommendedTeachers(@Param("limit") int limit);
}
