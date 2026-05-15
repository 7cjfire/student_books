package com.onlinecollege.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * 步骤 1：课程基本信息
 */
@Data
@Schema(description = "课程基本信息（步骤 1）")
public class CourseInfoDto {

    @NotBlank(message = "课程标题不能为空")
    @Size(max = 128, message = "课程标题长度不能超过 128")
    @Schema(description = "课程标题", required = true)
    private String title;

    @NotNull(message = "二级分类 subjectId 不能为空")
    @Schema(description = "二级分类 ID（edu_subject.id）", required = true)
    private Long subjectId;

    @NotNull(message = "一级分类 subjectParentId 不能为空")
    @Schema(description = "一级分类 ID（edu_subject.id，parentId=0 的节点）", required = true)
    private Long subjectParentId;

    @NotNull(message = "讲师 teacherId 不能为空")
    @Schema(description = "讲师 ID（teacher.id）", required = true)
    private Long teacherId;

    @DecimalMin(value = "0.00", message = "价格不能为负数")
    @Schema(description = "课程价格（0 表示免费）")
    private BigDecimal price;

    @Schema(description = "课程封面 URL（前置上传到 OSS 后传入）")
    private String cover;

    @Size(max = 1024, message = "课程简介长度不能超过 1024")
    @Schema(description = "课程简介")
    private String description;
}
