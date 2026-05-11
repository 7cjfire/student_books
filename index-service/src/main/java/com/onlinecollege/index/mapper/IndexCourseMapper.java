package com.onlinecollege.index.mapper;

import com.onlinecollege.index.vo.HomeCourseItemVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * index-service 直接读 course-service 的 edu_course 表做首页聚合。
 *
 * <p>跨服务直连 DB 属于教学项目简化；严格按微服务边界应该走 Feign 调 course-service。
 */
@Mapper
public interface IndexCourseMapper {

    /** 热门课程（已发布，按浏览量倒序） */
    @Select("SELECT id, title, cover, price, lesson_num AS lessonNum, view_count AS viewCount, " +
            "       subject_id AS subjectId, subject_parent_id AS subjectParentId, teacher_id AS teacherId " +
            "  FROM edu_course " +
            " WHERE status = 'PUBLISHED' AND deleted = 0 " +
            " ORDER BY view_count DESC, id DESC " +
            " LIMIT #{limit}")
    List<HomeCourseItemVo> selectHotCourses(@Param("limit") int limit);

    /** 最新发布课程，按 ID 倒序（雪花算法下约等于按创建时间倒序） */
    @Select("SELECT id, title, cover, price, lesson_num AS lessonNum, view_count AS viewCount, " +
            "       subject_id AS subjectId, subject_parent_id AS subjectParentId, teacher_id AS teacherId " +
            "  FROM edu_course " +
            " WHERE status = 'PUBLISHED' AND deleted = 0 " +
            " ORDER BY id DESC " +
            " LIMIT #{limit}")
    List<HomeCourseItemVo> selectLatestCourses(@Param("limit") int limit);
}
