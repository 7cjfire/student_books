import { request } from './http'

export interface LoginPayload {
  username: string
  password: string
}

export interface UserVo {
  id: number
  username: string
  nickName?: string
  roles?: string
  avatar?: string
}

export interface LoginResult {
  token: string
  user: UserVo
}

export const authApi = {
  login: (data: LoginPayload) =>
    request<LoginResult>({ method: 'POST', url: '/auth/login', data }),

  me: () => request<UserVo>({ method: 'GET', url: '/auth/me' }),

  logout: () => request<void>({ method: 'POST', url: '/auth/logout' }),
}
