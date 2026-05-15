<template>
  <div class="page">
    <div class="page-header">
      <h2>课程列表</h2>
      <el-button type="primary" :icon="Plus" @click="$router.push('/course/new')">
        新建课程
      </el-button>
    </div>

    <div class="search-bar">
      <el-select v-model="status" placeholder="按状态过滤" clearable style="width: 180px">
        <el-option
          v-for="s in statusOptions"
          :key="s.value"
          :label="s.label"
          :value="s.value"
        />
      </el-select>
      <el-button type="primary" @click="loadPage">查询</el-button>
    </div>

    <el-table v-loading="loading" :data="rows" border stripe>
      <el-table-column prop="id" label="ID" width="100" />
      <el-table-column label="封面" width="100">
        <template #default="{ row }">
          <el-image
            v-if="row.cover"
            :src="row.cover"
            fit="cover"
            style="width: 72px; height: 40px; border-radius: 4px"
          />
          <span v-else style="color: #c0c4cc">-</span>
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" show-overflow-tooltip />
      <el-table-column prop="lessonNum" label="小节数" width="80" />
      <el-table-column prop="price" label="价格" width="100" />
      <el-table-column prop="viewCount" label="浏览" width="80" />
      <el-table-column label="状态" width="120">
        <template #default="{ row }">
          <el-tag :type="statusTag[row.status]">
            {{ statusText[row.status] || row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="updateTime" label="更新时间" width="170" />
      <el-table-column label="操作" width="240" fixed="right">
        <template #default="{ row }">
          <el-button size="small" text @click="$router.push(`/course/${row.id}/edit`)">
            编辑
          </el-button>
          <el-button
            v-if="row.status === 'REVIEWING'"
            size="small"
            text
            type="success"
            @click="review(row, true)"
          >
            审核通过
          </el-button>
          <el-button
            v-if="row.status === 'REVIEWING'"
            size="small"
            text
            type="warning"
            @click="review(row, false)"
          >
            驳回
          </el-button>
          <el-popconfirm
            title="确认删除？已发布课程需先下架"
            @confirm="doDelete(row)"
          >
            <template #reference>
              <el-button size="small" text type="danger">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      style="margin-top: 12px"
      layout="total, sizes, prev, pager, next"
      :total="total"
      :page-sizes="[10, 20, 50]"
      v-model:current-page="pageNum"
      v-model:page-size="pageSize"
      @current-change="loadPage"
      @size-change="loadPage"
    />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import {
  courseApi,
  Course,
  CourseStatus,
  COURSE_STATUS_TEXT,
  COURSE_STATUS_TAG,
} from '@/api/course'

const loading = ref(false)
const rows = ref<Course[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const status = ref<CourseStatus | ''>('')

const statusText = COURSE_STATUS_TEXT
const statusTag = COURSE_STATUS_TAG

const statusOptions = (Object.keys(COURSE_STATUS_TEXT) as CourseStatus[]).map(
  (k) => ({ label: COURSE_STATUS_TEXT[k], value: k })
)

async function loadPage() {
  loading.value = true
  try {
    const r = await courseApi.page(
      pageNum.value,
      pageSize.value,
      (status.value || undefined) as CourseStatus | undefined
    )
    rows.value = r.records || []
    total.value = r.total || 0
  } finally {
    loading.value = false
  }
}
loadPage()

async function review(row: Course, publish: boolean) {
  if (!row.id) return
  await courseApi.review(row.id, publish)
  ElMessage.success(publish ? '已发布' : '已驳回')
  loadPage()
}

async function doDelete(row: Course) {
  if (!row.id) return
  await courseApi.remove(row.id)
  ElMessage.success('已删除')
  loadPage()
}
</script>
