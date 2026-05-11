package com.onlinecollege.index.vo;

import com.onlinecollege.index.entity.EduBanner;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 首页聚合数据：banner + 热门课程 + 最新课程 + 推荐讲师
 */
@Data
@Schema(description = "首页聚合数据")
public class HomeDataVo {

    @Schema(description = "轮播图")
    private List<EduBanner> banners;

    @Schema(description = "热门课程（按浏览量）")
    private List<HomeCourseItemVo> hotCourses;

    @Schema(description = "最新发布课程")
    private List<HomeCourseItemVo> latestCourses;

    @Schema(description = "推荐讲师")
    private List<HomeTeacherItemVo> recommendedTeachers;
}
