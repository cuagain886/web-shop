<template>
  <div class="admin-announcement-container">
    <div class="header">
      <h1>公告管理</h1>
      <button class="btn-primary" @click="showCreateDialog = true">
        <i class="icon-plus"></i> 新建公告
      </button>
    </div>

    <!-- 搜索和筛选 -->
    <div class="filter-section">
      <div class="filter-group">
        <label>状态：</label>
        <select v-model="filterStatus" @change="loadAnnouncements">
          <option value="">全部</option>
          <option value="0">草稿</option>
          <option value="1">已发布</option>
        </select>
      </div>
    </div>

    <!-- 公告列表 -->
    <div class="announcement-list">
      <table class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>标题</th>
            <th>类型</th>
            <th>状态</th>
            <th>置顶</th>
            <th>创建时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in announcements" :key="item.id">
            <td>{{ item.id }}</td>
            <td>{{ item.title }}</td>
            <td>
              <span :class="['badge', getTypeClass(item.type)]">
                {{ getTypeName(item.type) }}
              </span>
            </td>
            <td>
              <span :class="['badge', item.status === 1 ? 'badge-success' : 'badge-warning']">
                {{ item.status === 1 ? '已发布' : '草稿' }}
              </span>
            </td>
            <td>
              <span v-if="item.isTop === 1" class="badge badge-info">置顶</span>
              <span v-else class="text-muted">-</span>
            </td>
            <td>{{ formatDate(item.createdTime) }}</td>
            <td class="action-buttons">
              <button class="btn-sm btn-info" @click="editAnnouncement(item)">编辑</button>
              <button v-if="item.status === 0" class="btn-sm btn-success" @click="publishAnnouncement(item.id)">发布</button>
              <button class="btn-sm btn-danger" @click="deleteAnnouncement(item.id)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-if="announcements.length === 0" class="empty-state">
        <p>暂无公告</p>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination" v-if="totalPages > 1">
      <button @click="previousPage" :disabled="currentPage === 1">上一页</button>
      <span>第 {{ currentPage }} / {{ totalPages }} 页</span>
      <button @click="nextPage" :disabled="currentPage === totalPages">下一页</button>
    </div>

    <!-- 创建/编辑对话框 -->
    <div v-if="showCreateDialog" class="modal-overlay" @click="closeDialog">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h2>{{ editingId ? '编辑公告' : '新建公告' }}</h2>
          <button class="btn-close" @click="closeDialog">&times;</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>标题 *</label>
            <input v-model="formData.title" type="text" placeholder="请输入公告标题" />
          </div>
          <div class="form-group">
            <label>内容 *</label>
            <textarea v-model="formData.content" placeholder="请输入公告内容" rows="6"></textarea>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>类型</label>
              <select v-model="formData.type">
                <option value="1">普通公告</option>
                <option value="2">重要公告</option>
                <option value="3">紧急公告</option>
              </select>
            </div>
            <div class="form-group">
              <label>
                <input v-model="formData.isTop" type="checkbox" :true-value="1" :false-value="0" />
                置顶
              </label>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-secondary" @click="closeDialog">取消</button>
          <button class="btn-primary" @click="saveAnnouncement">保存</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {
  pageAnnouncements,
  createAnnouncement,
  updateAnnouncement,
  deleteAnnouncement,
  publishAnnouncement
} from '@/api/announcement'

