package com.onlinecollege.course.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.onlinecollege.course.enums.CourseStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程主表实体
 *
 * <p>状态字段 {@link #status} 用字符串存放 {@link CourseStatus}，
 * 因为字符串状态在 MySQL 查询日志里更易读，且 MyBatis-Plus 对 String 枚举支持原生良好。
 */
@Data
@TableName("edu_course")
@Schema(description = "课程")
public class EduCourse {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "课程 ID（雪花算法）")
    private Long id;

    @NotBlank(message = "课程标题不能为空")
    @Size(max = 128, message = "课程标题长度不能超过 128")
    @Schema(description = "课程标题", required = true)
    private String title;

    @Schema(description = "课程封面 URL（OSS）")
    private String cover;

    @Schema(description = "所属分类（二级分类 ID），对应 edu_subject.id")
    @TableField("subject_id")
    private Long subjectId;

    @Schema(description = "一级分类 ID（冗余，便于首页按大类聚合）")
    @TableField("subject_parent_id")
    private Long subjectParentId;

    @Schema(description = "讲师 ID，对应 teacher.id")
    @TableField("teacher_id")
    private Long teacherId;

    @DecimalMin(value = "0.00", message = "价格不能为负数")
    @Schema(description = "课程价格")
    private BigDecimal price;

    @Schema(description = "小节总数（发布前冗余统计，供首页展示）")
    @TableField("lesson_num")
    private Integer lessonNum;

    @Size(max = 1024)
    @Schema(description = "课程简介")
    private String description;

    @Schema(description = "课程状态，见 CourseStatus 枚举")
    private String status;

    @Schema(description = "浏览次数（首页热门课程排序用）")
    @TableField("view_count")
    private Long viewCount;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updateTime;

    @TableLogic
    @Schema(hidden = true)
    private Integer deleted;
}
