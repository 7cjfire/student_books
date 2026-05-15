package com.onlinecollege.course.service;

import com.onlinecollege.course.dto.ChapterDto;
import com.onlinecollege.course.entity.EduChapter;

import java.util.List;

public interface ChapterService {

    EduChapter addChapter(Long courseId, ChapterDto dto);

    boolean updateChapter(Long chapterId, ChapterDto dto);

    /**
     * 删除章节及其下所有小节。
     * @return 同时被删除的小节数
     */
    int deleteChapter(Long chapterId);

    List<EduChapter> listByCourseId(Long courseId);
}
