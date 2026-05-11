package com.onlinecollege.index.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 首页 banner（轮播图）
 *
 * <p>图片 URL 由 file-service 的 OSS 上传接口返回后填入 {@link #imageUrl}。
 */
@Data
@TableName("edu_banner")
@Schema(description = "首页 banner")
public class EduBanner {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "banner ID")
    private Long id;

    @NotBlank(message = "标题不能为空")
    @Size(max = 128)
    @Schema(description = "标题")
    private String title;

    @NotBlank(message = "图片 URL 不能为空")
    @Size(max = 512)
    @TableField("image_url")
    @Schema(description = "图片 URL（OSS 地址）", required = true)
    private String imageUrl;

    @Size(max = 512)
    @TableField("link_url")
    @Schema(description = "点击跳转 URL")
    private String linkUrl;

    @PositiveOrZero
    @Schema(description = "排序，越小越靠前")
    private Integer sort;

    @Min(0) @Max(1)
    @Schema(description = "是否启用：0=否 1=是")
    private Integer enabled;

    @TableField("start_time")
    @Schema(description = "生效开始时间（为空则立即生效）")
    private LocalDateTime startTime;

    @TableField("end_time")
    @Schema(description = "生效结束时间（为空则永久生效）")
    private LocalDateTime endTime;

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
