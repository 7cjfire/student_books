package com.onlinecollege.teacher.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 教师实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("teacher")
@Schema(description = "教师实体")
public class Teacher {
    
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "教师ID", example = "1")
    private Long id;
    
    @TableField("teacher_name")
    @Schema(description = "教师姓名", example = "张三", required = true)
    private String teacherName;
    
    @Schema(description = "教师工号", example = "T2023001", required = true)
    private String teacherNo;
    
    @Schema(description = "性别", example = "男")
    private String gender;
    
    @Schema(description = "年龄", example = "35")
    private Integer age;
    
    @Schema(description = "职称", example = "教授")
    private String title;
    
    @Schema(description = "所属院系", example = "计算机学院")
    private String department;
    
    @Schema(description = "联系电话", example = "13800138000")
    private String phone;
    
    @Schema(description = "邮箱", example = "zhangsan@example.com")
    private String email;
    
    @Schema(description = "入职日期", example = "2023-01-01")
    private LocalDate hireDate;
    
    @Schema(description = "基本工资", example = "15000.00")
    private BigDecimal salary;
    
    @Schema(description = "状态", example = "1")
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDate createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDate updateTime;
}