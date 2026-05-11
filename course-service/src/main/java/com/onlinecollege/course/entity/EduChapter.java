package com.onlinecollege.course.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 课程章节（一级菜单）
 */
@Data
@TableName("edu_chapter")
@Schema(description = "课程章节")
public class EduChapter {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "章节 ID")
    private Long id;

    @TableField("course_id")
    @Schema(description = "所属课程 ID", required = true)
    private Long courseId;

    @NotBlank(message = "章节标题不能为空")
    @Size(max = 128, message = "章节标题长度不能超过 128")
    @Schema(description = "章节标题", required = true)
    private String title;

    @PositiveOrZero
    @Schema(description = "章节排序")
    private Integer sort;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    @Schema(hidden = true)
    private Integer deleted;
}
