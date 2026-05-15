<template>
  <div class="page">
    <div class="page-header">
      <h2>文件上传</h2>
    </div>

    <el-row :gutter="16">
      <!-- 头像/图片上传 -->
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header><span>头像 / 图片上传</span></template>
          <el-upload
            drag
            :show-file-list="false"
            :before-upload="onImgBefore"
            :http-request="doUploadAvatar"
            accept="image/*"
          >
            <el-image
              v-if="avatarUrl"
              :src="avatarUrl"
              fit="cover"
              style="width: 200px; height: 200px; border-radius: 8px"
            />
            <div v-else class="upload-placeholder">
              <el-icon :size="40"><Upload /></el-icon>
              <div>将图片拖到此处，或<em>点击上传</em></div>
              <div class="tips">支持 jpg / jpeg / png / gif / webp，最大 5MB</div>
            </div>
          </el-upload>

          <div v-if="avatarUrl" class="result">
            <el-input v-model="avatarUrl" readonly>
              <template #prepend>URL</template>
              <template #append>
                <el-button @click="copyUrl(avatarUrl)">复制</el-button>
              </template>
            </el-input>
          </div>
        </el-card>
      </el-col>

      <!-- Excel 课程分类导入 -->
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header><span>Excel 课程分类导入</span></template>
          <el-upload
            drag
            :show-file-list="false"
            :before-upload="onExcelBefore"
            :http-request="doUploadExcel"
            accept=".xls,.xlsx"
          >
            <div class="upload-placeholder">
              <el-icon :size="40"><Document /></el-icon>
              <div>将 Excel 拖到此处，或<em>点击上传</em></div>
              <div class="tips">支持 xls / xlsx，最大 10MB</div>
              <div class="tips">列顺序：一级分类 | 二级分类 | 一级排序 | 二级排序</div>
            </div>
          </el-upload>

          <div v-if="importResult" class="result">
            <el-descriptions :column="1" border size="small">
              <el-descriptions-item label="文件名">{{ importResult.originalFilename }}</el-descriptions-item>
              <el-descriptions-item label="总行数">{{ importResult.totalRows }}</el-descriptions-item>
              <el-descriptions-item label="新增一级分类">{{ importResult.firstLevelInserted }}</el-descriptions-item>
              <el-descriptions-item label="新增二级分类">{{ importResult.secondLevelInserted }}</el-descriptions-item>
              <el-descriptions-item v-if="importResult.fileUrl" label="文件备份 URL">
                <el-link :href="importResult.fileUrl" target="_blank" type="primary">
                  {{ importResult.fileUrl }}
                </el-link>
              </el-descriptions-item>
            </el-descriptions>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Upload, Document } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import type { UploadRequestOptions } from 'element-plus'
import { fileApi, SubjectImportResult } from '@/api/file'

const avatarUrl = ref('')
const importResult = ref<SubjectImportResult | null>(null)

function onImgBefore(file: File) {
  if (!file.type.startsWith('image/')) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  if (file.size > 5 * 1024 * 1024) {
    ElMessage.error('图片不能超过 5MB')
    return false
  }
  return true
}

async function doUploadAvatar(opts: UploadRequestOptions) {
  try {
    const url = await fileApi.uploadAvatar(opts.file)
    avatarUrl.value = url
    ElMessage.success('上传成功！文件已存到阿里云 OSS')
  } catch {
    /* 已由拦截器弹错 */
  }
}

function onExcelBefore(file: File) {
  const ok = /\.(xls|xlsx)$/i.test(file.name)
  if (!ok) {
    ElMessage.error('只支持 xls / xlsx 格式')
    return false
  }
  if (file.size > 10 * 1024 * 1024) {
    ElMessage.error('Excel 不能超过 10MB')
    return false
  }
  return true
}

async function doUploadExcel(opts: UploadRequestOptions) {
  try {
    const r = await fileApi.uploadCourseCatalog(opts.file)
    importResult.value = r
    ElMessage.success(`导入完成：共 ${r.totalRows} 行，一级 ${r.firstLevelInserted} 条，二级 ${r.secondLevelInserted} 条`)
  } catch {
    /* 已由拦截器弹错 */
  }
}

function copyUrl(url: string) {
  navigator.clipboard.writeText(url).then(() => {
    ElMessage.success('已复制到剪贴板')
  }).catch(() => {
    ElMessage.warning('复制失败，请手动选中复制')
  })
}
</script>

<style scoped lang="scss">
.upload-placeholder {
  padding: 40px 0;
  text-align: center;
  color: #909399;
  em {
    color: #409eff;
    font-style: normal;
  }
  .tips {
    font-size: 12px;
    margin-top: 6px;
    color: #c0c4cc;
  }
}

.result {
  margin-top: 16px;
}
</style>
