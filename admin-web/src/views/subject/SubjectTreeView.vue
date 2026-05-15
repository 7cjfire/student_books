<template>
  <div class="page">
    <div class="page-header">
      <h2>课程分类</h2>
      <el-space>
        <el-upload
          :show-file-list="false"
          :before-upload="onExcelBefore"
          :http-request="doImportExcel"
          accept=".xls,.xlsx"
        >
          <el-button :icon="Upload">Excel 批量导入</el-button>
        </el-upload>
        <el-button type="primary" :icon="Plus" @click="openEdit({ parentId: 0 })">
          新增一级分类
        </el-button>
      </el-space>
    </div>

    <el-card v-loading="loading" shadow="never">
      <el-tree
        :data="tree"
        node-key="id"
        default-expand-all
        :props="{ label: 'title', children: 'children' }"
      >
        <template #default="{ node, data }">
          <div class="tree-node">
            <span class="name">
              <el-tag v-if="data.parentId === 0" type="info" size="small">一级</el-tag>
              <el-tag v-else size="small">二级</el-tag>
              {{ node.label }}
              <span class="sort">sort: {{ data.sort ?? 0 }}</span>
            </span>
            <span class="ops">
              <el-button
                v-if="data.parentId === 0"
                size="small"
                text
                @click.stop="openEdit({ parentId: data.id })"
              >
                新增子分类
              </el-button>
              <el-button size="small" text @click.stop="openEdit(data)">编辑</el-button>
              <el-popconfirm title="确认删除？（有子分类则不允许）" @confirm="doDelete(data)">
                <template #reference>
                  <el-button size="small" text type="danger" @click.stop>删除</el-button>
                </template>
              </el-popconfirm>
            </span>
          </div>
        </template>
      </el-tree>
    </el-card>

    <el-dialog v-model="edit.open" :title="edit.form.id ? '编辑分类' : '新增分类'" width="440px">
      <el-form :model="edit.form" label-width="80px">
        <el-form-item label="类别名称" required>
          <el-input v-model="edit.form.title" />
        </el-form-item>
        <el-form-item label="父级 ID">
          <el-input v-model="edit.form.parentId" disabled />
          <div style="font-size:12px;color:#909399">0 表示一级分类</div>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="edit.form.sort" :min="0" />
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
import { reactive, ref } from 'vue'
import { Plus, Upload } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { UploadRequestOptions } from 'element-plus'
import { subjectApi, Subject, SubjectTreeNode } from '@/api/subject'
import { fileApi } from '@/api/file'

const loading = ref(false)
const tree = ref<SubjectTreeNode[]>([])

async function load() {
  loading.value = true
  try {
    tree.value = await subjectApi.tree()
  } finally {
    loading.value = false
  }
}
load()

const edit = reactive({
  open: false,
  form: {} as Subject,
})
const saving = ref(false)

function openEdit(src: Partial<Subject>) {
  edit.form = {
    id: src.id,
    title: src.title || '',
    parentId: src.parentId ?? 0,
    sort: src.sort ?? 0,
  } as Subject
  edit.open = true
}

async function doSave() {
  if (!edit.form.title?.trim()) {
    ElMessage.warning('请填写类别名称')
    return
  }
  saving.value = true
  try {
    if (edit.form.id) {
      await subjectApi.update(edit.form.id, edit.form)
    } else {
      await subjectApi.add(edit.form)
    }
    ElMessage.success('已保存')
    edit.open = false
    load()
  } finally {
    saving.value = false
  }
}

async function doDelete(row: SubjectTreeNode) {
  if (!row.id) return
  await subjectApi.remove(row.id)
  ElMessage.success('已删除')
  load()
}

function onExcelBefore(file: File) {
  const ok = /\.(xls|xlsx)$/i.test(file.name)
  if (!ok) {
    ElMessage.error('只支持 xls / xlsx')
    return false
  }
  return true
}

async function doImportExcel(opts: UploadRequestOptions) {
  try {
    const r = await fileApi.uploadCourseCatalog(opts.file)
    ElMessageBox.alert(
      `共 ${r.totalRows} 行，新增一级 ${r.firstLevelInserted} 条，新增二级 ${r.secondLevelInserted} 条`,
      '导入完成',
      { type: 'success' }
    )
    load()
  } catch {
    /* 已由拦截器弹错 */
  }
}
</script>

<style scoped lang="scss">
.tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-right: 8px;
  .name {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    .sort {
      color: #909399;
      font-size: 12px;
    }
  }
}
</style>
