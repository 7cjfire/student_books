package com.onlinecollege.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

@Data
@Schema(description = "小节（视频）表单")
public class VideoDto {

    @NotNull(message = "chapterId 不能为空")
    @Schema(description = "所属章节 ID", required = true)
    private Long chapterId;

    @NotBlank(message = "小节标题不能为空")
    @Size(max = 128)
    private String title;

    @PositiveOrZero
    private Integer sort;

    @Size(max = 128)
    @Schema(description = "阿里云 VOD 视频 ID；如果步骤 3 还没上传可暂为空")
    private String videoId;

    @PositiveOrZero
    @Schema(description = "视频时长（秒）")
    private Integer videoDuration;

    @Min(0) @Max(1)
    @Schema(description = "是否免费试看：0=否, 1=是")
    private Integer isFree;
}
