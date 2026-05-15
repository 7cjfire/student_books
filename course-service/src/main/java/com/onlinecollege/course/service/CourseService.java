package com.onlinecollege.course.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.onlinecollege.course.dto.CourseInfoDto;
import com.onlinecollege.course.entity.EduCourse;
import com.onlinecollege.course.vo.CourseDetailVo;

/**
 * 课程多步骤发布服务
 */
public interface CourseService {

    /** 步骤 1：新建课程（只写基本信息，状态 = DRAFT） */
    EduCourse saveStepInfo(CourseInfoDto dto);

    /** 步骤 1 的重提交：更新基本信息；status &lt;= DRAFT 才允许 */
    boolean updateStepInfo(Long courseId, CourseInfoDto dto);

    /**
     * 步骤 2：章节/小节结构完成后调用，将课程从 DRAFT/CHAPTER_PENDING 前进一步
     * 校验：至少 1 个章节、每个章节至少 1 个小节。
     */
    boolean advanceToVideo(Long courseId);

    /**
     * 步骤 3：视频全部上传绑定完成后调用
     * 校验：每个小节都已经绑定 videoId。
     */
    boolean advanceToReview(Long courseId);

    /**
     * 步骤 4：审核结果 —— 发布 or 驳回
     * @param publish true=PUBLISHED, false=UNPUBLISHED
     */
    boolean finishReview(Long courseId, boolean publish);

    /** 取课程完整结构（课程 + 章节 + 小节） */
    CourseDetailVo getCourseDetail(Long courseId);

    /** 简单分页列表（后台用） */
    Page<EduCourse> page(Integer pageNum, Integer pageSize, String status);

    EduCourse getById(Long courseId);

    boolean deleteById(Long courseId);
}
