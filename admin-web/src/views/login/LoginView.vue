<template>
  <div class="login-page">
    <el-card class="login-card" shadow="always">
      <div class="brand">
        <h1>在线学院运管</h1>
        <p>Online College Admin</p>
      </div>
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="0"
        size="large"
        @submit.prevent="onSubmit"
      >
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" clearable>
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="密码"
            show-password
            clearable
            @keyup.enter="onSubmit"
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            style="width: 100%"
            :loading="loading"
            @click="onSubmit"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>
      <p class="tips">
        默认账号：<code>admin</code> / <code>admin123</code>（首次部署后请修改）
      </p>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()

const formRef = ref<FormInstance>()
const loading = ref(false)
const form = reactive({
  username: 'admin',
  password: '',
})
const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

async function onSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await auth.login(form)
    ElMessage.success(`欢迎回来，${auth.displayName}`)
    const redirect = (route.query.redirect as string) || '/dashboard'
    router.replace(redirect)
  } catch {
    /* 已由拦截器弹错 */
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.login-page {
  height: 100vh;
  background: linear-gradient(135deg, #409eff 0%, #67c23a 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}
.login-card {
  width: 380px;
  border-radius: 10px;
}
.brand {
  text-align: center;
  margin-bottom: 18px;
  h1 {
    margin: 0;
    font-size: 22px;
    color: #303133;
  }
  p {
    margin: 4px 0 0;
    font-size: 12px;
    color: #909399;
    letter-spacing: 1px;
  }
}
.tips {
  text-align: center;
  font-size: 12px;
  color: #909399;
  margin: 6px 0 0;
  code {
    color: #409eff;
    background: #ecf5ff;
    padding: 1px 4px;
    border-radius: 3px;
  }
}
</style>
