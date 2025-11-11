<template>
  <div class="admin-user-list">
    <el-card>
      <template #header>
        <h3>用户管理</h3>
      </template>

      <!-- 搜索筛选 -->
      <div class="filter-section">
        <el-form :model="filterForm" inline>
          <el-form-item label="关键词">
            <el-input
              v-model="filterForm.keyword"
              placeholder="用户名/手机号"
              clearable
              @clear="handleSearch"
              @keyup.enter="handleSearch"
              style="width: 200px;"
            />
          </el-form-item>

          <el-form-item label="用户状态">
            <el-select
              v-model="filterForm.status"
              placeholder="全部状态"
              clearable
              @change="handleSearch"
              style="width: 150px;"
            >
              <el-option label="全部" value="" />
              <el-option label="正常" value="active" />
              <el-option label="禁用" value="disabled" />
            </el-select>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
            <el-button @click="handleReset">
              <el-icon><RefreshRight /></el-icon>
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 用户表格 -->
      <el-table
        v-loading="loading"
        :data="userList"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        
        <el-table-column label="头像" width="80">
          <template #default="{ row }">
            <el-avatar :size="40" :src="row.avatar">
              {{ row.username.charAt(0).toUpperCase() }}
            </el-avatar>
          </template>
        </el-table-column>

        <el-table-column prop="username" label="用户名" width="150" />
        
        <el-table-column prop="nickname" label="昵称" width="150" />

        <el-table-column prop="phone" label="手机号" width="120" />

        <el-table-column prop="email" label="邮箱" min-width="180" />

        <el-table-column label="注册时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>

        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'active' ? 'success' : 'danger'">
              {{ row.status === 'active' ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" @click="handleViewDetail(row)">
              查看详情
            </el-button>
            
            <el-button
              v-if="row.status === 'active'"
              text
              type="danger"
              @click="handleDisableUser(row.id)"
            >
              禁用
            </el-button>
            
            <el-button
              v-else
              text
              type="success"
              @click="handleEnableUser(row.id)"
            >
              启用
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-section">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- 用户详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="用户详情"
      width="600px"
    >
      <el-descriptions v-if="currentUser" :column="2" border>
        <el-descriptions-item label="用户ID">{{ currentUser.id }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ currentUser.username }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{ currentUser.nickname }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ currentUser.phone }}</el-descriptions-item>
        <el-descriptions-item label="邮箱" :span="2">{{ currentUser.email }}</el-descriptions-item>
        <el-descriptions-item label="注册时间" :span="2">
          {{ formatDate(currentUser.createdAt) }}
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentUser.status === 'active' ? 'success' : 'danger'">
            {{ currentUser.status === 'active' ? '正常' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, RefreshRight } from '@element-plus/icons-vue'
import { getUserList, disableUser, enableUser } from '@/api/user'

const loading = ref(false)
const userList = ref([])
const detailDialogVisible = ref(false)
const currentUser = ref(null)

const filterForm = reactive({
  keyword: '',
  status: ''
})

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

/**
 * 获取用户列表
 */
const fetchUsers = async () => {
  try {
    loading.value = true
    
    const params = {
      pageNum: pagination.page,
      pageSize: pagination.pageSize,
      keyword: filterForm.keyword || undefined,
      status: filterForm.status === 'active' ? 1 : filterForm.status === 'disabled' ? 0 : undefined
    }
    
    const res = await getUserList(params)
    userList.value = res.records.map(user => ({
      ...user,
      status: user.status === 1 ? 'active' : 'disabled',
      createdAt: user.createdTime
    }))
    pagination.total = res.total
  } catch (error) {
    console.error('获取用户列表失败:', error)
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

/**
 * 搜索
 */
const handleSearch = () => {
  pagination.page = 1
  fetchUsers()
}

/**
 * 重置
 */
const handleReset = () => {
  filterForm.keyword = ''
  filterForm.status = ''
  pagination.page = 1
  fetchUsers()
}

/**
 * 分页改变
 */
const handleSizeChange = (size) => {
  pagination.pageSize = size
  pagination.page = 1
  fetchUsers()
}

const handlePageChange = (page) => {
  pagination.page = page
  fetchUsers()
}

/**
 * 查看详情
 */
const handleViewDetail = (user) => {
  currentUser.value = user
  detailDialogVisible.value = true
}

/**
 * 禁用用户
 */
const handleDisableUser = async (id) => {
  try {
    await ElMessageBox.confirm('确定要禁用该用户吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await disableUser(id)
    ElMessage.success('用户已禁用')
    fetchUsers()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('禁用用户失败:', error)
      ElMessage.error('禁用用户失败')
    }
  }
}

/**
 * 启用用户
 */
const handleEnableUser = async (id) => {
  try {
    await enableUser(id)
    ElMessage.success('用户已启用')
    fetchUsers()
  } catch (error) {
    console.error('启用用户失败:', error)
    ElMessage.error('启用用户失败')
  }
}

/**
 * 格式化日期
 */
const formatDate = (dateString) => {
  if (!dateString) return '-'
  return dateString
}

onMounted(() => {
  fetchUsers()
})
</script>

<style scoped>
.admin-user-list {
  padding: 0;
}

h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
}

.filter-section {
  margin-bottom: 20px;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.pagination-section {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>

