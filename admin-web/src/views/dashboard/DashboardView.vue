<template>
  <div class="page">
    <div class="page-header"><h2>仪表盘</h2></div>
    <el-row :gutter="12">
      <el-col :span="6" v-for="m in cards" :key="m.title">
        <el-card shadow="hover">
          <div class="metric">
            <el-icon :size="28" :color="m.color"><component :is="m.icon" /></el-icon>
            <div class="meta">
              <div class="num">{{ m.value ?? '-' }}</div>
              <div class="label">{{ m.title }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="hover" style="margin-top: 16px">
      <template #header>
        <span>快速导航</span>
      </template>
      <el-space wrap>
        <el-button @click="$router.push('/course/new')">新建课程</el-button>
        <el-button @click="$router.push('/banner')">管理 Banner</el-button>
        <el-button @click="$router.push('/subject')">课程分类</el-button>
        <el-button @click="$router.push('/home-preview')">预览首页</el-button>
      </el-space>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive } from 'vue'
import { indexApi } from '@/api/index-api'

const cards = reactive([
  { title: '首页 Banner', value: 0, icon: 'Picture',    color: '#409EFF' },
  { title: '热门课程',   value: 0, icon: 'VideoCamera', color: '#67C23A' },
  { title: '最新课程',   value: 0, icon: 'DataLine',    color: '#E6A23C' },
  { title: '推荐讲师',   value: 0, icon: 'User',        color: '#F56C6C' },
])

onMounted(async () => {
  try {
    const home = await indexApi.home()
    cards[0].value = home.banners?.length || 0
    cards[1].value = home.hotCourses?.length || 0
    cards[2].value = home.latestCourses?.length || 0
    cards[3].value = home.recommendedTeachers?.length || 0
  } catch {
    /* 已由拦截器处理 */
  }
})
</script>

<style scoped>
.metric {
  display: flex;
  align-items: center;
  gap: 16px;
}
.meta .num {
  font-size: 24px;
  font-weight: 600;
}
.meta .label {
  font-size: 12px;
  color: #909399;
}
</style>
