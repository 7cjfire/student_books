package com.onlinecollege.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

@Data
@Schema(description = "章节表单")
public class ChapterDto {

    @NotBlank(message = "章节标题不能为空")
    @Size(max = 128)
    private String title;

    @PositiveOrZero
    private Integer sort;
}
