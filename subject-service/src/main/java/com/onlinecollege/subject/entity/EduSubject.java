package com.onlinecollege.subject.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 课程分类实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("edu_subject")
@Schema(description = "课程分类实体")
public class EduSubject {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "分类ID（雪花算法）", example = "1621234567890123456")
    private Long id;

    @NotBlank(message = "分类名称不能为空")
    @Size(max = 50, message = "分类名称长度不能超过 50")
    @Schema(description = "类别名称", example = "计算机科学", required = true)
    private String title;

    @TableField("parent_id")
    @Schema(description = "父级ID（0表示一级分类）", example = "0")
    private Long parentId;

    @PositiveOrZero(message = "排序必须为非负整数")
    @Schema(description = "排序字段", example = "0")
    private Integer sort;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updateTime;
}