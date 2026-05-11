package com.onlinecollege.subject.vo;

import com.onlinecollege.subject.entity.EduSubject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 课程分类树形结构VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "课程分类树形结构")
public class SubjectTreeVo extends EduSubject {
    
    @Schema(description = "子分类列表")
    private List<SubjectTreeVo> children;
    
    /**
     * 从实体类创建VO
     *
     * @param subject 实体类
     * @return VO对象
     */
    public static SubjectTreeVo fromEntity(EduSubject subject) {
        SubjectTreeVo vo = new SubjectTreeVo();
        vo.setId(subject.getId());
        vo.setTitle(subject.getTitle());
        vo.setParentId(subject.getParentId());
        vo.setSort(subject.getSort());
        vo.setCreateTime(subject.getCreateTime());
        vo.setUpdateTime(subject.getUpdateTime());
        return vo;
    }
}