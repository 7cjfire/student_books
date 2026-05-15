<template>
  <div class="page course-wizard">
    <div class="page-header">
      <h2>
        {{ courseId ? '编辑课程' : '新建课程' }}
        <el-tag v-if="detail?.course?.status" :type="statusTag[detail.course.status]" style="margin-left: 8px">
          {{ statusText[detail.course.status] }}
        </el-tag>
      </h2>
      <el-button :icon="Back" @click="$router.push('/course')">返回列表</el-button>
    </div>

    <el-card>
      <el-steps :active="activeStep" align-center finish-status="success" style="margin-bottom: 16px">
        <el-step title="基本信息" description="标题/分类/讲师/价格" />
        <el-step title="章节结构" description="一级章节 + 二级小节" />
        <el-step title="视频绑定" description="VOD videoId 回填" />
        <el-step title="审核发布" description="提交后审核" />
      </el-steps>

      <!-- Step 1 -->
      <div v-show="activeStep === 0">
        <el-form ref="infoRef" :model="info" :rules="infoRules" label-width="100px">
          <el-row :gutter="12">
            <el-col :span="12">
              <el-form-item prop="title" label="课程标题">
                <el-input v-model="info.title" />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item prop="subjectParentId" label="一级分类">
                <el-select
                  v-model="info.subjectParentId"
                  placeholder="请选择"
                  @change="onFirstChange"
                  style="width: 100%"
                >
                  <el-option v-for="s in firstLevel" :key="s.id" :label="s.title" :value="s.id" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item prop="subjectId" label="二级分类">
                <el-select v-model="info.subjectId" placeholder="请选择" style="width: 100%">
                  <el-option v-for="s in secondLevel" :key="s.id" :label="s.title" :value="s.id" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="12">
            <el-col :span="8">
              <el-form-item prop="teacherId" label="讲师">
                <el-select
                  v-model="info.teacherId"
                  placeholder="请选择"
                  filterable
                  style="width: 100%"
                >
                  <el-option
                    v-for="t in teacherOptions"
                    :key="t.id"
                    :label="`${t.teacherName} (${t.title || '讲师'})`"
                    :value="t.id"
                  />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="价格">
                <el-input-number v-model="info.price" :min="0" :precision="2" style="width: 100%" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="封面">
                <el-upload
                  :show-file-list="false"
                  :before-upload="onCoverBefore"
                  :http-request="doUploadCover"
                  accept="image/*"
                >
                  <el-image
                    v-if="info.cover"
                    :src="info.cover"
                    fit="cover"
                    style="width: 120px; height: 68px; border-radius: 4px"
                  />
                  <el-button v-else :icon="Upload">上传封面</el-button>
                </el-upload>
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="课程简介">
            <el-input v-model="info.description" type="textarea" :rows="3" maxlength="1024" show-word-limit />
          </el-form-item>
        </el-form>

        <div class="step-actions">
          <el-button type="primary" :loading="saving" @click="saveStep1">保存基本信息</el-button>
          <el-button v-if="courseId" type="primary" plain @click="activeStep = 1">
            下一步：章节结构 <el-icon><Right /></el-icon>
          </el-button>
        </div>
      </div>

      <!-- Step 2 -->
      <div v-show="activeStep === 1">
        <div style="margin-bottom: 12px">
          <el-button type="primary" :icon="Plus" @click="openChapterEdit()">新增章节</el-button>
          <span style="margin-left: 12px; color: #909399; font-size: 12px">
            至少 1 个章节，且每个章节至少 1 个小节，才能进入下一步
          </span>
        </div>
        <div v-if="!chapters.length" style="text-align: center; color: #909399; padding: 40px 0">
          还没有章节，请先新增
        </div>
        <div v-else>
          <el-card
            v-for="ch in chapters"
            :key="ch.id"
            shadow="never"
            class="chapter-card"
          >
            <template #header>
              <div class="chapter-header">
                <span>
                  <el-tag type="info" size="small">章</el-tag>
                  <strong>{{ ch.title }}</strong>
                  <span class="sort">sort: {{ ch.sort ?? 0 }}</span>
                </span>
                <span>
                  <el-button size="small" text @click="openChapterEdit(ch)">编辑章节</el-button>
                  <el-popconfirm
                    title="删除后章节下所有小节也会被删除"
                    @confirm="deleteChapter(ch)"
                  >
                    <template #reference>
                      <el-button size="small" text type="danger">删除章节</el-button>
                    </template>
                  </el-popconfirm>
                  <el-button size="small" text type="primary" @click="openVideoEdit({ chapterId: ch.id! } as any)">
                    + 小节
                  </el-button>
                </span>
              </div>
            </template>
            <el-table :data="ch.videos" border>
              <el-table-column prop="sort" label="排序" width="80" />
              <el-table-column prop="title" label="小节标题" />
              <el-table-column label="视频" width="140">
                <template #default="{ row }">
                  <el-tag v-if="row.videoId" type="success">已绑定</el-tag>
                  <el-tag v-else type="warning">待绑定</el-tag>
                  <span v-if="row.videoDuration" style="margin-left: 6px; color: #909399">
                    {{ row.videoDuration }}s
                  </span>
                </template>
              </el-table-column>
              <el-table-column label="免费" width="80">
                <template #default="{ row }">
                  <el-tag size="small" v-if="row.isFree === 1" type="success">是</el-tag>
                  <span v-else style="color: #909399">否</span>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="180">
                <template #default="{ row }">
                  <el-button size="small" text @click="openVideoEdit(row)">编辑</el-button>
                  <el-popconfirm title="确认删除？" @confirm="deleteVideo(row)">
                    <template #reference>
                      <el-button size="small" text type="danger">删除</el-button>
                    </template>
                  </el-popconfirm>
                </template>
              </el-table-column>
              <template #empty>暂无小节，请点右上角"+ 小节"</template>
            </el-table>
          </el-card>
        </div>

        <div class="step-actions">
          <el-button @click="activeStep = 0"><el-icon><Back /></el-icon> 上一步</el-button>
          <el-button type="primary" :loading="saving" @click="goStep3">
            下一步：视频绑定 <el-icon><Right /></el-icon>
          </el-button>
        </div>
      </div>

      <!-- Step 3 -->
      <div v-show="activeStep === 2">
        <el-alert type="info" :closable="false" style="margin-bottom: 12px">
          本项目只存 VOD videoId，视频直传部分由前端接阿里云 Web SDK 完成。
          这里直接填写从阿里云 VOD 拿到的 videoId 即可；如果是测试可随便填一个字符串。
        </el-alert>

        <el-table :data="flatVideos" border stripe>
          <el-table-column label="章节" prop="chapterTitle" width="200" />
          <el-table-column label="小节" prop="title" />
          <el-table-column label="videoId" min-width="260">
            <template #default="{ row }">
              <el-input
                v-model="row.__videoIdEdit"
                placeholder="阿里云 VOD videoId"
                clearable
              />
            </template>
          </el-table-column>
          <el-table-column label="时长（秒）" width="140">
            <template #default="{ row }">
              <el-input-number v-model="row.__durationEdit" :min="0" style="width: 100%" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button size="small" type="primary" :loading="row.__saving" @click="saveBind(row)">
                保存
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="step-actions">
          <el-button @click="activeStep = 1"><el-icon><Back /></el-icon> 上一步</el-button>
          <el-button type="primary" :loading="saving" @click="goStep4">
            下一步：提交审核 <el-icon><Right /></el-icon>
          </el-button>
        </div>
      </div>

      <!-- Step 4 -->
      <div v-show="activeStep === 3">
        <el-descriptions :column="2" border title="课程概览">
          <el-descriptions-item label="课程标题">{{ info.title }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="statusTag[detail?.course?.status || 'DRAFT']">
              {{ statusText[detail?.course?.status || 'DRAFT'] }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="章节数">{{ chapters.length }}</el-descriptions-item>
          <el-descriptions-item label="小节数">{{ totalVideos }}</el-descriptions-item>
          <el-descriptions-item label="已绑视频">{{ boundVideos }} / {{ totalVideos }}</el-descriptions-item>
          <el-descriptions-item label="价格">¥{{ info.price ?? 0 }}</el-descriptions-item>
        </el-descriptions>

        <div class="step-actions">
          <el-button @click="activeStep = 2"><el-icon><Back /></el-icon> 上一步</el-button>
          <template v-if="detail?.course?.status === 'VIDEO_PENDING' || detail?.course?.status === 'CHAPTER_PENDING' || detail?.course?.status === 'DRAFT'">
            <el-button type="primary" :loading="saving" @click="submitReview">
              提交审核
            </el-button>
          </template>
          <template v-else-if="detail?.course?.status === 'REVIEWING'">
            <el-button type="warning" :loading="saving" @click="doReview(false)">驳回</el-button>
            <el-button type="success" :loading="saving" @click="doReview(true)">
              审核通过并发布
            </el-button>
          </template>
          <template v-else>
            <el-alert type="success" :closable="false" title="当前状态已经是终态，无需审核操作" />
          </template>
        </div>
      </div>
    </el-card>

    <!-- 章节编辑 Dialog -->
    <el-dialog v-model="chapterDlg.open" :title="chapterDlg.form.id ? '编辑章节' : '新增章节'" width="420px">
      <el-form :model="chapterDlg.form" label-width="80px">
        <el-form-item label="标题" required>
          <el-input v-model="chapterDlg.form.title" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="chapterDlg.form.sort" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="chapterDlg.open = false">取消</el-button>
        <el-button type="primary" @click="saveChapter">保存</el-button>
      </template>
    </el-dialog>

    <!-- 小节编辑 Dialog -->
    <el-dialog v-model="videoDlg.open" :title="videoDlg.form.id ? '编辑小节' : '新增小节'" width="480px">
      <el-form :model="videoDlg.form" label-width="90px">
        <el-form-item label="所属章节" required>
          <el-select v-model="videoDlg.form.chapterId" placeholder="请选择" style="width: 100%">
            <el-option v-for="ch in chapters" :key="ch.id" :label="ch.title" :value="ch.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="标题" required>
          <el-input v-model="videoDlg.form.title" />
        </el-form-item>
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="排序">
              <el-input-number v-model="videoDlg.form.sort" :min="0" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="免费试看">
              <el-switch v-model="videoDlg.form.isFree" :active-value="1" :inactive-value="0" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="videoId">
          <el-input v-model="videoDlg.form.videoId" placeholder="阿里云 VOD videoId，可留空到步骤3再填" />
        </el-form-item>
        <el-form-item label="时长（秒）">
          <el-input-number v-model="videoDlg.form.videoDuration" :min="0" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="videoDlg.open = false">取消</el-button>
        <el-button type="primary" @click="saveVideo">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Back, Plus, Right, Upload } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules, UploadRequestOptions } from 'element-plus'
