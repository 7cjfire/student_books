package com.onlinecollege.subject.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.onlinecollege.subject.entity.EduSubject;
import com.onlinecollege.subject.vo.SubjectTreeVo;

import java.util.List;

/**
 * 课程分类服务接口
 */
public interface SubjectService extends IService<EduSubject> {
    
    /**
     * 新增分类
     *
     * @param subject 分类信息
     * @return 是否成功
     */
    boolean addSubject(EduSubject subject);
    
    /**
     * 根据ID删除分类
     *
     * @param id 分类ID
     * @return 是否成功
     */
    boolean deleteSubjectById(Long id);
    
    /**
     * 根据ID修改分类
     *
     * @param subject 分类信息
     * @return 是否成功
     */
    boolean updateSubjectById(EduSubject subject);
    
    /**
     * 根据ID查询分类
     *
     * @param id 分类ID
     * @return 分类信息
     */
    EduSubject getSubjectById(Long id);
    
    /**
     * 查询所有一级分类
     *
     * @return 一级分类列表
     */
    List<EduSubject> getFirstLevelSubjects();
    
    /**
     * 根据父级ID查询子分类
     *
     * @param parentId 父级ID
     * @return 子分类列表
     */
    List<EduSubject> getSubjectsByParentId(Long parentId);
    
    /**
     * 查询分类树形结构
     *
     * @return 分类树形结构
     */
    List<SubjectTreeVo> getSubjectTree();
    
    /**
     * 根据分类ID查询完整路径
     *
     * @param id 分类ID
     * @return 分类路径列表
     */
    List<EduSubject> getSubjectPath(Long id);
}