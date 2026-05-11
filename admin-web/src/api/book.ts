import { request } from './http'

export interface Book {
  id?: number
  bookName: string
  author: string
  publisher?: string
  publishDate?: string
  price?: number
  stock?: number
  createTime?: string
  updateTime?: string
}

export interface PageResult<T> {
  records: T[]
  total: number
  current: number
  size: number
}

export const bookApi = {
  page: (pageNum: number, pageSize: number) =>
    request<PageResult<Book>>({
      method: 'GET',
      url: '/books/page',
      params: { pageNum, pageSize },
    }),

  list: (params: { bookName?: string; author?: string }) =>
    request<Book[]>({ method: 'GET', url: '/books/list', params }),

  get: (id: number) => request<Book>({ method: 'GET', url: `/books/${id}` }),

  add: (data: Book) => request<Book>({ method: 'POST', url: '/books', data }),

  update: (id: number, data: Book) =>
    request<boolean>({ method: 'PUT', url: `/books/${id}`, data }),

  remove: (id: number) =>
    request<boolean>({ method: 'DELETE', url: `/books/${id}` }),
}
