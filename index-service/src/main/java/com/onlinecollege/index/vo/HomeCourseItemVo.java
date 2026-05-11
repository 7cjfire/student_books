package com.onlinecollege.index.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class HomeCourseItemVo {
    private Long id;
    private String title;
    private String cover;
    private BigDecimal price;
    private Integer lessonNum;
    private Long viewCount;
    private Long subjectId;
    private Long subjectParentId;
    private Long teacherId;
}
