import { request } from './http'
import type { PageResult } from './book'

export type CourseStatus =
  | 'DRAFT'
  | 'CHAPTER_PENDING'
  | 'VIDEO_PENDING'
  | 'REVIEWING'
  | 'PUBLISHED'
  | 'UNPUBLISHED'

export interface Course {
  id?: number
  title: string
  cover?: string
  subjectId?: number
  subjectParentId?: number
  teacherId?: number
  price?: number
  lessonNum?: number
  description?: string
  status?: CourseStatus
  viewCount?: number
  createTime?: string
  updateTime?: string
}

export interface CourseChapter {
  id?: number
  courseId?: number
  title: string
  sort?: number
}

export interface CourseVideo {
  id?: number
  courseId?: number
  chapterId: number
  title: string
  sort?: number
  videoId?: string
  videoSourceUrl?: string
  videoDuration?: number
  isFree?: number
}

export interface CourseDetail {
  course: Course
  chapters: Array<CourseChapter & { videos: CourseVideo[] }>
}

export const courseApi = {
  page: (pageNum: number, pageSize: number, status?: CourseStatus) =>
    request<PageResult<Course>>({
      method: 'GET',
      url: '/course/courses/page',
      params: { pageNum, pageSize, status },
    }),

  detail: (id: number) =>
    request<CourseDetail>({ method: 'GET', url: `/course/courses/${id}/detail` }),

  get: (id: number) =>
    request<Course>({ method: 'GET', url: `/course/courses/${id}` }),

  step1Create: (data: Course) =>
    request<Course>({ method: 'POST', url: '/course/courses/step-info', data }),

  step1Update: (id: number, data: Course) =>
    request<boolean>({
      method: 'PUT',
      url: `/course/courses/${id}/step-info`,
      data,
    }),

  advanceToVideo: (id: number) =>
    request<boolean>({
      method: 'POST',
      url: `/course/courses/${id}/step-advance/video`,
    }),

  advanceToReview: (id: number) =>
    request<boolean>({
      method: 'POST',
      url: `/course/courses/${id}/step-advance/review`,
    }),

  review: (id: number, publish: boolean) =>
    request<boolean>({
      method: 'POST',
      url: `/course/courses/${id}/review`,
      params: { publish },
    }),

  remove: (id: number) =>
    request<boolean>({ method: 'DELETE', url: `/course/courses/${id}` }),

  // 章节
  addChapter: (courseId: number, data: CourseChapter) =>
    request<CourseChapter>({
      method: 'POST',
      url: `/course/courses/${courseId}/chapters`,
      data,
    }),

  updateChapter: (courseId: number, chapterId: number, data: CourseChapter) =>
    request<boolean>({
      method: 'PUT',
      url: `/course/courses/${courseId}/chapters/${chapterId}`,
      data,
    }),

  deleteChapter: (courseId: number, chapterId: number) =>
    request<number>({
      method: 'DELETE',
      url: `/course/courses/${courseId}/chapters/${chapterId}`,
    }),

  // 小节
  addVideo: (courseId: number, data: CourseVideo) =>
    request<CourseVideo>({
      method: 'POST',
      url: `/course/courses/${courseId}/videos`,
      data,
    }),

  updateVideo: (courseId: number, videoPk: number, data: CourseVideo) =>
    request<boolean>({
      method: 'PUT',
      url: `/course/courses/${courseId}/videos/${videoPk}`,
      data,
    }),

  bindVod: (courseId: number, videoPk: number, videoId: string, videoDuration?: number) =>
    request<boolean>({
      method: 'POST',
      url: `/course/courses/${courseId}/videos/${videoPk}/bind-vod`,
      data: { videoId, videoDuration },
    }),

  deleteVideo: (courseId: number, videoPk: number) =>
    request<boolean>({
      method: 'DELETE',
      url: `/course/courses/${courseId}/videos/${videoPk}`,
    }),
}

export const COURSE_STATUS_TEXT: Record<CourseStatus, string> = {
  DRAFT: '草稿',
  CHAPTER_PENDING: '章节待完善',
  VIDEO_PENDING: '视频待绑定',
  REVIEWING: '审核中',
  PUBLISHED: '已发布',
  UNPUBLISHED: '已下架',
}

export const COURSE_STATUS_TAG: Record<
  CourseStatus,
  '' | 'success' | 'warning' | 'info' | 'danger'
> = {
  DRAFT: 'info',
  CHAPTER_PENDING: 'info',
  VIDEO_PENDING: 'warning',
  REVIEWING: 'warning',
  PUBLISHED: 'success',
  UNPUBLISHED: 'danger',
}
