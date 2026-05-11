package com.onlinecollege.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.onlinecollege.common.exception.BusinessException;
import com.onlinecollege.course.dto.ChapterDto;
import com.onlinecollege.course.entity.EduChapter;
import com.onlinecollege.course.entity.EduVideo;
import com.onlinecollege.course.mapper.EduChapterMapper;
import com.onlinecollege.course.mapper.EduVideoMapper;
import com.onlinecollege.course.service.ChapterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChapterServiceImpl implements ChapterService {

    private final EduChapterMapper chapterMapper;
    private final EduVideoMapper videoMapper;

    @Override
    public EduChapter addChapter(Long courseId, ChapterDto dto) {
        EduChapter chapter = new EduChapter();
        chapter.setCourseId(courseId);
        chapter.setTitle(dto.getTitle());
        chapter.setSort(dto.getSort() == null ? 0 : dto.getSort());
        chapterMapper.insert(chapter);
        return chapter;
    }

    @Override
    public boolean updateChapter(Long chapterId, ChapterDto dto) {
        EduChapter existed = chapterMapper.selectById(chapterId);
        if (existed == null) {
            throw BusinessException.notFound("章节不存在: " + chapterId);
        }
        existed.setTitle(dto.getTitle());
        if (dto.getSort() != null) {
            existed.setSort(dto.getSort());
        }
        return chapterMapper.updateById(existed) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteChapter(Long chapterId) {
        EduChapter existed = chapterMapper.selectById(chapterId);
        if (existed == null) {
            throw BusinessException.notFound("章节不存在: " + chapterId);
        }
        // 级联（逻辑）删除小节
        long videoCount = videoMapper.delete(new LambdaQueryWrapper<EduVideo>()
                .eq(EduVideo::getChapterId, chapterId));
        chapterMapper.deleteById(chapterId);
        log.info("删除章节 {} 及其 {} 个小节", chapterId, videoCount);
        return (int) videoCount;
    }

    @Override
    public List<EduChapter> listByCourseId(Long courseId) {
        return chapterMapper.selectList(new LambdaQueryWrapper<EduChapter>()
                .eq(EduChapter::getCourseId, courseId)
                .orderByAsc(EduChapter::getSort)
                .orderByAsc(EduChapter::getId));
    }
}
