import { request } from './http'
import type { PageResult } from './book'

export interface Comment {
  id?: number
  courseId: number | string
  userId?: number
  nickname?: string
  avatar?: string
  content: string
  createTime?: string
}

export const commentApi = {
  /** 按课程分页查评论 */
  list: (courseId: number | string, pageNum = 1, pageSize = 10) =>
    request<PageResult<Comment>>({
      method: 'GET',
      url: `/course/comments/course/${courseId}`,
      params: { pageNum, pageSize },
    }),

  /** 发表评论 */
  add: (data: { courseId: number | string; content: string; nickname?: string }) =>
    request<Comment>({
      method: 'POST',
      url: '/course/comments',
      data,
    }),

  /** 删除评论 */
  remove: (id: number) =>
    request<boolean>({ method: 'DELETE', url: `/course/comments/${id}` }),
}
