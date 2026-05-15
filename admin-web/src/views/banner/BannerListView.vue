<template>
  <div class="page">
    <div class="page-header">
      <h2>Banner 管理</h2>
      <el-button type="primary" :icon="Plus" @click="openEdit()">新增 Banner</el-button>
    </div>

    <el-table v-loading="loading" :data="rows" border stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="图片" width="120">
        <template #default="{ row }">
          <el-image
            v-if="row.imageUrl"
            :src="row.imageUrl"
            fit="cover"
            style="width: 80px; height: 45px; border-radius: 4px"
            :preview-src-list="[row.imageUrl]"
            preview-teleported
          />
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="linkUrl" label="跳转链接" show-overflow-tooltip />
      <el-table-column prop="sort" label="排序" width="80" />
      <el-table-column label="启用" width="80">
        <template #default="{ row }">
          <el-tag :type="row.enabled === 1 ? 'success' : 'info'">
            {{ row.enabled === 1 ? '是' : '否' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="startTime" label="开始时间" width="170" />
      <el-table-column prop="endTime" label="结束时间" width="170" />
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button size="small" text @click="openEdit(row)">编辑</el-button>
          <el-popconfirm title="确认删除？" @confirm="doDelete(row)">
            <template #reference>
              <el-button size="small" text type="danger">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog
      v-model="edit.open"
      :title="edit.form.id ? '编辑 Banner' : '新增 Banner'"
      width="560px"
    >
      <el-form :model="edit.form" label-width="90px">
        <el-form-item label="标题" required>
          <el-input v-model="edit.form.title" />
        </el-form-item>
        <el-form-item label="图片" required>
          <el-upload
            :show-file-list="false"
            :before-upload="onImgBefore"
            :http-request="doUploadImage"
            accept="image/*"
          >
            <div class="img-upload">
              <el-image
                v-if="edit.form.imageUrl"
                :src="edit.form.imageUrl"
                fit="cover"
                style="width: 240px; height: 120px; border-radius: 4px"
              />
              <div v-else class="placeholder">
                <el-icon :size="24"><Plus /></el-icon>
                <span>点击上传</span>
              </div>
            </div>
          </el-upload>
          <div style="font-size: 12px; color: #909399">
            会走 file-service OSS 上传，如果 OSS 关闭则存到本地磁盘
          </div>
        </el-form-item>
        <el-form-item label="跳转链接">
          <el-input v-model="edit.form.linkUrl" placeholder="https://..." />
        </el-form-item>
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="排序">
              <el-input-number v-model="edit.form.sort" :min="0" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="启用">
              <el-switch
                v-model="edit.form.enabled"
                :active-value="1"
                :inactive-value="0"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="生效时间">
          <el-date-picker
            v-model="timeRange"
            type="datetimerange"
            value-format="YYYY-MM-DD HH:mm:ss"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="edit.open = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="doSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, watch } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import type { UploadRequestOptions } from 'element-plus'
import { indexApi, Banner } from '@/api/index-api'
import { fileApi } from '@/api/file'

const loading = ref(false)
const rows = ref<Banner[]>([])

async function load() {
  loading.value = true
  try {
    rows.value = await indexApi.bannerList()
  } finally {
    loading.value = false
  }
}
load()

const edit = reactive({
  open: false,
  form: {} as Banner,
})
const saving = ref(false)
const timeRange = ref<[string, string] | null>(null)

watch(
  () => edit.open,
  (v) => {
    if (v) {
      timeRange.value =
        edit.form.startTime || edit.form.endTime
          ? [edit.form.startTime || '', edit.form.endTime || '']
          : null
    }
  }
)

function openEdit(row?: Banner) {
  edit.form = row
    ? { ...row }
    : ({
        title: '',
        imageUrl: '',
        sort: 0,
        enabled: 1,
      } as Banner)
  edit.open = true
}

function onImgBefore(file: File) {
  if (!file.type.startsWith('image/')) {
    ElMessage.error('只能上传图片')
    return false
  }
  return true
}

async function doUploadImage(opts: UploadRequestOptions) {
  try {
    const url = await fileApi.uploadAvatar(opts.file)
    edit.form.imageUrl = url
    ElMessage.success('图片已上传')
  } catch {
    /* 已由拦截器弹错 */
  }
}

async function doSave() {
  if (!edit.form.title || !edit.form.imageUrl) {
    ElMessage.warning('标题和图片必填')
    return
  }
  edit.form.startTime = timeRange.value?.[0] || undefined
  edit.form.endTime = timeRange.value?.[1] || undefined

  saving.value = true
  try {
    if (edit.form.id) {
      await indexApi.bannerUpdate(edit.form.id, edit.form)
    } else {
      await indexApi.bannerAdd(edit.form)
    }
    ElMessage.success('已保存')
    edit.open = false
    load()
  } finally {
    saving.value = false
  }
}

async function doDelete(row: Banner) {
  if (!row.id) return
  await indexApi.bannerDelete(row.id)
  ElMessage.success('已删除')
  load()
}
</script>

<style scoped lang="scss">
.img-upload {
  cursor: pointer;
  .placeholder {
    width: 240px;
    height: 120px;
    border: 1px dashed #dcdfe6;
    border-radius: 4px;
    color: #909399;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 6px;
    &:hover {
      border-color: #409eff;
      color: #409eff;
    }
  }
}
</style>
