package com.onlinecollege.file.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 课程分类实体（本模块用于 Excel 批量导入写入）。
 *
 * <p>对应 {@code subject-service} 的 {@code EduSubject}，字段保持一致；
 * 只做插入不暴露 HTTP CRUD，CRUD 入口在 subject-service 里。
 */
@Data
@TableName("edu_subject")
public class EduSubject {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    private String title;

    @TableField("parent_id")
    private Long parentId;

    private Integer sort;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