import {
  courseApi,
  Course,
  CourseChapter,
  CourseVideo,
  COURSE_STATUS_TEXT,
  COURSE_STATUS_TAG,
} from '@/api/course'
import { subjectApi, Subject } from '@/api/subject'
import { teacherApi, Teacher } from '@/api/teacher'
import { fileApi } from '@/api/file'

const route = useRoute()
const router = useRouter()

const statusText = COURSE_STATUS_TEXT
const statusTag = COURSE_STATUS_TAG

const courseId = ref<number | null>(
  route.params.id ? Number(route.params.id) : null
)
const activeStep = ref(0)

const saving = ref(false)
const infoRef = ref<FormInstance>()

const info = reactive<Course>({
  title: '',
  subjectId: undefined,
  subjectParentId: undefined,
  teacherId: undefined,
  price: 0,
  cover: '',
  description: '',
})

const infoRules: FormRules = {
  title: [{ required: true, message: '请填写课程标题', trigger: 'blur' }],
  subjectParentId: [{ required: true, message: '请选择一级分类', trigger: 'change' }],
  subjectId: [{ required: true, message: '请选择二级分类', trigger: 'change' }],
  teacherId: [{ required: true, message: '请选择讲师', trigger: 'change' }],
}

// 分类选项
const firstLevel = ref<Subject[]>([])
const secondLevel = ref<Subject[]>([])
subjectApi.firstLevel().then((r) => (firstLevel.value = r))
async function onFirstChange(parentId: number) {
  info.subjectId = undefined
  secondLevel.value = parentId ? await subjectApi.children(parentId) : []
}

