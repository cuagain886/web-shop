<template>
  <div class="announcement-detail-page">
    <el-container>
      <el-main>
        <div class="announcement-container">
          <!-- 返回按钮 -->
          <el-button 
            type="primary" 
            :icon="ArrowLeft" 
            @click="goBack"
            class="back-btn"
          >
            返回
          </el-button>

          <!-- 加载状态 -->
          <el-skeleton v-if="loading" :rows="10" animated />

          <!-- 公告内容 -->
          <div v-else-if="announcement" class="announcement-content">
            <!-- 公告头部 -->
            <div class="announcement-header">
              <h1 class="announcement-title">
                {{ announcement.title }}
                <el-tag 
                  v-if="announcement.isTop" 
                  type="danger" 
                  effect="dark"
                  class="top-tag"
                >
                  置顶
                </el-tag>
                <el-tag 
                  :type="getTypeTag(announcement.type).type"
                  class="type-tag"
                >
                  {{ getTypeTag(announcement.type).label }}
                </el-tag>
              </h1>
              
              <div class="announcement-meta">
                <span class="meta-item">
                  <el-icon><Clock /></el-icon>
                  发布时间: {{ formatDate(announcement.createdTime) }}
                </span>
                <span class="meta-item" v-if="announcement.updatedTime !== announcement.createdTime">
                  <el-icon><Edit /></el-icon>
                  更新时间: {{ formatDate(announcement.updatedTime) }}
                </span>
              </div>
            </div>

            <!-- 分隔线 -->
            <el-divider />

            <!-- 公告正文 -->
            <div class="announcement-body" v-html="announcement.content"></div>
          </div>

          <!-- 错误提示 -->
          <el-empty 
            v-else 
            description="公告不存在或已被删除"
            :image-size="200"
          >
            <el-button type="primary" @click="goBack">返回首页</el-button>
          </el-empty>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Clock, Edit } from '@element-plus/icons-vue'
import { getAnnouncement } from '@/api/announcement'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const announcement = ref(null)

// 获取公告类型标签
const getTypeTag = (type) => {
  const typeMap = {
    1: { label: '普通公告', type: 'info' },
    2: { label: '重要公告', type: 'warning' },
    3: { label: '活动公告', type: 'success' }
  }
  return typeMap[type] || typeMap[1]
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 返回上一页
const goBack = () => {
  router.back()
}

// 加载公告详情
const loadAnnouncement = async () => {
  const id = route.params.id
  if (!id) {
    ElMessage.error('公告ID无效')
    return
  }

  loading.value = true
  try {
    // request.js 的响应拦截器已经返回了 res.data，所以这里直接获取数据
    const data = await getAnnouncement(id)
    announcement.value = data
  } catch (error) {
    console.error('加载公告失败:', error)
    // 错误已经在 request.js 拦截器中处理并显示了
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadAnnouncement()
})
</script>

<style scoped>
.announcement-detail-page {
  min-height: 100vh;
  background-color: #f5f7fa;
  padding: 20px 0;
}

.announcement-container {
  max-width: 900px;
  margin: 0 auto;
  background: white;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.back-btn {
  margin-bottom: 20px;
}

.announcement-content {
  animation: fadeIn 0.3s ease-in;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.announcement-header {
  margin-bottom: 20px;
}

.announcement-title {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin: 0 0 15px 0;
  line-height: 1.4;
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.top-tag {
  font-size: 12px;
}

.type-tag {
  font-size: 12px;
}

.announcement-meta {
  display: flex;
  gap: 20px;
  color: #909399;
  font-size: 14px;
  flex-wrap: wrap;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 5px;
}

.meta-item .el-icon {
  font-size: 16px;
}

.announcement-body {
  font-size: 16px;
  line-height: 1.8;
  color: #606266;
  word-wrap: break-word;
  white-space: pre-wrap;
}

.announcement-body :deep(p) {
  margin: 15px 0;
}

.announcement-body :deep(h1),
.announcement-body :deep(h2),
.announcement-body :deep(h3) {
  margin: 20px 0 15px 0;
  color: #303133;
}

.announcement-body :deep(ul),
.announcement-body :deep(ol) {
  margin: 15px 0;
  padding-left: 30px;
}

.announcement-body :deep(li) {
  margin: 8px 0;
}

.announcement-body :deep(a) {
  color: #409eff;
  text-decoration: none;
}

.announcement-body :deep(a:hover) {
  text-decoration: underline;
}

.announcement-body :deep(img) {
  max-width: 100%;
  height: auto;
  border-radius: 4px;
  margin: 15px 0;
}

.announcement-body :deep(blockquote) {
  border-left: 4px solid #409eff;
  padding-left: 15px;
  margin: 15px 0;
  color: #606266;
  background-color: #f5f7fa;
  padding: 10px 15px;
  border-radius: 4px;
}

.announcement-body :deep(code) {
  background-color: #f5f7fa;
  padding: 2px 6px;
  border-radius: 3px;
  font-family: 'Courier New', monospace;
  font-size: 14px;
}

.announcement-body :deep(pre) {
  background-color: #f5f7fa;
  padding: 15px;
  border-radius: 4px;
  overflow-x: auto;
  margin: 15px 0;
}

.announcement-body :deep(pre code) {
  background: none;
  padding: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .announcement-container {
    padding: 20px 15px;
    border-radius: 0;
  }

  .announcement-title {
    font-size: 22px;
  }

  .announcement-body {
    font-size: 15px;
  }

  .announcement-meta {
    flex-direction: column;
    gap: 10px;
  }
}
</style>