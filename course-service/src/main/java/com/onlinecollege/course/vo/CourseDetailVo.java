package com.onlinecollege.course.vo;

import com.onlinecollege.course.entity.EduCourse;
import com.onlinecollege.course.entity.EduVideo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 课程完整结构（课程 + 章节 + 小节），用于"多步骤"中的查看当前草稿。
 */
@Data
@Schema(description = "课程详情（含章节树）")
public class CourseDetailVo {

    @Schema(description = "课程基本信息")
    private EduCourse course;

    @Schema(description = "章节列表（每个章节内含小节）")
    private List<ChapterVo> chapters;

    @Data
    @Schema(description = "章节 + 小节")
    public static class ChapterVo {
        private Long id;
        private String title;
        private Integer sort;
        private List<EduVideo> videos;
    }
}