// 讲师选项
const teacherOptions = ref<Teacher[]>([])
teacherApi.page(1, 100).then((r) => (teacherOptions.value = r.records || []))

// 详情
const detail = ref<Awaited<ReturnType<typeof courseApi.detail>> | null>(null)
const chapters = computed(() => detail.value?.chapters || [])

async function loadDetail() {
  if (!courseId.value) return
  detail.value = await courseApi.detail(courseId.value)
  const c = detail.value.course
  Object.assign(info, {
    id: c.id,
    title: c.title,
    subjectId: c.subjectId,
    subjectParentId: c.subjectParentId,
    teacherId: c.teacherId,
    price: c.price,
    cover: c.cover,
    description: c.description,
  })
  if (c.subjectParentId) {
    secondLevel.value = await subjectApi.children(c.subjectParentId)
  }
}
if (courseId.value) loadDetail()

// ---- Step 1 ----
function onCoverBefore(file: File) {
  if (!file.type.startsWith('image/')) {
    ElMessage.error('只能上传图片')
    return false
  }
  return true
}

async function doUploadCover(opts: UploadRequestOptions) {
  info.cover = await fileApi.uploadAvatar(opts.file)
  ElMessage.success('封面已上传')
}

async function saveStep1() {
  const valid = await infoRef.value?.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try {
    if (!courseId.value) {
      const created = await courseApi.step1Create(info)
      courseId.value = created.id!
      ElMessage.success('课程已创建')
      router.replace(`/course/${courseId.value}/edit`)
    } else {
      await courseApi.step1Update(courseId.value, info)
      ElMessage.success('已保存')
    }
    await loadDetail()
    activeStep.value = 1
  } finally {
    saving.value = false
  }
}

