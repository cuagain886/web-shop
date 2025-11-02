<template>
  <div class="admin-operation-log">
    <el-card>
      <template #header>
        <h3>操作日志</h3>
      </template>

      <!-- 筛选区域 -->
      <div class="filter-section">
        <el-form :model="filterForm" inline>
          <el-form-item label="操作类别">
            <el-select
              v-model="filterForm.category"
              placeholder="全部类别"
              clearable
              @change="handleSearch"
              style="width: 150px;"
            >
              <el-option label="全部" value="" />
              <el-option label="商品管理" value="商品管理" />
              <el-option label="订单管理" value="订单管理" />
              <el-option label="账户设置" value="账户设置" />
            </el-select>
          </el-form-item>

          <el-form-item label="关键词">
            <el-input
              v-model="filterForm.keyword"
              placeholder="搜索操作内容"
              clearable
              @clear="handleSearch"
              @keyup.enter="handleSearch"
              style="width: 200px;"
            />
          </el-form-item>

          <el-form-item label="时间范围">
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              @change="handleSearch"
            />
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

      <!-- 操作按钮 -->
      <div class="action-bar">
        <el-button type="danger" plain @click="handleClearLogs">
          <el-icon><Delete /></el-icon>
          清空日志
        </el-button>
      </div>

      <!-- 日志列表 -->
      <el-table
        v-loading="loading"
        :data="logList"
        stripe
        style="width: 100%"
      >
        <el-table-column label="操作类别" width="120">
          <template #default="{ row }">
            <el-tag :type="getCategoryType(row.category)" size="small">
              {{ row.category }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作类型" width="120">
          <template #default="{ row }">
            {{ row.typeName }}
          </template>
        </el-table-column>

        <el-table-column label="操作内容" min-width="300">
          <template #default="{ row }">
            <div class="content-cell">
              {{ row.content }}
            </div>
          </template>
        </el-table-column>

        <el-table-column label="操作对象" width="150">
          <template #default="{ row }">
            <span class="target-cell">{{ row.target || '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column label="操作人" width="120">
          <template #default="{ row }">
            {{ row.operator }}
          </template>
        </el-table-column>

        <el-table-column label="操作时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
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

      <!-- 空状态 -->
      <el-empty v-if="logList.length === 0 && !loading" description="暂无操作日志" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, RefreshRight, Delete } from '@element-plus/icons-vue'
import { getOperationLogs, clearOperationLogs } from '@/utils/operationLog'

const loading = ref(false)
const logList = ref([])
const dateRange = ref([])

const filterForm = reactive({
  category: '',
  keyword: ''
})

const pagination = reactive({
  page: 1,
  pageSize: 20,
  total: 0
})

/**
 * 获取操作日志列表
 */
const fetchLogs = () => {
  try {
    loading.value = true

    const params = {
      page: pagination.page,
      pageSize: pagination.pageSize,
      category: filterForm.category || undefined,
      keyword: filterForm.keyword || undefined,
      startDate: dateRange.value && dateRange.value[0] ? dateRange.value[0] : undefined,
      endDate: dateRange.value && dateRange.value[1] ? dateRange.value[1] : undefined
    }

    const result = getOperationLogs(params)
    logList.value = result.list
    pagination.total = result.total
  } catch (error) {
    console.error('获取操作日志失败:', error)
    ElMessage.error('获取操作日志失败')
  } finally {
    loading.value = false
  }
}

/**
 * 搜索
 */
const handleSearch = () => {
  pagination.page = 1
  fetchLogs()
}

/**
 * 重置
 */
const handleReset = () => {
  filterForm.category = ''
  filterForm.keyword = ''
  dateRange.value = []
  pagination.page = 1
  fetchLogs()
}

/**
 * 分页改变
 */
const handleSizeChange = (size) => {
  pagination.pageSize = size
  pagination.page = 1
  fetchLogs()
}

const handlePageChange = (page) => {
  pagination.page = page
  fetchLogs()
}

/**
 * 清空日志
 */
const handleClearLogs = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要清空所有操作日志吗？此操作不可恢复。',
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    clearOperationLogs()
    ElMessage.success('操作日志已清空')
    fetchLogs()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('清空日志失败:', error)
    }
  }
}

/**
 * 获取类别标签类型
 */
const getCategoryType = (category) => {
  const typeMap = {
    '商品管理': 'primary',
    '订单管理': 'success',
    '账户设置': 'warning',
    '其他': 'info'
  }
  return typeMap[category] || 'info'
}

/**
 * 格式化日期
 */
const formatDate = (dateString) => {
  const date = new Date(dateString)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

onMounted(() => {
  fetchLogs()
})
</script>

<style scoped>
.admin-operation-log {
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

.action-bar {
  margin-bottom: 15px;
  display: flex;
  justify-content: flex-end;
}

.content-cell {
  font-size: 14px;
  color: #333;
  line-height: 1.5;
}

.target-cell {
  font-size: 12px;
  color: #909399;
}

.pagination-section {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>

