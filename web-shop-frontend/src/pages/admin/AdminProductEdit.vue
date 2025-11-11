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

        <el-form-item label="库存数量" v-if="skuList.length > 0">
          <span style="font-size: 16px; font-weight: 500;">{{ productForm.stock }} 件</span>
          <span class="form-tip">（由SKU库存自动计算）</span>
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

        <el-form-item label="商品规格">
          <div class="specs-section">
            <el-button
              type="primary"
              size="small"
              @click="handleAddSpec"
            >
              添加规格
            </el-button>
            <div v-if="productForm.specs.length > 0" class="specs-list">
              <div
                v-for="(spec, index) in productForm.specs"
                :key="index"
                class="spec-item"
              >
                <el-input
                  v-model="spec.name"
                  placeholder="规格名称（如：颜色、尺寸）"
                  style="width: 200px;"
                />
                <div class="spec-values-input">
                  <el-tag
                    v-for="(value, vIndex) in spec.values"
                    :key="vIndex"
                    closable
                    @close="handleRemoveSpecValue(index, vIndex)"
                    style="margin-right: 8px;"
                  >
                    {{ value }}
                  </el-tag>
                  <el-input
                    v-model="specInputValues[index]"
                    placeholder="输入后按回车添加"
                    style="width: 150px;"
                    @keyup.enter="handleAddSpecValue(index)"
                  />
                </div>
                <el-button
                  type="danger"
                  size="small"
                  @click="handleRemoveSpec(index)"
                >
                  删除
                </el-button>
              </div>
            </div>
            <div class="spec-tip">
              提示：规格名称如"颜色"、"尺寸"等，规格值可添加多个选项供用户选择
            </div>
          </div>
        </el-form-item>

        <!-- SKU表格 -->
        <el-form-item label="SKU列表" v-if="productForm.specs.length > 0">
          <el-button type="primary" size="small" @click="generateSkus" style="margin-bottom: 10px;">
            生成SKU
          </el-button>
          <el-table v-if="skuList.length > 0" :data="skuList" border style="width: 100%">
            <el-table-column prop="skuName" label="规格组合" width="200" />
            <el-table-column label="价格" width="150">
              <template #default="{ row }">
                <el-input-number v-model="row.price" :min="0" :precision="2" :step="1" size="small" />
              </template>
            </el-table-column>
            <el-table-column label="原价" width="150">
              <template #default="{ row }">
                <el-input-number v-model="row.originalPrice" :min="0" :precision="2" :step="1" size="small" />
              </template>
            </el-table-column>
            <el-table-column label="库存" width="120">
              <template #default="{ row }">
                <el-input-number v-model="row.stock" :min="0" :step="1" size="small" @change="updateTotalStock" />
              </template>
            </el-table-column>
            <el-table-column label="SKU编码" width="150">
              <template #default="{ row }">
                <el-input v-model="row.skuCode" size="small" placeholder="自动生成" />
              </template>
            </el-table-column>
          </el-table>
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
const specInputValues = ref([])

// 是否为编辑模式
const isEdit = computed(() => !!route.params.id)

// 上传地址（实际不使用，由http-request处理）
const uploadAction = ''

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
  specs: [],
  description: '',
  skus: []
})

const skuList = ref([])

