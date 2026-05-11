package com.onlinecollege.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;

/**
 * 步骤 3：给"已有小节"绑定 VOD 视频（前端上传完成后回传 videoId）
 */
@Data
@Schema(description = "绑定 VOD 视频")
public class BindVideoDto {

    @NotBlank(message = "videoId 不能为空")
    private String videoId;

    @PositiveOrZero
    private Integer videoDuration;

    @Schema(description = "视频源地址，可空（由 VOD 回调或前端提供）")
    private String videoSourceUrl;
}
