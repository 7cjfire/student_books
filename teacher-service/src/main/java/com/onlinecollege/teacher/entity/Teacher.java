package com.onlinecollege.teacher.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
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

    @NotBlank(message = "教师姓名不能为空")
    @Size(max = 50, message = "教师姓名长度不能超过 50")
    @TableField("teacher_name")
    @Schema(description = "教师姓名", example = "张三", required = true)
    private String teacherName;

    @NotBlank(message = "教师工号不能为空")
    @Size(max = 30, message = "教师工号长度不能超过 30")
    @Schema(description = "教师工号", example = "T2023001", required = true)
    private String teacherNo;

    @Size(max = 10, message = "性别长度不能超过 10")
    @Schema(description = "性别", example = "男")
    private String gender;

    @Min(value = 18, message = "年龄不能小于 18")
    @Max(value = 100, message = "年龄不能大于 100")
    @Schema(description = "年龄", example = "35")
    private Integer age;

    @Size(max = 30, message = "职称长度不能超过 30")
    @Schema(description = "职称", example = "教授")
    private String title;

    @Size(max = 50, message = "所属院系长度不能超过 50")
    @Schema(description = "所属院系", example = "计算机学院")
    private String department;

    @Pattern(regexp = "^$|^1[3-9]\\d{9}$", message = "联系电话格式不正确")
    @Schema(description = "联系电话", example = "13800138000")
    private String phone;

    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过 100")
    @Schema(description = "邮箱", example = "zhangsan@example.com")
    private String email;

    @Schema(description = "入职日期", example = "2023-01-01")
    private LocalDate hireDate;

    @DecimalMin(value = "0.00", message = "基本工资不能为负数")
    @Schema(description = "基本工资", example = "15000.00")
    private BigDecimal salary;

    @Min(value = 0, message = "状态值非法")
    @Max(value = 1, message = "状态值非法")
    @Schema(description = "状态（0=禁用, 1=启用）", example = "1")
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDate createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDate updateTime;
}
