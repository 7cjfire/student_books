import { request } from './http'

export interface Subject {
  id?: number
  title: string
  parentId?: number
  sort?: number
  createTime?: string
  updateTime?: string
}

export interface SubjectTreeNode extends Subject {
  children?: SubjectTreeNode[]
}

export const subjectApi = {
  tree: () =>
    request<SubjectTreeNode[]>({ method: 'GET', url: '/subjects/tree' }),

  firstLevel: () =>
    request<Subject[]>({ method: 'GET', url: '/subjects/first-level' }),

  children: (parentId: number) =>
    request<Subject[]>({
      method: 'GET',
      url: `/subjects/children/${parentId}`,
    }),

  path: (id: number) =>
    request<Subject[]>({ method: 'GET', url: `/subjects/path/${id}` }),

  add: (data: Subject) =>
    request<boolean>({ method: 'POST', url: '/subjects', data }),

  update: (id: number, data: Subject) =>
    request<boolean>({ method: 'PUT', url: `/subjects/${id}`, data }),

  remove: (id: number) =>
    request<boolean>({ method: 'DELETE', url: `/subjects/${id}` }),
}
