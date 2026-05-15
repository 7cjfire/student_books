package com.onlinecollege.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.onlinecollege.common.exception.BusinessException;
import com.onlinecollege.course.dto.CourseInfoDto;
import com.onlinecollege.course.entity.EduChapter;
import com.onlinecollege.course.entity.EduCourse;
import com.onlinecollege.course.entity.EduVideo;
import com.onlinecollege.course.enums.CourseStatus;
import com.onlinecollege.course.mapper.EduChapterMapper;
import com.onlinecollege.course.mapper.EduCourseMapper;
import com.onlinecollege.course.mapper.EduVideoMapper;
import com.onlinecollege.course.service.CourseService;
import com.onlinecollege.course.vo.CourseDetailVo;
import com.onlinecollege.course.vo.CourseDetailVo.ChapterVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 课程服务实现：多步骤发布的核心逻辑。
 *
 * <p>每一步都会校验"当前状态是否允许这次操作"。如果不允许，直接抛 409 让前端去处理，
 * 不做隐式状态回退。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private static final int MAX_PAGE_SIZE = 100;

    private final EduCourseMapper courseMapper;
    private final EduChapterMapper chapterMapper;
    private final EduVideoMapper videoMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EduCourse saveStepInfo(CourseInfoDto dto) {
        EduCourse course = new EduCourse();
        copyInfo(dto, course);
        course.setStatus(CourseStatus.DRAFT);
        course.setLessonNum(0);
        course.setViewCount(0L);
        courseMapper.insert(course);
        log.info("创建课程草稿 id={}, title={}", course.getId(), course.getTitle());
        return course;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStepInfo(Long courseId, CourseInfoDto dto) {
        EduCourse course = requireCourse(courseId);
        if (isPublishedOrReviewing(course)) {
            throw BusinessException.conflict("课程已进入审核/发布状态，不能再修改基本信息");
        }
        copyInfo(dto, course);
        return courseMapper.updateById(course) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean advanceToVideo(Long courseId) {
        EduCourse course = requireCourse(courseId);
        if (!(CourseStatus.DRAFT.equals(course.getStatus())
                || CourseStatus.CHAPTER_PENDING.equals(course.getStatus()))) {
            throw BusinessException.conflict("当前状态不允许进入步骤 3：" + course.getStatus());
        }

        // 必须有章节
        List<EduChapter> chapters = chapterMapper.selectList(new LambdaQueryWrapper<EduChapter>()
                .eq(EduChapter::getCourseId, courseId));
        if (chapters.isEmpty()) {
            throw BusinessException.badRequest("课程必须至少包含 1 个章节");
        }
        // 每个章节必须有小节
        Set<Long> chapterIds = chapters.stream().map(EduChapter::getId).collect(Collectors.toSet());
        List<EduVideo> videos = videoMapper.selectList(new LambdaQueryWrapper<EduVideo>()
                .in(EduVideo::getChapterId, chapterIds));
        Set<Long> chaptersWithVideos = videos.stream().map(EduVideo::getChapterId).collect(Collectors.toSet());
        for (EduChapter c : chapters) {
            if (!chaptersWithVideos.contains(c.getId())) {
                throw BusinessException.badRequest("章节【" + c.getTitle() + "】没有小节，无法进入步骤 3");
            }
        }

        return transitTo(course, CourseStatus.VIDEO_PENDING);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean advanceToReview(Long courseId) {
        EduCourse course = requireCourse(courseId);
        if (!CourseStatus.VIDEO_PENDING.equals(course.getStatus())) {
            throw BusinessException.conflict("当前状态不允许提交审核：" + course.getStatus());
        }

        // 所有小节必须已经绑定 VOD videoId
        List<EduVideo> videos = videoMapper.selectList(new LambdaQueryWrapper<EduVideo>()
                .eq(EduVideo::getCourseId, courseId));
        if (videos.isEmpty()) {
            throw BusinessException.badRequest("课程尚无小节");
        }
        List<String> unboundTitles = videos.stream()
                .filter(v -> !StringUtils.hasText(v.getVideoId()))
                .map(EduVideo::getTitle)
                .toList();
        if (!unboundTitles.isEmpty()) {
            throw BusinessException.badRequest("以下小节还未绑定视频：" + unboundTitles);
        }

        // 冗余小节总数，供首页/列表展示
        course.setLessonNum(videos.size());
        courseMapper.updateById(course);

        return transitTo(course, CourseStatus.REVIEWING);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean finishReview(Long courseId, boolean publish) {
        EduCourse course = requireCourse(courseId);
        if (!CourseStatus.REVIEWING.equals(course.getStatus())) {
            throw BusinessException.conflict("当前状态不是审核中：" + course.getStatus());
        }
        return transitTo(course, publish ? CourseStatus.PUBLISHED : CourseStatus.UNPUBLISHED);
    }

    @Override
    public CourseDetailVo getCourseDetail(Long courseId) {
        EduCourse course = requireCourse(courseId);

        List<EduChapter> chapters = chapterMapper.selectList(new LambdaQueryWrapper<EduChapter>()
                .eq(EduChapter::getCourseId, courseId)
                .orderByAsc(EduChapter::getSort).orderByAsc(EduChapter::getId));

        List<EduVideo> videos = videoMapper.selectList(new LambdaQueryWrapper<EduVideo>()
                .eq(EduVideo::getCourseId, courseId)
                .orderByAsc(EduVideo::getSort).orderByAsc(EduVideo::getId));

        Map<Long, List<EduVideo>> byChapter = new HashMap<>();
        for (EduVideo v : videos) {
            byChapter.computeIfAbsent(v.getChapterId(), k -> new ArrayList<>()).add(v);
        }

        List<ChapterVo> chapterVos = chapters.stream().map(c -> {
            ChapterVo cv = new ChapterVo();
            cv.setId(c.getId());
            cv.setTitle(c.getTitle());
            cv.setSort(c.getSort());
            List<EduVideo> vs = byChapter.getOrDefault(c.getId(), List.of());
            vs.sort(Comparator.comparingInt(a -> a.getSort() == null ? 0 : a.getSort()));
            cv.setVideos(vs);
            return cv;
        }).toList();

        CourseDetailVo vo = new CourseDetailVo();
        vo.setCourse(course);
        vo.setChapters(chapterVos);
        return vo;
    }

    @Override
    public Page<EduCourse> page(Integer pageNum, Integer pageSize, String status) {
        int p  = pageNum  == null || pageNum  < 1 ? 1  : pageNum;
        int ps = pageSize == null || pageSize < 1 ? 10 : pageSize;
        if (ps > MAX_PAGE_SIZE) ps = MAX_PAGE_SIZE;
        Page<EduCourse> page = new Page<>(p, ps);
        LambdaQueryWrapper<EduCourse> w = new LambdaQueryWrapper<EduCourse>()
                .orderByDesc(EduCourse::getId);
        if (StringUtils.hasText(status)) {
            if (!CourseStatus.ALL.contains(status)) {
                throw BusinessException.badRequest("非法的 status：" + status);
            }
            w.eq(EduCourse::getStatus, status);
        }
        return courseMapper.selectPage(page, w);
    }

    @Override
    public EduCourse getById(Long courseId) {
        return requireCourse(courseId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteById(Long courseId) {
        EduCourse c = requireCourse(courseId);
        if (CourseStatus.PUBLISHED.equals(c.getStatus())) {
            throw BusinessException.conflict("已发布课程不能直接删除，请先下架");
        }
        // 级联（逻辑）删除章节与小节
        videoMapper.delete(new LambdaQueryWrapper<EduVideo>().eq(EduVideo::getCourseId, courseId));
        chapterMapper.delete(new LambdaQueryWrapper<EduChapter>().eq(EduChapter::getCourseId, courseId));
        return courseMapper.deleteById(courseId) > 0;
    }

    // ---- helpers ----

    private EduCourse requireCourse(Long courseId) {
        if (courseId == null) {
            throw BusinessException.badRequest("courseId 不能为空");
        }
        EduCourse c = courseMapper.selectById(courseId);
        if (c == null) {
            throw BusinessException.notFound("课程不存在: " + courseId);
        }
        return c;
    }

    private boolean isPublishedOrReviewing(EduCourse c) {
        return CourseStatus.PUBLISHED.equals(c.getStatus())
                || CourseStatus.REVIEWING.equals(c.getStatus());
    }

    private boolean transitTo(EduCourse course, String target) {
        if (!CourseStatus.canTransit(course.getStatus(), target)) {
            throw BusinessException.conflict(
                    "非法的状态迁移：" + course.getStatus() + " -> " + target);
        }
        course.setStatus(target);
        return courseMapper.updateById(course) > 0;
    }

    private void copyInfo(CourseInfoDto dto, EduCourse course) {
        course.setTitle(dto.getTitle());
        course.setSubjectId(dto.getSubjectId());
        course.setSubjectParentId(dto.getSubjectParentId());
        course.setTeacherId(dto.getTeacherId());
        course.setPrice(dto.getPrice());
        course.setCover(dto.getCover());
        course.setDescription(dto.getDescription());
    }
}
