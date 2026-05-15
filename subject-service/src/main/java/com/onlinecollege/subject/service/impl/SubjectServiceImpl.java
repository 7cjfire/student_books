package com.onlinecollege.subject.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.onlinecollege.common.exception.BusinessException;
import com.onlinecollege.subject.entity.EduSubject;
import com.onlinecollege.subject.mapper.SubjectMapper;
import com.onlinecollege.subject.service.SubjectService;
import com.onlinecollege.subject.vo.SubjectTreeVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 课程分类服务实现类
 */
@Slf4j
@Service
public class SubjectServiceImpl extends ServiceImpl<SubjectMapper, EduSubject> implements SubjectService {

    @Override
    public boolean addSubject(EduSubject subject) {
        if (!StringUtils.hasText(subject.getTitle())) {
            throw BusinessException.badRequest("分类名称不能为空");
        }

        // 设置默认值
        if (subject.getParentId() == null) {
            subject.setParentId(0L);
        }
        if (subject.getSort() == null) {
            subject.setSort(0);
        }

        // 二级分类时，校验父节点存在
        if (subject.getParentId() != 0L && this.getById(subject.getParentId()) == null) {
            throw BusinessException.badRequest("父分类不存在，parentId: " + subject.getParentId());
        }

        return this.save(subject);
    }

    @Override
    public boolean deleteSubjectById(Long id) {
        EduSubject subject = this.getById(id);
        if (subject == null) {
            throw BusinessException.notFound("分类不存在，ID: " + id);
        }

        // 检查是否有子分类
        LambdaQueryWrapper<EduSubject> queryWrapper = new LambdaQueryWrapper<EduSubject>()
                .eq(EduSubject::getParentId, id);
        long childCount = this.count(queryWrapper);
        if (childCount > 0) {
            throw BusinessException.conflict("该分类下有 " + childCount + " 个子分类，无法删除");
        }

        return this.removeById(id);
    }

    @Override
    public boolean updateSubjectById(EduSubject subject) {
        if (subject.getId() == null) {
            throw BusinessException.badRequest("分类ID不能为空");
        }
        if (this.getById(subject.getId()) == null) {
            throw BusinessException.notFound("分类不存在，ID: " + subject.getId());
        }
        if (!StringUtils.hasText(subject.getTitle())) {
            throw BusinessException.badRequest("分类名称不能为空");
        }
        return this.updateById(subject);
    }

    @Override
    public EduSubject getSubjectById(Long id) {
        EduSubject subject = this.getById(id);
        if (subject == null) {
            throw BusinessException.notFound("分类不存在，ID: " + id);
        }
        return subject;
    }

    @Override
    public List<EduSubject> getFirstLevelSubjects() {
        LambdaQueryWrapper<EduSubject> queryWrapper = new LambdaQueryWrapper<EduSubject>()
                .eq(EduSubject::getParentId, 0)
                .orderByAsc(EduSubject::getSort);
        return this.list(queryWrapper);
    }

    @Override
    public List<EduSubject> getSubjectsByParentId(Long parentId) {
        if (parentId == null) {
            throw BusinessException.badRequest("父分类ID不能为空");
        }
        LambdaQueryWrapper<EduSubject> queryWrapper = new LambdaQueryWrapper<EduSubject>()
                .eq(EduSubject::getParentId, parentId)
                .orderByAsc(EduSubject::getSort);
        return this.list(queryWrapper);
    }

    @Override
    public List<SubjectTreeVo> getSubjectTree() {
        List<EduSubject> allSubjects = this.list();

        Map<Long, SubjectTreeVo> voMap = new HashMap<>(allSubjects.size() * 2);
        for (EduSubject subject : allSubjects) {
            voMap.put(subject.getId(), SubjectTreeVo.fromEntity(subject));
        }

        List<SubjectTreeVo> tree = new ArrayList<>();
        for (SubjectTreeVo vo : voMap.values()) {
            if (vo.getParentId() == null || vo.getParentId() == 0L) {
                tree.add(vo);
            } else {
                SubjectTreeVo parentVo = voMap.get(vo.getParentId());
                if (parentVo != null) {
                    if (parentVo.getChildren() == null) {
                        parentVo.setChildren(new ArrayList<>());
                    }
                    parentVo.getChildren().add(vo);
                } else {
                    // 父节点缺失（数据不一致），作为顶层展示并记日志
                    log.warn("分类 id={} 的父节点 parentId={} 不存在，作为顶层展示", vo.getId(), vo.getParentId());
                    tree.add(vo);
                }
            }
        }

        Comparator<SubjectTreeVo> bySort = Comparator.comparingInt(v -> v.getSort() == null ? 0 : v.getSort());
        tree.sort(bySort);
        for (SubjectTreeVo vo : voMap.values()) {
            if (vo.getChildren() != null) {
                vo.getChildren().sort(bySort);
            }
        }
        return tree;
    }

    @Override
    public List<EduSubject> getSubjectPath(Long id) {
        EduSubject current = this.getById(id);
        if (current == null) {
            throw BusinessException.notFound("分类不存在，ID: " + id);
        }

        List<EduSubject> path = new ArrayList<>();
        path.add(current);

        // 防御性：最多向上追 10 层，避免数据异常导致死循环
        int guard = 10;
        while (current.getParentId() != null && current.getParentId() != 0L && guard-- > 0) {
            EduSubject parent = this.getById(current.getParentId());
            if (parent == null) {
                break;
            }
            path.add(0, parent);
            current = parent;
        }
        return path;
    }
}
