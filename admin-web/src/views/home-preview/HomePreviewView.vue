<template>
  <div class="page">
    <div class="page-header">
      <h2>首页预览</h2>
      <el-space>
        <el-button :icon="Refresh" @click="load">刷新</el-button>
        <el-button :icon="Delete" type="warning" @click="evict">清空缓存</el-button>
      </el-space>
    </div>

    <el-card v-loading="loading" shadow="never">
      <template #header><span>Banner ({{ data?.banners?.length || 0 }})</span></template>
      <div class="banner-strip">
        <el-image
          v-for="b in data?.banners"
          :key="b.id"
          :src="b.imageUrl"
          fit="cover"
          class="banner-thumb"
        />
        <el-empty v-if="!data?.banners?.length" description="暂无 Banner" :image-size="60" />
      </div>
    </el-card>

    <el-row :gutter="12" style="margin-top: 12px">
      <el-col :span="12">
        <el-card v-loading="loading" shadow="never">
          <template #header><span>热门课程</span></template>
          <course-list :items="data?.hotCourses || []" empty="暂无热门课程" />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card v-loading="loading" shadow="never">
          <template #header><span>最新课程</span></template>
          <course-list :items="data?.latestCourses || []" empty="暂无最新课程" />
        </el-card>
      </el-col>
    </el-row>

    <el-card v-loading="loading" shadow="never" style="margin-top: 12px">
      <template #header><span>推荐讲师</span></template>
      <el-space wrap>
        <el-card
          v-for="t in data?.recommendedTeachers"
          :key="t.id"
          class="teacher-card"
          shadow="never"
        >
          <div class="teacher-row">
            <el-avatar>{{ t.teacherName?.slice(0, 1) }}</el-avatar>
            <div>
              <div>{{ t.teacherName }}</div>
              <div class="desc">{{ t.title || '-' }} / {{ t.department || '-' }}</div>
            </div>
          </div>
        </el-card>
        <el-empty v-if="!data?.recommendedTeachers?.length" description="暂无推荐讲师" :image-size="60" />
      </el-space>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, defineComponent, h, PropType } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh, Delete } from '@element-plus/icons-vue'
import { indexApi, HomeData, HomeCourseItem } from '@/api/index-api'

const data = ref<HomeData | null>(null)
const loading = ref(false)

async function load() {
  loading.value = true
  try {
    data.value = await indexApi.home()
  } finally {
    loading.value = false
  }
}
load()

async function evict() {
  await indexApi.evictHomeCache()
  ElMessage.success('缓存已清空')
  load()
}

// 简单的内联组件：课程列表
const CourseList = defineComponent({
  props: {
    items: { type: Array as PropType<HomeCourseItem[]>, default: () => [] },
    empty: { type: String, default: '暂无数据' },
  },
  setup(props) {
    return () =>
      props.items.length
        ? h(
            'ul',
            { class: 'course-ul' },
            props.items.map((c) =>
              h('li', { key: c.id, class: 'course-item' }, [
                h('span', { class: 'title' }, c.title),
                h('span', { class: 'meta' }, [
                  h('span', {}, `${c.lessonNum ?? 0} 节`),
                  h('span', { class: 'sep' }, '·'),
                  h('span', {}, `${c.viewCount ?? 0} 人`),
                  h('span', { class: 'sep' }, '·'),
                  h('span', { class: 'price' }, `¥${c.price ?? 0}`),
                ]),
              ])
            )
          )
        : h('div', { class: 'empty' }, props.empty)
  },
})
</script>

<style scoped lang="scss">
.banner-strip {
  display: flex;
  gap: 12px;
  overflow-x: auto;
  .banner-thumb {
    width: 220px;
    height: 100px;
    border-radius: 4px;
    flex-shrink: 0;
  }
}

.teacher-card {
  width: 200px;
  .teacher-row {
    display: flex;
    align-items: center;
    gap: 10px;
    .desc {
      font-size: 12px;
      color: #909399;
      margin-top: 2px;
    }
  }
}

:deep(.course-ul) {
  list-style: none;
  padding: 0;
  margin: 0;
}
:deep(.course-item) {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid #f2f6fc;
  .title {
    color: #303133;
  }
  .meta {
    font-size: 12px;
    color: #909399;
    .sep {
      margin: 0 6px;
    }
    .price {
      color: #f56c6c;
    }
  }
}
:deep(.empty) {
  color: #c0c4cc;
  text-align: center;
  padding: 16px 0;
}
</style>
