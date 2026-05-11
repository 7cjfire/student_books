import http, { request } from './http'

export interface SubjectImportResult {
  originalFilename: string
  fileUrl?: string
  totalRows: number
  firstLevelInserted: number
  secondLevelInserted: number
}

export const fileApi = {
  /** 头像 / 通用图片上传（返回 URL）*/
  uploadAvatar: async (file: File): Promise<string> => {
    const fd = new FormData()
    fd.append('file', file)
    const res = (await http.post('/upload/avatar', fd, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })) as unknown as { data: string }
    return res.data
  },

  /** 课程分类 Excel 上传导入 */
  uploadCourseCatalog: async (file: File): Promise<SubjectImportResult> => {
    const fd = new FormData()
    fd.append('file', file)
    const res = (await http.post('/upload/course-catalog', fd, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })) as unknown as { data: SubjectImportResult }
    return res.data
  },

  /** VOD 播放凭证 */
  vodPlayAuth: (videoId: string) =>
    request<{ videoId: string; playAuth: string; title?: string }>({
      method: 'GET',
      url: `/vod/play-auth/${videoId}`,
    }),
}
