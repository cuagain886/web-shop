<template>
  <div class="admin-category-manage">
    <el-card class="page-card">
      <!-- 页面标题 -->
      <template #header>
        <div class="card-header">
          <h3>分类管理</h3>
          <el-button type="primary" @click="handleAdd()">
            <el-icon><Plus /></el-icon>
            新增一级分类
          </el-button>
        </div>
      </template>

      <!-- 分类树形表格 -->
      <el-table
        v-loading="loading"
        :data="categoryTree"
        row-key="id"
        :tree-props="{ children: 'children' }"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        
        <el-table-column prop="name" label="分类名称" min-width="200" />
        
        <el-table-column label="层级" width="100">
          <template #default="{ row }">
            <el-tag :type="row.level === 1 ? 'primary' : 'success'">
              {{ row.level === 1 ? '一级' : '二级' }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="sort" label="排序" width="100" />
        
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'active' ? 'success' : 'info'">
              {{ row.status === 'active' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.level === 1"
              text
              type="primary"
              @click="handleAdd(row.id)"
            >
              添加子分类
            </el-button>
            
            <el-button text type="primary" @click="handleEdit(row)">
              编辑
            </el-button>
            
            <el-popconfirm
              title="确定要删除该分类吗？"
              @confirm="handleDelete(row.id)"
            >
              <template #reference>
                <el-button text type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
    >
      <el-form
        ref="categoryFormRef"
        :model="categoryForm"
        :rules="categoryRules"
        label-width="100px"
      >
        <el-form-item label="分类名称" prop="name">
          <el-input
            v-model="categoryForm.name"
            placeholder="请输入分类名称"
            maxlength="50"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="父分类" v-if="categoryForm.parentId !== undefined">
          <el-select
            v-model="categoryForm.parentId"
            placeholder="请选择父分类"
            :disabled="isEdit"
          >
            <el-option label="无（一级分类）" :value="0" />
            <el-option
              v-for="category in parentCategories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
          <div class="form-tip">注意：编辑时不可修改层级</div>
        </el-form-item>

        <el-form-item label="排序" prop="sort">
          <el-input-number
            v-model="categoryForm.sort"
            :min="0"
            :max="999"
            controls-position="right"
          />
          <span class="form-tip">数字越小越靠前</span>
        </el-form-item>

        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="categoryForm.status">
            <el-radio value="active">启用</el-radio>
            <el-radio value="inactive">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import {
  getCategoryTree,
  getCategoryList,
  addCategory,
  updateCategory,
  deleteCategory
} from '@/api/category'

const loading = ref(false)
const dialogVisible = ref(false)
const submitting = ref(false)
const categoryFormRef = ref(null)

const categoryTree = ref([])
const parentCategories = ref([]) // 一级分类列表（用于选择父分类）

const isEdit = ref(false)
const editCategoryId = ref(null)

const categoryForm = reactive({
  name: '',
  parentId: 0,
  sort: 99,
  status: 'active'
})

const categoryRules = {
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' }
  ]
}

const dialogTitle = computed(() => {
  return isEdit.value ? '编辑分类' : '新增分类'
})

/**
 * 获取分类树
 */
const fetchCategoryTree = async () => {
  try {
    loading.value = true
    const tree = await getCategoryTree()
    categoryTree.value = tree
  } catch (error) {
    console.error('获取分类树失败:', error)
    ElMessage.error('获取分类列表失败')
  } finally {
    loading.value = false
  }
}

/**
 * 获取一级分类列表
 */
const fetchParentCategories = async () => {
  try {
    const list = await getCategoryList({ level: 1 })
    parentCategories.value = list
  } catch (error) {
    console.error('获取一级分类失败:', error)
  }
}

/**
 * 新增分类
 */
const handleAdd = (parentId = 0) => {
  isEdit.value = false
  editCategoryId.value = null
  
  // 重置表单
  Object.assign(categoryForm, {
    name: '',
    parentId: parentId,
    sort: 99,
    status: 'active'
  })
  
  dialogVisible.value = true
}

/**
 * 编辑分类
 */
const handleEdit = (category) => {
  isEdit.value = true
  editCategoryId.value = category.id
  
  // 回填表单
  Object.assign(categoryForm, {
    name: category.name,
    parentId: category.parentId,
    sort: category.sort,
    status: category.status
  })
  
  dialogVisible.value = true
}

/**
 * 提交表单
 */
const handleSubmit = async () => {
  try {
    // 表单验证
    await categoryFormRef.value.validate()
    
    submitting.value = true
    
    if (isEdit.value) {
      // 更新分类
      await updateCategory(editCategoryId.value, {
        name: categoryForm.name,
        sort: categoryForm.sort,
        status: categoryForm.status
      })
      ElMessage.success('修改成功')
    } else {
      // 新增分类
      await addCategory(categoryForm)
      ElMessage.success('新增成功')
    }
    
    dialogVisible.value = false
    fetchCategoryTree()
    fetchParentCategories() // 刷新父分类列表
  } catch (error) {
    if (error !== 'cancel') {
      console.error('提交失败:', error)
      ElMessage.error(error.message || '操作失败')
    }
  } finally {
    submitting.value = false
  }
}

/**
 * 删除分类
 */
const handleDelete = async (id) => {
  try {
    await deleteCategory(id)
    ElMessage.success('删除成功')
    fetchCategoryTree()
    fetchParentCategories() // 刷新父分类列表
  } catch (error) {
    console.error('删除失败:', error)
    ElMessage.error(error.message || '删除失败')
  }
}

onMounted(() => {
  fetchCategoryTree()
  fetchParentCategories()
})
</script>

<style scoped>
.admin-category-manage {
  padding: 0;
}

.page-card {
  min-height: calc(100vh - 120px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.form-tip {
  margin-left: 10px;
  color: #909399;
  font-size: 13px;
}
</style>


