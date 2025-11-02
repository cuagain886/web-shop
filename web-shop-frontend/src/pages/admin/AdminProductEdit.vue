<template>
  <div class="admin-product-edit">
    <el-card class="page-card">
      <!-- 页面标题 -->
      <template #header>
        <div class="card-header">
          <h3>{{ isEdit ? '编辑商品' : '新增商品' }}</h3>
          <el-button @click="goBack">返回列表</el-button>
        </div>
      </template>

      <!-- 商品表单 -->
      <el-form
        ref="productFormRef"
        :model="productForm"
        :rules="productRules"
        label-width="120px"
        class="product-form"
      >
        <el-form-item label="商品名称" prop="name">
          <el-input
            v-model="productForm.name"
            placeholder="请输入商品名称"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="商品副标题" prop="subtitle">
          <el-input
            v-model="productForm.subtitle"
            placeholder="请输入商品副标题"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="商品分类" prop="categoryId">
          <el-select
            v-model="productForm.categoryId"
            placeholder="请选择商品分类"
            @change="handleCategoryChange"
          >
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="商品价格" prop="price">
          <el-input-number
            v-model="productForm.price"
            :min="0"
            :precision="2"
            :step="1"
            controls-position="right"
          />
          <span class="form-tip">元</span>
        </el-form-item>

        <el-form-item label="原价" prop="originalPrice">
          <el-input-number
            v-model="productForm.originalPrice"
            :min="0"
            :precision="2"
            :step="1"
            controls-position="right"
          />
          <span class="form-tip">元（用于显示折扣，可选）</span>
        </el-form-item>

        <el-form-item label="库存数量" prop="stock">
          <el-input-number
            v-model="productForm.stock"
            :min="0"
            :step="1"
            controls-position="right"
          />
          <span class="form-tip">件</span>
        </el-form-item>

        <el-form-item label="商品状态" prop="status">
          <el-radio-group v-model="productForm.status">
            <el-radio value="pending">待上架</el-radio>
            <el-radio value="active">立即上架</el-radio>
            <el-radio value="inactive">下架</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="商品图片" prop="images">
          <div class="image-upload-area">
            <el-upload
              v-model:file-list="imageFileList"
              :action="uploadAction"
              :http-request="handleUpload"
              list-type="picture-card"
              :limit="5"
              :on-exceed="handleExceed"
              :on-remove="handleRemove"
            >
              <el-icon><Plus /></el-icon>
            </el-upload>
            <div class="upload-tip">
              建议尺寸：800x800像素，最多上传5张
            </div>
          </div>
        </el-form-item>

        <el-form-item label="商品描述" prop="description">
          <el-input
            v-model="productForm.description"
            type="textarea"
            :rows="6"
            placeholder="请输入商品详细描述"
            maxlength="1000"
            show-word-limit
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            :loading="submitting"
            @click="handleSubmit"
          >
            {{ isEdit ? '保存修改' : '提交新增' }}
          </el-button>
          <el-button @click="goBack">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { 
  getAdminProductDetail, 
  addProduct, 
  updateProduct,
  uploadProductImage 
} from '@/api/product'
import { getCategoryList } from '@/api/category'

const route = useRoute()
const router = useRouter()

const productFormRef = ref(null)
const submitting = ref(false)
const categories = ref([])
const imageFileList = ref([])

// 是否为编辑模式
const isEdit = computed(() => !!route.params.id)

// 上传地址（实际不使用，由http-request处理）
const uploadAction = 'https://mock-upload'

const productForm = reactive({
  name: '',
  subtitle: '',
  categoryId: null,
  categoryName: '',
  price: 0,
  originalPrice: 0,
  stock: 0,
  status: 'pending',
  images: [],
  description: ''
})

const productRules = {
  name: [
    { required: true, message: '请输入商品名称', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择商品分类', trigger: 'change' }
  ],
  price: [
    { required: true, message: '请输入商品价格', trigger: 'blur' }
  ],
  stock: [
    { required: true, message: '请输入库存数量', trigger: 'blur' }
  ],
  images: [
    {
      validator: (rule, value, callback) => {
        if (!productForm.images || productForm.images.length === 0) {
          callback(new Error('请至少上传一张商品图片'))
        } else {
          callback()
        }
      },
      trigger: 'change'
    }
  ]
}

/**
 * 获取分类列表
 */
const fetchCategories = async () => {
  try {
    const list = await getCategoryList({ level: 2 })
    categories.value = list
  } catch (error) {
    console.error('获取分类列表失败:', error)
    ElMessage.error('获取分类列表失败')
  }
}

/**
 * 获取商品详情
 */
const fetchProductDetail = async (id) => {
  try {
    const product = await getAdminProductDetail(id)
    
    // 回填表单
    Object.assign(productForm, {
      name: product.name,
      subtitle: product.subtitle || '',
      categoryId: product.categoryId,
      categoryName: product.categoryName,
      price: product.price,
      originalPrice: product.originalPrice || product.price,
      stock: product.stock,
      status: product.status,
      images: product.images || [],
      description: product.description || ''
    })
    
    // 回填图片列表
    imageFileList.value = product.images.map((url, index) => ({
      uid: Date.now() + index,
      name: `image-${index + 1}`,
      status: 'success',
      url
    }))
  } catch (error) {
    console.error('获取商品详情失败:', error)
    ElMessage.error('获取商品详情失败')
    router.push('/admin/products')
  }
}

/**
 * 分类改变
 */
const handleCategoryChange = (categoryId) => {
  const category = categories.value.find(c => c.id === categoryId)
  if (category) {
    productForm.categoryName = category.name
  }
}

/**
 * 自定义上传
 */
const handleUpload = async ({ file }) => {
  try {
    const result = await uploadProductImage(file)
    
    // 添加到图片列表
    productForm.images.push(result.url)
    
    ElMessage.success('上传成功')
    return result
  } catch (error) {
    console.error('上传失败:', error)
    ElMessage.error('上传失败')
    throw error
  }
}

/**
 * 超出数量限制
 */
const handleExceed = () => {
  ElMessage.warning('最多上传5张图片')
}

/**
 * 移除图片
 */
const handleRemove = (file) => {
  const url = file.url || file.response?.url
  if (url) {
    const index = productForm.images.indexOf(url)
    if (index > -1) {
      productForm.images.splice(index, 1)
    }
  }
}

/**
 * 提交表单
 */
const handleSubmit = async () => {
  try {
    // 表单验证
    await productFormRef.value.validate()
    
    submitting.value = true
    
    const data = {
      ...productForm
    }
    
    if (isEdit.value) {
      // 更新商品
      await updateProduct(route.params.id, data)
      ElMessage.success('修改成功')
    } else {
      // 新增商品
      await addProduct(data)
      ElMessage.success('新增成功')
    }
    
    // 返回列表页
    router.push('/admin/products')
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
 * 返回
 */
const goBack = () => {
  router.back()
}

onMounted(async () => {
  await fetchCategories()
  
  // 如果是编辑模式，获取商品详情
  if (isEdit.value) {
    await fetchProductDetail(route.params.id)
  }
})
</script>

<style scoped>
.admin-product-edit {
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

.product-form {
  max-width: 800px;
}

.form-tip {
  margin-left: 10px;
  color: #909399;
  font-size: 14px;
}

.image-upload-area {
  width: 100%;
}

.upload-tip {
  margin-top: 10px;
  color: #909399;
  font-size: 13px;
}

:deep(.el-upload--picture-card) {
  width: 120px;
  height: 120px;
}

:deep(.el-upload-list--picture-card .el-upload-list__item) {
  width: 120px;
  height: 120px;
}
</style>


