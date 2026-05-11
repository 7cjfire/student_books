package com.onlinecollege.course.service;

import com.onlinecollege.course.dto.BindVideoDto;
import com.onlinecollege.course.dto.VideoDto;
import com.onlinecollege.course.entity.EduVideo;

import java.util.List;

public interface VideoService {

    EduVideo addVideo(Long courseId, VideoDto dto);

    boolean updateVideo(Long videoPk, VideoDto dto);

    /** 给"已有小节"绑定 VOD 视频 */
    boolean bindVodVideo(Long videoPk, BindVideoDto dto);

    boolean deleteVideo(Long videoPk);

    List<EduVideo> listByChapterId(Long chapterId);
}
