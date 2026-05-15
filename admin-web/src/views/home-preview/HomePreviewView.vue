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
          <ul v-if="data?.hotCourses?.length" class="course-ul">
            <li v-for="c in data.hotCourses" :key="c.id" class="course-item">
              <span class="title">{{ c.title }}</span>
              <span class="meta">
                <span>{{ c.lessonNum ?? 0 }} 节</span>
                <span class="sep">·</span>
                <span>{{ c.viewCount ?? 0 }} 人</span>
                <span class="sep">·</span>
                <span class="price">¥{{ c.price ?? 0 }}</span>
              </span>
            </li>
          </ul>
          <div v-else class="empty">暂无热门课程</div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card v-loading="loading" shadow="never">
          <template #header><span>最新课程</span></template>
          <ul v-if="data?.latestCourses?.length" class="course-ul">
            <li v-for="c in data.latestCourses" :key="c.id" class="course-item">
              <span class="title">{{ c.title }}</span>
              <span class="meta">
                <span>{{ c.lessonNum ?? 0 }} 节</span>
                <span class="sep">·</span>
                <span>{{ c.viewCount ?? 0 }} 人</span>
                <span class="sep">·</span>
                <span class="price">¥{{ c.price ?? 0 }}</span>
                <el-button
                  size="small"
                  type="primary"
                  text
                  style="margin-left: 8px"
                  @click="openCourseDetail(c)"
                >
                  详情/播放
                </el-button>
              </span>
            </li>
          </ul>
          <div v-else class="empty">暂无最新课程</div>
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

    <!-- 课程详情弹窗（含章节列表 + 播放按钮） -->
    <el-dialog v-model="detailVisible" :title="detailCourse?.title || '课程详情'" width="700px">
      <div v-if="detailLoading" style="text-align: center; padding: 30px">
        <el-icon class="is-loading" :size="24"><Loading /></el-icon>
        <span style="margin-left: 8px">加载中...</span>
      </div>
      <div v-else-if="detailData">
        <el-descriptions :column="2" border size="small" style="margin-bottom: 16px">
          <el-descriptions-item label="课程标题">{{ detailData.course.title }}</el-descriptions-item>
          <el-descriptions-item label="价格">¥{{ detailData.course.price ?? 0 }}</el-descriptions-item>
          <el-descriptions-item label="小节数">{{ detailData.course.lessonNum ?? 0 }}</el-descriptions-item>
          <el-descriptions-item label="浏览量">{{ detailData.course.viewCount ?? 0 }}</el-descriptions-item>
        </el-descriptions>

        <div v-for="ch in detailData.chapters" :key="ch.id" style="margin-bottom: 12px">
          <h4 style="margin: 0 0 6px">{{ ch.title }}</h4>
          <el-table :data="ch.videos" border size="small">
            <el-table-column prop="title" label="小节" />
            <el-table-column label="视频" width="200">
              <template #default="{ row }">
                <el-button
                  v-if="row.videoId"
                  size="small"
                  type="success"
                  @click="playVideoId = row.videoId; showPlayer = true"
                >
                  播放
                </el-button>
                <el-tag v-else type="info" size="small">无视频</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div v-if="!detailData.chapters?.length" class="empty">暂无章节</div>
      </div>
    </el-dialog>

    <!-- 视频播放器 -->
    <VideoPlayer v-model="showPlayer" :video-id="playVideoId" />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh, Delete, Loading } from '@element-plus/icons-vue'
import { indexApi, HomeData, HomeCourseItem } from '@/api/index-api'
import { courseApi, CourseDetail } from '@/api/course'
import VideoPlayer from '@/components/VideoPlayer.vue'

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

// 课程详情弹窗
const detailVisible = ref(false)
const detailLoading = ref(false)
const detailCourse = ref<HomeCourseItem | null>(null)
const detailData = ref<CourseDetail | null>(null)

async function openCourseDetail(course: HomeCourseItem) {
  detailCourse.value = course
  detailVisible.value = true
  detailLoading.value = true
  detailData.value = null
  try {
    detailData.value = await courseApi.detail(course.id as number)
  } catch {
    /* 已由拦截器弹错 */
  } finally {
    detailLoading.value = false
  }
}

// 视频播放
const showPlayer = ref(false)
const playVideoId = ref('')
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

.course-ul {
  list-style: none;
  padding: 0;
  margin: 0;
}
.course-item {
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
    display: inline-flex;
    align-items: center;
    .sep {
      margin: 0 6px;
    }
    .price {
      color: #f56c6c;
    }
  }
}
.empty {
  color: #c0c4cc;
  text-align: center;
  padding: 16px 0;
}
</style>
