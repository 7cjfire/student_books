import { request } from './http'

export interface Banner {
  id?: number
  title: string
  imageUrl: string
  linkUrl?: string
  sort?: number
  enabled?: number
  startTime?: string
  endTime?: string
  createTime?: string
  updateTime?: string
}

export interface HomeCourseItem {
  id: number
  title: string
  cover?: string
  price?: number
  lessonNum?: number
  viewCount?: number
  teacherId?: number
}

export interface HomeTeacherItem {
  id: number
  teacherName: string
  title?: string
  department?: string
}

export interface HomeData {
  banners: Banner[]
  hotCourses: HomeCourseItem[]
  latestCourses: HomeCourseItem[]
  recommendedTeachers: HomeTeacherItem[]
}

export const indexApi = {
  home: () => request<HomeData>({ method: 'GET', url: '/index/home' }),

  evictHomeCache: () =>
    request<void>({ method: 'POST', url: '/index/home/cache/evict' }),

  bannerList: () =>
    request<Banner[]>({ method: 'GET', url: '/index/banners' }),

  activeBanners: () =>
    request<Banner[]>({ method: 'GET', url: '/index/banners/active' }),

  bannerAdd: (data: Banner) =>
    request<Banner>({ method: 'POST', url: '/index/banners', data }),

  bannerUpdate: (id: number, data: Banner) =>
    request<boolean>({ method: 'PUT', url: `/index/banners/${id}`, data }),

  bannerDelete: (id: number) =>
    request<boolean>({ method: 'DELETE', url: `/index/banners/${id}` }),
}