export default {
  name: 'AdminAnnouncementManage',
  data() {
    return {
      announcements: [],
      currentPage: 1,
      pageSize: 10,
      totalPages: 1,
      filterStatus: '',
      showCreateDialog: false,
      editingId: null,
      formData: {
        title: '',
        content: '',
        type: 1,
        isTop: 0
      }
    }
  },
  mounted() {
    this.loadAnnouncements()
  },
  methods: {
    async loadAnnouncements() {
      try {
        const status = this.filterStatus ? parseInt(this.filterStatus) : null
        const response = await pageAnnouncements(this.currentPage, this.pageSize, status)
        // response 是 PageResult 对象，包含 records 和 pages 字段
        this.announcements = response.records || []
        this.totalPages = response.pages || 1
      } catch (error) {
        console.error('加载公告失败:', error)
        alert('加载公告失败')
      }
    },
    editAnnouncement(item) {
      this.editingId = item.id
      this.formData = {
        title: item.title,
        content: item.content,
        type: item.type,
        isTop: item.isTop
      }
      this.showCreateDialog = true
    },
    async saveAnnouncement() {
      if (!this.formData.title || !this.formData.content) {
        alert('请填写标题和内容')
        return
      }

      try {
        let response
        if (this.editingId) {
          response = await updateAnnouncement(this.editingId, this.formData)
        } else {
          response = await createAnnouncement(this.formData)
        }

        // response 是字符串消息
        alert(this.editingId ? '公告更新成功' : '公告创建成功')
        this.closeDialog()
        this.loadAnnouncements()
      } catch (error) {
        console.error('保存公告失败:', error)
        alert('保存公告失败')
      }
    },
    async deleteAnnouncement(id) {
      if (!confirm('确定要删除这条公告吗？')) {
        return
      }

      try {
        const response = await deleteAnnouncement(id)
        alert('公告删除成功')
        this.loadAnnouncements()
      } catch (error) {
        console.error('删除公告失败:', error)
        alert('删除公告失败')
      }
    },
    async publishAnnouncement(id) {
      try {
        const response = await publishAnnouncement(id)
        alert('公告发布成功')
        this.loadAnnouncements()
      } catch (error) {
        console.error('发布公告失败:', error)
        alert('发布公告失败')
      }
    },
    closeDialog() {
      this.showCreateDialog = false
      this.editingId = null
      this.formData = {
        title: '',
        content: '',
        type: 1,
        isTop: 0
      }
    },
    previousPage() {
      if (this.currentPage > 1) {
        this.currentPage--
        this.loadAnnouncements()
      }
    },
    nextPage() {
      if (this.currentPage < this.totalPages) {
        this.currentPage++
        this.loadAnnouncements()
      }
    },
    getTypeName(type) {
      const types = {
        1: '普通公告',
        2: '重要公告',
        3: '紧急公告'
      }
      return types[type] || '未知'
    },
    getTypeClass(type) {
      const classes = {
        1: 'badge-secondary',
        2: 'badge-warning',
        3: 'badge-danger'
      }
      return classes[type] || 'badge-secondary'
    },
    formatDate(date) {
      if (!date) return '-'
      return new Date(date).toLocaleString('zh-CN')
    }
  }
}
</script>

<style scoped>
.admin-announcement-container {
  padding: 20px;
  background-color: #f5f5f5;
  min-height: 100vh;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  background-color: white;
  padding: 20px;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.header h1 {
  margin: 0;
  font-size: 24px;
  color: #333;
}

.filter-section {
  background-color: white;
  padding: 15px;
  margin-bottom: 20px;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 10px;
}

.filter-group label {
  font-weight: 500;
  color: #333;
}

.filter-group select {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.announcement-list {
  background-color: white;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.table {
  width: 100%;
  border-collapse: collapse;
}

.table thead {
  background-color: #f9f9f9;
  border-bottom: 2px solid #ddd;
}

.table th {
  padding: 12px;
  text-align: left;
  font-weight: 600;
  color: #333;
}

.table td {
  padding: 12px;
  border-bottom: 1px solid #eee;
}

.table tbody tr:hover {
  background-color: #f9f9f9;
}

.badge {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 3px;
  font-size: 12px;
  font-weight: 500;
}

.badge-success {
  background-color: #d4edda;
  color: #155724;
}

.badge-warning {
  background-color: #fff3cd;
  color: #856404;
}

.badge-danger {
  background-color: #f8d7da;
  color: #721c24;
}

.badge-info {
  background-color: #d1ecf1;
  color: #0c5460;
}

.badge-secondary {
  background-color: #e2e3e5;
  color: #383d41;
}

.text-muted {
  color: #999;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.btn-sm {
  padding: 6px 12px;
  font-size: 12px;
  border: none;
  border-radius: 3px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-info {
  background-color: #17a2b8;
  color: white;
}

.btn-info:hover {
  background-color: #138496;
}

.btn-success {
  background-color: #28a745;
  color: white;
}

.btn-success:hover {
  background-color: #218838;
}

.btn-danger {
  background-color: #dc3545;
  color: white;
}

.btn-danger:hover {
  background-color: #c82333;
}

.empty-state {
  text-align: center;
  padding: 40px;
  color: #999;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  margin-top: 20px;
  padding: 20px;
  background-color: white;
  border-radius: 4px;
}

.pagination button {
  padding: 8px 16px;
  border: 1px solid #ddd;
  background-color: white;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.pagination button:hover:not(:disabled) {
  background-color: #f0f0f0;
}

.pagination button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  border-radius: 4px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
  max-width: 600px;
  width: 90%;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #eee;
}

.modal-header h2 {
  margin: 0;
  font-size: 20px;
  color: #333;
}

.btn-close {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #999;
}

.btn-close:hover {
  color: #333;
}

.modal-body {
  padding: 20px;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: 500;
  color: #333;
}

.form-group input[type="text"],
.form-group input[type="email"],
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  font-family: inherit;
}

.form-group textarea {
  resize: vertical;
}

.form-group input[type="checkbox"] {
  margin-right: 5px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 20px;
  border-top: 1px solid #eee;
}

.btn-primary,
.btn-secondary {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.btn-primary {
  background-color: #007bff;
  color: white;
  display: flex;
  align-items: center;
  gap: 5px;
}

.btn-primary:hover {
  background-color: #0056b3;
}

.btn-secondary {
  background-color: #6c757d;
  color: white;
}

.btn-secondary:hover {
  background-color: #545b62;
}

.icon-plus::before {
  content: '+';
}
</style>