const productRules = {
   name: [
     { required: true, message: '请输入商品名称', trigger: 'blur' }
   ],
   categoryId: [
     { required: true, message: '请选择商品分类', trigger: 'change' }
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
    const response = await getCategoryList({ parentId: 0 })
    categories.value = response.data || response
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
    const response = await getAdminProductDetail(id)
    const product = response.data || response
    
    // 解析images字段（可能是JSON字符串）
    let images = []
    if (product.images) {
      images = typeof product.images === 'string'
        ? JSON.parse(product.images)
        : product.images
    }
    
    // 解析specs字段
    let specs = []
    if (product.specs) {
      specs = typeof product.specs === 'string'
        ? JSON.parse(product.specs)
        : product.specs
    }
    
    // 转换status（数字转字符串）
    const statusMap = { 0: 'inactive', 1: 'active' }
    const status = statusMap[product.status] || 'pending'
    
    // 回填表单
    Object.assign(productForm, {
      name: product.name,
      subtitle: product.subtitle || '',
      categoryId: product.categoryId,
      categoryName: product.categoryName,
      price: 0,
      originalPrice: 0,
      stock: product.stock,
      status: status,
      images: images,
      specs: specs,
      description: product.description || ''
    })
    
    // 回填图片列表
    imageFileList.value = images.map((url, index) => ({
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
    // 使用商品名称作为前缀（如果有的话）
    const prefix = productForm.name || 'product'
    const result = await uploadProductImage(file, prefix)
    
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
 * 添加规格
 */
const handleAddSpec = () => {
  const newIndex = productForm.specs.length
  productForm.specs.push({
    name: '',
    values: []
  })
  specInputValues.value[newIndex] = ''
}

/**
 * 删除规格
 */
const handleRemoveSpec = (index) => {
  productForm.specs.splice(index, 1)
  specInputValues.value.splice(index, 1)
}

/**
 * 添加规格值
 */
const handleAddSpecValue = (index) => {
  const value = specInputValues.value[index]?.trim()
  if (value && !productForm.specs[index].values.includes(value)) {
    productForm.specs[index].values.push(value)
    specInputValues.value[index] = ''
  }
}

/**
 * 删除规格值
 */
const handleRemoveSpecValue = (specIndex, valueIndex) => {
  productForm.specs[specIndex].values.splice(valueIndex, 1)
}

/**
 * 生成SKU列表
 */
const generateSkus = () => {
  if (productForm.specs.length === 0) {
    ElMessage.warning('请先添加规格')
    return
  }
  
  // 检查规格是否完整
  for (const spec of productForm.specs) {
    if (!spec.name || spec.values.length === 0) {
      ElMessage.warning('请完善规格信息')
      return
    }
  }
  
  // 生成SKU组合
  const combinations = generateCombinations(productForm.specs)
  skuList.value = combinations.map((combo, index) => ({
    skuName: combo.name,
    skuCode: generateSkuCode(combo.name),
    attributes: JSON.stringify(combo.attributes),
    price: null,
    originalPrice: null,
    stock: 0
  }))
  
  // 更新商品总库存
  updateTotalStock()
  
  ElMessage.success(`已生成 ${skuList.value.length} 个SKU，请设置每个SKU的价格和库存`)
}

/**
 * 更新商品总库存（SKU库存之和）
 */
const updateTotalStock = () => {
  if (skuList.value.length > 0) {
    productForm.stock = skuList.value.reduce((sum, sku) => sum + (sku.stock || 0), 0)
  }
}

/**
 * 生成规格组合
 */
const generateCombinations = (specs) => {
  if (specs.length === 0) return []
  
  const result = []
  const generate = (index, current, attrs) => {
    if (index === specs.length) {
      result.push({
        name: current.join('-'),
        attributes: attrs
      })
      return
    }
    
    const spec = specs[index]
    for (const value of spec.values) {
      generate(
        index + 1,
        [...current, value],
        { ...attrs, [spec.name]: value }
      )
    }
  }
  
  generate(0, [], {})
  return result
}

/**
 * 生成SKU编码
 */
const generateSkuCode = (skuName) => {
  const timestamp = Date.now().toString().slice(-6)
  const random = Math.random().toString(36).substring(2, 5).toUpperCase()
  return `SKU-${timestamp}-${random}`
}

/**
 * 提交表单
 */
const handleSubmit = async () => {
  try {
    // 表单验证
    await productFormRef.value.validate()
    
    // 验证SKU数据
    if (skuList.value.length > 0) {
      for (const sku of skuList.value) {
        if (sku.stock === null || sku.stock === undefined) {
          ElMessage.warning(`SKU "${sku.skuName}" 的库存不能为空`)
          return
        }
        if (!sku.price || sku.price === 0) {
          ElMessage.warning(`SKU "${sku.skuName}" 的价格不能为空或为0`)
          return
        }
        if (!sku.originalPrice || sku.originalPrice === 0) {
          ElMessage.warning(`SKU "${sku.skuName}" 的原价不能为空或为0`)
          return
        }
      }
    }
    
    submitting.value = true
    
    // 转换状态：字符串 -> 数字
    const statusMap = {
      'pending': 0,
      'active': 1,
      'inactive': 0
    }
    
    // 从SKU中获取商品价格（取第一个SKU的价格）
    let productPrice = 0
    let productOriginalPrice = 0
    if (skuList.value.length > 0) {
      productPrice = skuList.value[0].price || 0
      productOriginalPrice = skuList.value[0].originalPrice || 0
    }
    
    const data = {
      ...productForm,
      price: productPrice,
      originalPrice: productOriginalPrice,
      status: statusMap[productForm.status] || 0,
      images: JSON.stringify(productForm.images),
      specs: productForm.specs && productForm.specs.length > 0 ? JSON.stringify(productForm.specs) : null,
      coverImage: productForm.images[0] || '',
      skus: skuList.value.length > 0 ? skuList.value.map(sku => ({
        skuCode: sku.skuCode,
        skuName: sku.skuName,
        attributes: sku.attributes,
        price: sku.price,
        originalPrice: sku.originalPrice,
        stock: sku.stock
      })) : null
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

.specs-section {
  width: 100%;
}

.specs-list {
  margin-top: 15px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.spec-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.spec-values-input {
  flex: 1;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
  padding: 8px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  min-height: 40px;
}

.spec-tip {
  margin-top: 10px;
  color: #909399;
  font-size: 13px;
}
</style>

