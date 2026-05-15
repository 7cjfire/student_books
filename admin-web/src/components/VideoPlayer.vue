<template>
  <el-dialog v-model="visible" :title="title || '视频播放'" width="720px" @close="onClose">
    <div v-if="loading" style="text-align: center; padding: 40px">
      <el-icon class="is-loading" :size="32"><Loading /></el-icon>
      <p>正在获取播放凭证...</p>
    </div>
    <div v-else-if="error" style="text-align: center; padding: 40px; color: #f56c6c">
      {{ error }}
    </div>
    <div v-else>
      <div id="aliplayer-container" style="width: 100%; height: 400px; background: #000"></div>
      <el-descriptions v-if="playAuth" :column="1" border size="small" style="margin-top: 12px">
        <el-descriptions-item label="videoId">{{ videoId }}</el-descriptions-item>
        <el-descriptions-item label="playAuth">
          <span style="word-break: break-all; font-size: 11px">{{ playAuth }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="title">{{ title }}</el-descriptions-item>
      </el-descriptions>
      <p style="margin-top: 8px; color: #909399; font-size: 12px">
        提示：如需实际播放视频，请在 index.html 中引入阿里云 Prism Player SDK：<br>
        &lt;link href="https://g.alicdn.com/de/prismplayer/2.15.2/skins/default/aliplayer-min.css" rel="stylesheet"&gt;<br>
        &lt;script src="https://g.alicdn.com/de/prismplayer/2.15.2/aliplayer-min.js"&gt;&lt;/script&gt;
      </p>
    </div>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, watch, nextTick } from 'vue'
import { Loading } from '@element-plus/icons-vue'
import { fileApi } from '@/api/file'

const props = defineProps<{
  modelValue: boolean
  videoId: string
}>()
const emit = defineEmits(['update:modelValue'])

const visible = ref(props.modelValue)
watch(() => props.modelValue, (v) => { visible.value = v })
watch(visible, (v) => { emit('update:modelValue', v) })

const loading = ref(false)
const error = ref('')
const playAuth = ref('')
const title = ref('')

watch(() => props.modelValue, async (v) => {
  if (!v || !props.videoId) return
  loading.value = true
  error.value = ''
  playAuth.value = ''
  title.value = ''
  try {
    const res = await fileApi.vodPlayAuth(props.videoId)
    playAuth.value = res.playAuth
    title.value = res.title || ''

    // 如果页面引入了阿里云播放器 SDK，尝试初始化播放器
    await nextTick()
    const container = document.getElementById('aliplayer-container')
    if (container) container.innerHTML = ''
    if ((window as any).Aliplayer) {
      new (window as any).Aliplayer({
        id: 'aliplayer-container',
        vid: props.videoId,
        playauth: res.playAuth,
        width: '100%',
        height: '400px',
        autoplay: true,
      })
    }
  } catch (e: any) {
    error.value = e?.message || '获取播放凭证失败'
  } finally {
    loading.value = false
  }
})

function onClose() {
  playAuth.value = ''
  title.value = ''
  error.value = ''
  // 销毁播放器
  const container = document.getElementById('aliplayer-container')
  if (container) container.innerHTML = ''
}
</script>
