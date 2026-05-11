import { request } from './http'
import type { PageResult } from './book'

export interface Teacher {
  id?: number
  teacherName: string
  teacherNo: string
  gender?: string
  age?: number
  title?: string
  department?: string
  phone?: string
  email?: string
  hireDate?: string
  salary?: number
  status?: number
  createTime?: string
  updateTime?: string
}

export const teacherApi = {
  page: (pageNum: number, pageSize: number) =>
    request<PageResult<Teacher>>({
      method: 'GET',
      url: '/teacher/list',
      params: { pageNum, pageSize },
    }),

  search: (params: { teacherName?: string; department?: string }) =>
    request<Teacher[]>({ method: 'GET', url: '/teacher/search', params }),

  get: (id: number) =>
    request<Teacher>({ method: 'GET', url: `/teacher/${id}` }),

  add: (data: Teacher) =>
    request<boolean>({ method: 'POST', url: '/teacher/add', data }),

  update: (data: Teacher) =>
    request<boolean>({ method: 'PUT', url: '/teacher/update', data }),

  remove: (id: number) =>
    request<boolean>({ method: 'DELETE', url: `/teacher/delete/${id}` }),
}