// ---- Step 2 ----
const chapterDlg = reactive({ open: false, form: {} as CourseChapter })
function openChapterEdit(src?: CourseChapter) {
  chapterDlg.form = src ? { ...src } : ({ title: '', sort: 0 } as CourseChapter)
  chapterDlg.open = true
}
async function saveChapter() {
  if (!courseId.value) {
    ElMessage.warning('请先保存步骤 1')
    return
  }
  if (!chapterDlg.form.title?.trim()) {
    ElMessage.warning('标题不能为空')
    return
  }
  if (chapterDlg.form.id) {
    await courseApi.updateChapter(courseId.value, chapterDlg.form.id, chapterDlg.form)
  } else {
    await courseApi.addChapter(courseId.value, chapterDlg.form)
  }
  chapterDlg.open = false
  ElMessage.success('已保存')
  await loadDetail()
}
async function deleteChapter(ch: CourseChapter) {
  if (!courseId.value || !ch.id) return
  await courseApi.deleteChapter(courseId.value, ch.id)
  ElMessage.success('已删除')
  await loadDetail()
}

const videoDlg = reactive({ open: false, form: {} as CourseVideo })
function openVideoEdit(src: Partial<CourseVideo>) {
  videoDlg.form = {
    id: src.id,
    chapterId: src.chapterId!,
    title: src.title || '',
    sort: src.sort ?? 0,
    videoId: src.videoId,
    videoDuration: src.videoDuration,
    isFree: src.isFree ?? 0,
  } as CourseVideo
  videoDlg.open = true
}
async function saveVideo() {
  if (!courseId.value) return
  if (!videoDlg.form.title?.trim()) {
    ElMessage.warning('标题不能为空')
    return
  }
  if (videoDlg.form.id) {
    await courseApi.updateVideo(courseId.value, videoDlg.form.id, videoDlg.form)
  } else {
    await courseApi.addVideo(courseId.value, videoDlg.form)
  }
  videoDlg.open = false
  ElMessage.success('已保存')
  await loadDetail()
}
async function deleteVideo(v: CourseVideo) {
  if (!courseId.value || !v.id) return
  await courseApi.deleteVideo(courseId.value, v.id)
  ElMessage.success('已删除')
  await loadDetail()
}

