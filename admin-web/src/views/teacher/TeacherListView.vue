<template>
  <div class="page">
    <div class="page-header">
      <h2>教师管理</h2>
      <el-button type="primary" :icon="Plus" @click="openEdit()">新增教师</el-button>
    </div>

    <div class="search-bar">
      <el-input v-model="q.teacherName" placeholder="按姓名搜索" clearable style="width: 180px" />
      <el-input v-model="q.department" placeholder="按院系搜索" clearable style="width: 180px" />
      <el-button type="primary" @click="onSearch">查询</el-button>
      <el-button @click="resetSearch">重置</el-button>
    </div>

    <el-table v-loading="loading" :data="rows" border stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="teacherName" label="姓名" width="120" />
      <el-table-column prop="teacherNo"   label="工号" width="120" />
      <el-table-column prop="gender"      label="性别" width="80" />
      <el-table-column prop="title"       label="职称" width="120" />
      <el-table-column prop="department"  label="所属院系" width="160" />
      <el-table-column prop="phone"       label="电话" width="140" />
      <el-table-column prop="email"       label="邮箱" />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
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

    <el-pagination
      v-if="!searchMode"
      style="margin-top: 12px"
      layout="total, sizes, prev, pager, next"
      :total="total"
      :page-sizes="[10, 20, 50]"
      v-model:current-page="pageNum"
      v-model:page-size="pageSize"
      @current-change="loadPage"
      @size-change="loadPage"
    />

    <el-dialog
      v-model="edit.open"
      :title="edit.form.id ? '编辑教师' : '新增教师'"
      width="600px"
    >
      <el-form ref="formRef" :model="edit.form" :rules="rules" label-width="90px">
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item prop="teacherName" label="姓名">
              <el-input v-model="edit.form.teacherName" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="teacherNo" label="工号">
              <el-input v-model="edit.form.teacherNo" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :span="8">
            <el-form-item label="性别">
              <el-select v-model="edit.form.gender" clearable placeholder="请选择">
                <el-option label="男" value="男" />
                <el-option label="女" value="女" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="年龄">
              <el-input-number v-model="edit.form.age" :min="18" :max="100" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="职称">
              <el-input v-model="edit.form.title" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="所属院系">
          <el-input v-model="edit.form.department" />
        </el-form-item>
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="电话">
              <el-input v-model="edit.form.phone" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱">
              <el-input v-model="edit.form.email" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="入职日期">
              <el-date-picker
                v-model="edit.form.hireDate"
                type="date"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="基本工资">
              <el-input-number v-model="edit.form.salary" :min="0" :precision="2" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="状态">
          <el-switch
            v-model="edit.form.status"
            :active-value="1"
            :inactive-value="0"
            active-text="启用"
            inactive-text="禁用"
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
import { reactive, ref } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import { teacherApi, Teacher } from '@/api/teacher'

const loading = ref(false)
const rows = ref<Teacher[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const searchMode = ref(false)

const q = reactive({ teacherName: '', department: '' })

async function loadPage() {
  loading.value = true
  try {
    const r = await teacherApi.page(pageNum.value, pageSize.value)
    rows.value = r.records || []
    total.value = r.total || 0
    searchMode.value = false
  } finally {
    loading.value = false
  }
}

async function onSearch() {
  if (!q.teacherName && !q.department) {
    pageNum.value = 1
    return loadPage()
  }
  loading.value = true
  try {
    rows.value = await teacherApi.search({
      teacherName: q.teacherName || undefined,
      department: q.department || undefined,
    })
    total.value = rows.value.length
    searchMode.value = true
  } finally {
    loading.value = false
  }
}

function resetSearch() {
  q.teacherName = ''
  q.department = ''
  pageNum.value = 1
  loadPage()
}

const edit = reactive({
  open: false,
  form: {} as Teacher,
})
const formRef = ref<FormInstance>()
const saving = ref(false)

const rules: FormRules = {
  teacherName: [{ required: true, message: '姓名不能为空', trigger: 'blur' }],
  teacherNo:   [{ required: true, message: '工号不能为空', trigger: 'blur' }],
}

function openEdit(row?: Teacher) {
  edit.form = row
    ? { ...row }
    : ({ status: 1 } as Teacher)
  edit.open = true
}

async function doSave() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try {
    if (edit.form.id) {
      await teacherApi.update(edit.form)
    } else {
      await teacherApi.add(edit.form)
    }
    ElMessage.success('已保存')
    edit.open = false
    searchMode.value ? onSearch() : loadPage()
  } finally {
    saving.value = false
  }
}

async function doDelete(row: Teacher) {
  if (!row.id) return
  await teacherApi.remove(row.id)
  ElMessage.success('已删除')
  searchMode.value ? onSearch() : loadPage()
}

loadPage()
</script>
