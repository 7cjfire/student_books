<template>
  <el-container class="app-container">
    <el-aside :width="collapsed ? '64px' : '220px'" class="sidebar">
      <div class="logo">
        <span v-if="!collapsed">在线学院运管</span>
        <span v-else>OC</span>
      </div>
      <el-menu
        :default-active="$route.path"
        :collapse="collapsed"
        router
        background-color="#001529"
        text-color="#bfbfbf"
        active-text-color="#fff"
      >
        <template v-for="r in menuItems" :key="r.path">
          <el-menu-item :index="'/' + r.path">
            <el-icon><component :is="r.meta?.icon" /></el-icon>
            <template #title>{{ r.meta?.title }}</template>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="topbar">
        <el-icon class="trigger" @click="collapsed = !collapsed">
          <component :is="collapsed ? 'Expand' : 'Fold'" />
        </el-icon>
        <div class="spacer" />
        <el-dropdown @command="onCommand">
          <span class="user">
            <el-avatar :size="28" :src="auth.user?.avatar">
              {{ (auth.displayName || 'A').slice(0, 1) }}
            </el-avatar>
            <span class="name">{{ auth.displayName }}</span>
            <el-icon><CaretBottom /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item disabled>
                <span style="color:#999">
                  角色: {{ auth.roles.join(', ') || '无' }}
                </span>
              </el-dropdown-item>
              <el-dropdown-item command="logout">
                <el-icon><SwitchButton /></el-icon>&nbsp;退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>

      <el-main class="main-area">
        <router-view v-slot="{ Component }">
          <keep-alive :include="cached">
            <component :is="Component" />
          </keep-alive>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { authApi } from '@/api/auth'

const router = useRouter()
useRoute()
const auth = useAuthStore()
const collapsed = ref(false)
const cached = ['book', 'teacher', 'subject', 'course', 'banner']

const menuItems = computed(() =>
  router.getRoutes().filter((r) => {
    return (
      r.meta?.title &&
      !r.meta?.public &&
      !r.meta?.hidden &&
      r.path !== '/' &&
      !r.path.startsWith('/login') &&
      r.path.split('/').length === 2 // 只显示一级菜单，过滤 course/:id/edit 等
    )
  })
)

async function onCommand(cmd: string) {
  if (cmd === 'logout') {
    try {
      await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        type: 'warning',
      })
    } catch {
      return
    }
    try {
      await authApi.logout()
    } catch {
      /* ignore */
    }
    auth.logout()
    router.replace('/login')
  }
}
</script>

<style scoped lang="scss">
.app-container {
  height: 100%;
}

.sidebar {
  background: #001529;
  transition: width 0.2s;
  overflow: hidden;
  .logo {
    height: 56px;
    line-height: 56px;
    text-align: center;
    color: #fff;
    font-size: 16px;
    font-weight: 600;
    letter-spacing: 1px;
  }
  :deep(.el-menu) {
    border-right: 0;
  }
}

.topbar {
  display: flex;
  align-items: center;
  background: #fff;
  border-bottom: 1px solid #eee;
  padding: 0 16px;
  .trigger {
    font-size: 20px;
    cursor: pointer;
  }
  .spacer {
    flex: 1;
  }
  .user {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    cursor: pointer;
    .name {
      color: #333;
    }
  }
}

.main-area {
  padding: 12px;
  background: #f5f7fa;
}
</style>
