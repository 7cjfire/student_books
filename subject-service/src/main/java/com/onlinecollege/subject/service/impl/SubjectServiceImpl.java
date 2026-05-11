package com.onlinecollege.subject.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.onlinecollege.subject.entity.EduSubject;
import com.onlinecollege.subject.mapper.SubjectMapper;
import com.onlinecollege.subject.service.SubjectService;
import com.onlinecollege.subject.vo.SubjectTreeVo;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 课程分类服务实现类
 */
@Service
public class SubjectServiceImpl extends ServiceImpl<SubjectMapper, EduSubject> implements SubjectService {
    
    @Override
    public boolean addSubject(EduSubject subject) {
        // 验证必填字段
        if (!StringUtils.hasText(subject.getTitle())) {
            throw new IllegalArgumentException("分类名称不能为空");
        }
        
        // 设置默认值
        if (subject.getParentId() == null) {
            subject.setParentId(0L); // 默认为一级分类
        }
        if (subject.getSort() == null) {
            subject.setSort(0);
        }
        
        return this.save(subject);
    }
    
    @Override
    public boolean deleteSubjectById(Long id) {
        // 检查分类是否存在
        EduSubject subject = this.getById(id);
        if (subject == null) {
            throw new IllegalArgumentException("分类不存在，ID: " + id);
        }
        
        // 检查是否有子分类
        LambdaQueryWrapper<EduSubject> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EduSubject::getParentId, id);
        long childCount = this.count(queryWrapper);
        if (childCount > 0) {
            throw new IllegalArgumentException("该分类下有子分类，无法删除");
        }
        
        return this.removeById(id);
    }
    
    @Override
    public boolean updateSubjectById(EduSubject subject) {
        if (subject.getId() == null) {
            throw new IllegalArgumentException("分类ID不能为空");
        }
        
        // 检查分类是否存在
        EduSubject existingSubject = this.getById(subject.getId());
        if (existingSubject == null) {
            throw new IllegalArgumentException("分类不存在，ID: " + subject.getId());
        }
        
        // 验证必填字段
        if (!StringUtils.hasText(subject.getTitle())) {
            throw new IllegalArgumentException("分类名称不能为空");
        }
        
        return this.updateById(subject);
    }
    
    @Override
    public EduSubject getSubjectById(Long id) {
        EduSubject subject = this.getById(id);
        if (subject == null) {
            throw new IllegalArgumentException("分类不存在，ID: " + id);
        }
        return subject;
    }
    
    @Override
    public List<EduSubject> getFirstLevelSubjects() {
        LambdaQueryWrapper<EduSubject> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EduSubject::getParentId, 0);
        queryWrapper.orderByAsc(EduSubject::getSort);
        return this.list(queryWrapper);
    }
    
    @Override
    public List<EduSubject> getSubjectsByParentId(Long parentId) {
        LambdaQueryWrapper<EduSubject> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EduSubject::getParentId, parentId);
        queryWrapper.orderByAsc(EduSubject::getSort);
        return this.list(queryWrapper);
    }
    
    @Override
    public List<SubjectTreeVo> getSubjectTree() {
        // 查询所有分类
        List<EduSubject> allSubjects = this.list();
        
        // 构建ID到VO的映射
        Map<Long, SubjectTreeVo> voMap = new HashMap<>();
        for (EduSubject subject : allSubjects) {
            SubjectTreeVo vo = SubjectTreeVo.fromEntity(subject);
            voMap.put(subject.getId(), vo);
        }
        
        // 构建树形结构
        List<SubjectTreeVo> tree = new ArrayList<>();
        for (SubjectTreeVo vo : voMap.values()) {
            if (vo.getParentId() == 0) {
                // 一级分类
                tree.add(vo);
            } else {
                // 子分类，添加到父分类的children中
                SubjectTreeVo parentVo = voMap.get(vo.getParentId());
                if (parentVo != null) {
                    if (parentVo.getChildren() == null) {
                        parentVo.setChildren(new ArrayList<>());
                    }
                    parentVo.getChildren().add(vo);
                }
            }
        }
        
        // 对树进行排序
        tree.sort((a, b) -> a.getSort() - b.getSort());
        for (SubjectTreeVo vo : voMap.values()) {
            if (vo.getChildren() != null) {
                vo.getChildren().sort((a, b) -> a.getSort() - b.getSort());
            }
        }
        
        return tree;
    }
    
    @Override
    public List<EduSubject> getSubjectPath(Long id) {
        List<EduSubject> path = new ArrayList<>();
        EduSubject current = this.getById(id);
        
        if (current == null) {
            throw new IllegalArgumentException("分类不存在，ID: " + id);
        }
        
        // 添加当前分类
        path.add(current);
        
        // 递归添加父分类
        while (current.getParentId() != null && current.getParentId() != 0) {
            current = this.getById(current.getParentId());
            if (current != null) {
                path.add(0, current); // 添加到列表开头
            } else {
                break;
            }
        }
        
        return path;
    }
}