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
 * 小节（二级菜单），视频挂在这里。
 */
@Data
@TableName("edu_video")
@Schema(description = "课程小节（带视频）")
public class EduVideo {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "小节 ID")
    private Long id;

    @TableField("course_id")
    @Schema(description = "所属课程 ID")
    private Long courseId;

    @TableField("chapter_id")
    @Schema(description = "所属章节 ID", required = true)
    private Long chapterId;

    @NotBlank(message = "小节标题不能为空")
    @Size(max = 128, message = "小节标题长度不能超过 128")
    @Schema(description = "小节标题", required = true)
    private String title;

    @PositiveOrZero
    @Schema(description = "小节排序")
    private Integer sort;

    @Schema(description = "阿里云 VOD 视频 ID，为 null 表示尚未绑定视频")
    @TableField("video_id")
    private String videoId;

    @Schema(description = "视频源地址（VOD 回源地址；可空）")
    @TableField("video_source_url")
    private String videoSourceUrl;

    @Schema(description = "视频时长（秒）")
    @TableField("video_duration")
    private Integer videoDuration;

    @Schema(description = "是否免费试看，0=否, 1=是")
    @TableField("is_free")
    private Integer isFree;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    @Schema(hidden = true)
    private Integer deleted;
}
