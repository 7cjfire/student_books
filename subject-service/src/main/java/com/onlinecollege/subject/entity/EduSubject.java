package com.onlinecollege.subject.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
    
    @Schema(description = "类别名称", example = "计算机科学", required = true)
    private String title;
    
    @TableField("parent_id")
    @Schema(description = "父级ID（0表示一级分类）", example = "0")
    private Long parentId;
    
    @Schema(description = "排序字段", example = "0")
    private Integer sort;
    
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}