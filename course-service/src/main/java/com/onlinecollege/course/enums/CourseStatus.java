package com.onlinecollege.course.enums;

import java.util.Set;

/**
 * 课程多步骤发布状态机
 *
 * <pre>
 *      DRAFT
 *        |  (step1: 基本信息完成)
 *        v
 *     CHAPTER_PENDING   (step2: 章节/小节结构搭建)
 *        |  (每一小节都关联了 videoId)
 *        v
 *     VIDEO_PENDING     (step3: 视频上传/绑定完成)
 *        |  (step4: 填课程简介/封面 + 提交审核)
 *        v
 *     REVIEWING
 *        |
 *        +--> PUBLISHED    (审核通过)
 *        \--> UNPUBLISHED  (审核驳回，可回到 DRAFT)
 * </pre>
 */
public final class CourseStatus {

    public static final String DRAFT            = "DRAFT";
    public static final String CHAPTER_PENDING  = "CHAPTER_PENDING";
    public static final String VIDEO_PENDING    = "VIDEO_PENDING";
    public static final String REVIEWING        = "REVIEWING";
    public static final String PUBLISHED        = "PUBLISHED";
    public static final String UNPUBLISHED      = "UNPUBLISHED";

    /** 允许从当前状态合法跳转到的下一步状态 */
    public static final Set<String> ALL = Set.of(
            DRAFT, CHAPTER_PENDING, VIDEO_PENDING, REVIEWING, PUBLISHED, UNPUBLISHED);

    private CourseStatus() {}

    /** 判断是否允许某个状态迁移 */
    public static boolean canTransit(String from, String to) {
        if (from == null || to == null || !ALL.contains(to)) {
            return false;
        }
        return switch (from) {
            case DRAFT           -> CHAPTER_PENDING.equals(to);
            case CHAPTER_PENDING -> VIDEO_PENDING.equals(to);
            case VIDEO_PENDING   -> REVIEWING.equals(to) || CHAPTER_PENDING.equals(to);
            case REVIEWING       -> PUBLISHED.equals(to) || UNPUBLISHED.equals(to);
            case UNPUBLISHED     -> DRAFT.equals(to);
            case PUBLISHED       -> UNPUBLISHED.equals(to);
            default              -> false;
        };
    }
}