async function goStep3() {
  if (!courseId.value) return
  saving.value = true
  try {
    // 如果后端状态还没到 VIDEO_PENDING，调用 step-advance/video
    const st = detail.value?.course.status
    if (st === 'DRAFT' || st === 'CHAPTER_PENDING') {
      await courseApi.advanceToVideo(courseId.value)
      await loadDetail()
    }
    activeStep.value = 2
  } finally {
    saving.value = false
  }
}

// ---- Step 3 ----
interface FlatVideoRow extends CourseVideo {
  chapterTitle: string
  __videoIdEdit: string
  __durationEdit: number | undefined
  __saving: boolean
}

const flatVideos = ref<FlatVideoRow[]>([])
watch(
  () => detail.value,
  () => {
    flatVideos.value = (detail.value?.chapters || []).flatMap((ch) =>
      (ch.videos || []).map((v) => ({
        ...v,
        chapterTitle: ch.title,
        __videoIdEdit: v.videoId || '',
        __durationEdit: v.videoDuration,
        __saving: false,
      }))
    )
  },
  { immediate: true }
)

async function saveBind(row: FlatVideoRow) {
  if (!courseId.value || !row.id) return
  if (!row.__videoIdEdit?.trim()) {
    ElMessage.warning('videoId 不能为空')
    return
  }
  row.__saving = true
  try {
    await courseApi.bindVod(courseId.value, row.id, row.__videoIdEdit.trim(), row.__durationEdit)
    ElMessage.success('已绑定')
    await loadDetail()
  } finally {
    row.__saving = false
  }
}

async function goStep4() {
  if (!courseId.value) return
  const unbound = flatVideos.value.filter((v) => !v.__videoIdEdit?.trim())
  if (unbound.length) {
    try {
      await ElMessageBox.confirm(
        `还有 ${unbound.length} 个小节没绑定 videoId，现在提交审核会失败。确定要继续？`,
        '提示',
        { type: 'warning' }
      )
    } catch {
      return
    }
  }
  activeStep.value = 3
}

// ---- Step 4 ----
const totalVideos = computed(() =>
  (chapters.value || []).reduce((s, c) => s + (c.videos?.length || 0), 0)
)
const boundVideos = computed(() =>
  (chapters.value || []).reduce(
    (s, c) => s + (c.videos?.filter((v) => !!v.videoId).length || 0),
    0
  )
)

async function submitReview() {
  if (!courseId.value) return
  saving.value = true
  try {
    await courseApi.advanceToReview(courseId.value)
    ElMessage.success('已提交审核')
    await loadDetail()
  } finally {
    saving.value = false
  }
}

async function doReview(publish: boolean) {
  if (!courseId.value) return
  saving.value = true
  try {
    await courseApi.review(courseId.value, publish)
    ElMessage.success(publish ? '课程已发布' : '已驳回')
    await loadDetail()
  } finally {
    saving.value = false
  }
}
</script>

<style scoped lang="scss">
.course-wizard {
  .chapter-card {
    margin-bottom: 12px;
    .chapter-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      .sort {
        margin-left: 8px;
        color: #909399;
        font-size: 12px;
      }
    }
  }
  .step-actions {
    display: flex;
    justify-content: center;
    gap: 12px;
    margin-top: 24px;
    padding-top: 16px;
    border-top: 1px dashed #ebeef5;
  }
}
</style>
