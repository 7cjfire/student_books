package com.onlinecollege.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.onlinecollege.common.exception.BusinessException;
import com.onlinecollege.course.dto.BindVideoDto;
import com.onlinecollege.course.dto.VideoDto;
import com.onlinecollege.course.entity.EduChapter;
import com.onlinecollege.course.entity.EduVideo;
import com.onlinecollege.course.mapper.EduChapterMapper;
import com.onlinecollege.course.mapper.EduVideoMapper;
import com.onlinecollege.course.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {

    private final EduVideoMapper videoMapper;
    private final EduChapterMapper chapterMapper;

    @Override
    public EduVideo addVideo(Long courseId, VideoDto dto) {
        EduChapter chapter = chapterMapper.selectById(dto.getChapterId());
        if (chapter == null) {
            throw BusinessException.badRequest("章节不存在: " + dto.getChapterId());
        }
        if (!chapter.getCourseId().equals(courseId)) {
            throw BusinessException.badRequest("章节不属于当前课程");
        }

        EduVideo v = new EduVideo();
        v.setCourseId(courseId);
        v.setChapterId(dto.getChapterId());
        v.setTitle(dto.getTitle());
        v.setSort(dto.getSort() == null ? 0 : dto.getSort());
        v.setVideoId(dto.getVideoId());
        v.setVideoDuration(dto.getVideoDuration());
        v.setIsFree(dto.getIsFree() == null ? 0 : dto.getIsFree());
        videoMapper.insert(v);
        return v;
    }

    @Override
    public boolean updateVideo(Long videoPk, VideoDto dto) {
        EduVideo existed = videoMapper.selectById(videoPk);
        if (existed == null) {
            throw BusinessException.notFound("小节不存在: " + videoPk);
        }
        existed.setTitle(dto.getTitle());
        if (dto.getSort() != null)          existed.setSort(dto.getSort());
        if (dto.getVideoId() != null)       existed.setVideoId(dto.getVideoId());
        if (dto.getVideoDuration() != null) existed.setVideoDuration(dto.getVideoDuration());
        if (dto.getIsFree() != null)        existed.setIsFree(dto.getIsFree());
        return videoMapper.updateById(existed) > 0;
    }

    @Override
    public boolean bindVodVideo(Long videoPk, BindVideoDto dto) {
        EduVideo existed = videoMapper.selectById(videoPk);
        if (existed == null) {
            throw BusinessException.notFound("小节不存在: " + videoPk);
        }
        existed.setVideoId(dto.getVideoId());
        if (dto.getVideoDuration() != null) {
            existed.setVideoDuration(dto.getVideoDuration());
        }
        if (dto.getVideoSourceUrl() != null) {
            existed.setVideoSourceUrl(dto.getVideoSourceUrl());
        }
        return videoMapper.updateById(existed) > 0;
    }

    @Override
    public boolean deleteVideo(Long videoPk) {
        if (videoMapper.selectById(videoPk) == null) {
            throw BusinessException.notFound("小节不存在: " + videoPk);
        }
        return videoMapper.deleteById(videoPk) > 0;
    }

    @Override
    public List<EduVideo> listByChapterId(Long chapterId) {
        return videoMapper.selectList(new LambdaQueryWrapper<EduVideo>()
                .eq(EduVideo::getChapterId, chapterId)
                .orderByAsc(EduVideo::getSort)
                .orderByAsc(EduVideo::getId));
    }
}
