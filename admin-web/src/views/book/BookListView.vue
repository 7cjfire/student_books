<template>
  <div class="page">
    <div class="page-header">
      <h2>图书管理</h2>
      <el-button type="primary" :icon="Plus" @click="openEdit()">新增图书</el-button>
    </div>

    <div class="search-bar">
      <el-input
        v-model="q.bookName"
        placeholder="按书名模糊搜索"
        clearable
        style="width: 200px"
      />
      <el-input
        v-model="q.author"
        placeholder="按作者精确搜索"
        clearable
        style="width: 180px"
      />
      <el-button type="primary" @click="onSearch">查询</el-button>
      <el-button @click="resetSearch">重置</el-button>
    </div>

    <el-table v-loading="loading" :data="rows" border stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="bookName" label="书名" />
      <el-table-column prop="author" label="作者" width="140" />
      <el-table-column prop="publisher" label="出版社" width="180" />
      <el-table-column prop="price" label="价格" width="100" />
      <el-table-column prop="stock" label="库存" width="100" />
      <el-table-column prop="updateTime" label="更新时间" width="160" />
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
      :title="edit.form.id ? '编辑图书' : '新增图书'"
      width="520px"
    >
      <el-form ref="formRef" :model="edit.form" :rules="rules" label-width="90px">
        <el-form-item prop="bookName" label="书名">
          <el-input v-model="edit.form.bookName" />
        </el-form-item>
        <el-form-item prop="author" label="作者">
          <el-input v-model="edit.form.author" />
        </el-form-item>
        <el-form-item label="出版社">
          <el-input v-model="edit.form.publisher" />
        </el-form-item>
        <el-form-item label="出版日期">
          <el-date-picker
            v-model="edit.form.publishDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="选择日期"
          />
        </el-form-item>
        <el-form-item label="价格">
          <el-input-number v-model="edit.form.price" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="库存">
          <el-input-number v-model="edit.form.stock" :min="0" />
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
import { bookApi, Book } from '@/api/book'

const loading = ref(false)
const rows = ref<Book[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const searchMode = ref(false)

const q = reactive({ bookName: '', author: '' })

async function loadPage() {
  loading.value = true
  try {
    const r = await bookApi.page(pageNum.value, pageSize.value)
    rows.value = r.records || []
    total.value = r.total || 0
    searchMode.value = false
  } finally {
    loading.value = false
  }
}

async function onSearch() {
  if (!q.bookName && !q.author) {
    pageNum.value = 1
    return loadPage()
  }
  loading.value = true
  try {
    rows.value = await bookApi.list({
      bookName: q.bookName || undefined,
      author: q.author || undefined,
    })
    total.value = rows.value.length
    searchMode.value = true
  } finally {
    loading.value = false
  }
}

function resetSearch() {
  q.bookName = ''
  q.author = ''
  pageNum.value = 1
  loadPage()
}

// ---------- edit ----------
const edit = reactive({
  open: false,
  form: {} as Book,
})
const formRef = ref<FormInstance>()
const saving = ref(false)

const rules: FormRules = {
  bookName: [{ required: true, message: '书名不能为空', trigger: 'blur' }],
  author:   [{ required: true, message: '作者不能为空', trigger: 'blur' }],
}

function openEdit(row?: Book) {
  edit.form = row ? { ...row } : ({ stock: 0, price: 0 } as Book)
  edit.open = true
}

async function doSave() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try {
    if (edit.form.id) {
      await bookApi.update(edit.form.id, edit.form)
    } else {
      await bookApi.add(edit.form)
    }
    ElMessage.success('已保存')
    edit.open = false
    searchMode.value ? onSearch() : loadPage()
  } finally {
    saving.value = false
  }
}

async function doDelete(row: Book) {
  if (!row.id) return
  await bookApi.remove(row.id)
  ElMessage.success('已删除')
  searchMode.value ? onSearch() : loadPage()
}

loadPage()
</script>
