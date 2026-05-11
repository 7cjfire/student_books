import axios, { AxiosError, AxiosInstance, AxiosRequestConfig } from 'axios'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import router from '@/router'

export interface ApiResult<T = unknown> {
  code: number
  message: string
  data: T
}

function createHttp(): AxiosInstance {
  const instance = axios.create({
    baseURL: '/api',
    timeout: 30_000,
  })

  instance.interceptors.request.use((config) => {
    const auth = useAuthStore()
    if (auth.token && config.headers) {
      config.headers.Authorization = `Bearer ${auth.token}`
    }
    return config
  })

  instance.interceptors.response.use(
    (response) => {
      const body = response.data as ApiResult
      // 后端统一返回 {code, message, data}
      if (body && typeof body.code === 'number') {
        if (body.code === 200) {
          return body
        }
        ElMessage.error(body.message || '请求失败')
        return Promise.reject(body)
      }
      // 兼容非 Result 结构（例如直接 blob）
      return response
    },
    (error: AxiosError<ApiResult>) => {
      const status = error.response?.status
      const body = error.response?.data
      const msg = body?.message || error.message || '网络异常'

      if (status === 401) {
        ElMessage.warning('登录已失效，请重新登录')
        const auth = useAuthStore()
        auth.logout()
        const currentPath = router.currentRoute.value.fullPath
        if (!currentPath.startsWith('/login')) {
          router.replace({ path: '/login', query: { redirect: currentPath } })
        }
      } else {
        ElMessage.error(msg)
      }
      return Promise.reject(error)
    }
  )
  return instance
}

const http = createHttp()

/** 便捷方法：直接返回 response.data */
export async function request<T = unknown>(cfg: AxiosRequestConfig): Promise<T> {
  const res = (await http.request<ApiResult<T>>(cfg)) as unknown as ApiResult<T>
  return res.data
}

export default http
