import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/login/LoginView.vue'),
    meta: { public: true, title: '登录' },
  },
  {
    path: '/',
    component: () => import('@/layout/BasicLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'dashboard',
        component: () => import('@/views/dashboard/DashboardView.vue'),
        meta: { title: '仪表盘', icon: 'Odometer' },
      },
      {
        path: 'book',
        name: 'book',
        component: () => import('@/views/book/BookListView.vue'),
        meta: { title: '图书管理', icon: 'Reading' },
      },
      {
        path: 'teacher',
        name: 'teacher',
        component: () => import('@/views/teacher/TeacherListView.vue'),
        meta: { title: '教师管理', icon: 'User' },
      },
      {
        path: 'subject',
        name: 'subject',
        component: () => import('@/views/subject/SubjectTreeView.vue'),
        meta: { title: '课程分类', icon: 'Collection' },
      },
      {
        path: 'course',
        name: 'course',
        component: () => import('@/views/course/CourseListView.vue'),
        meta: { title: '课程列表', icon: 'VideoCamera' },
      },
      {
        path: 'course/new',
        name: 'course-new',
        component: () => import('@/views/course/CourseWizardView.vue'),
        meta: { title: '新建课程', hidden: true },
      },
      {
        path: 'course/:id/edit',
        name: 'course-edit',
        component: () => import('@/views/course/CourseWizardView.vue'),
        meta: { title: '编辑课程', hidden: true },
      },
      {
        path: 'banner',
        name: 'banner',
        component: () => import('@/views/banner/BannerListView.vue'),
        meta: { title: 'Banner 管理', icon: 'Picture' },
      },
      {
        path: 'home-preview',
        name: 'home-preview',
        component: () => import('@/views/home-preview/HomePreviewView.vue'),
        meta: { title: '首页预览', icon: 'HomeFilled' },
      },
      {
        path: 'upload',
        name: 'upload',
        component: () => import('@/views/upload/UploadView.vue'),
        meta: { title: '文件上传', icon: 'Upload' },
      },
    ],
  },
  { path: '/:pathMatch(.*)*', redirect: '/' },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to) => {
  const auth = useAuthStore()
  if (to.meta.public) return true
  if (!auth.isLoggedIn) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }
  return true
})

router.afterEach((to) => {
  const title = (to.meta.title as string) || '运管'
  document.title = `${title} · 在线学院运管`
})

export default router
