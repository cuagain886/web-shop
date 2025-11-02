<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <h3>收货地址管理</h3>
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增地址
        </el-button>
      </div>
    </template>

    <div class="address-list">
      <div
        v-for="address in addressList"
        :key="address.id"
        class="address-item"
        :class="{ 'is-default': address.isDefault }"
      >
        <div class="address-tag" v-if="address.isDefault">
          <el-tag type="danger" size="small">默认</el-tag>
        </div>
        
        <div class="address-content">
          <div class="address-header">
            <span class="receiver-name">{{ address.receiverName }}</span>
            <span class="receiver-phone">{{ address.phone }}</span>
          </div>
          <div class="address-detail">
            {{ address.province }} {{ address.city }} {{ address.district }} {{ address.detail }}
          </div>
        </div>

        <div class="address-actions">
          <el-button text type="primary" @click="handleEdit(address)">
            编辑
          </el-button>
          <el-button
            v-if="!address.isDefault"
            text
            type="success"
            @click="handleSetDefault(address.id)"
          >
            设为默认
          </el-button>
          <el-popconfirm
            title="确定要删除这个地址吗？"
            @confirm="handleDelete(address.id)"
          >
            <template #reference>
              <el-button text type="danger">删除</el-button>
            </template>
          </el-popconfirm>
        </div>
      </div>
    </div>

    <el-empty v-if="addressList.length === 0" description="暂无收货地址" />

    <!-- 新增/编辑地址对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
    >
      <el-form
        ref="addressFormRef"
        :model="addressForm"
        :rules="addressRules"
        label-width="100px"
      >
        <el-form-item label="收货人" prop="receiverName">
          <el-input
            v-model="addressForm.receiverName"
            placeholder="请输入收货人姓名"
            maxlength="20"
          />
        </el-form-item>

        <el-form-item label="联系电话" prop="phone">
          <el-input
            v-model="addressForm.phone"
            placeholder="请输入联系电话"
            maxlength="11"
          />
        </el-form-item>

        <el-form-item label="所在地区" prop="region">
          <el-cascader
            v-model="addressForm.region"
            :options="regionOptions"
            placeholder="请选择省/市/区"
            style="width: 100%;"
            @change="handleRegionChange"
          />
        </el-form-item>

        <el-form-item label="详细地址" prop="detail">
          <el-input
            v-model="addressForm.detail"
            type="textarea"
            :rows="3"
            placeholder="请输入详细地址，如街道、门牌号等"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="设为默认">
          <el-switch v-model="addressForm.isDefault" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">
          保存
        </el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getAddressList, addAddress, updateAddress, deleteAddress, setDefaultAddress } from '@/api/address'

const addressList = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增地址')
const saving = ref(false)
const addressFormRef = ref(null)

const addressForm = reactive({
  id: null,
  receiverName: '',
  phone: '',
  region: [],
  province: '',
  city: '',
  district: '',
  detail: '',
  isDefault: false
})

const addressRules = {
  receiverName: [
    { required: true, message: '请输入收货人姓名', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  region: [
    { required: true, message: '请选择所在地区', trigger: 'change' }
  ],
  detail: [
    { required: true, message: '请输入详细地址', trigger: 'blur' }
  ]
}

// Mock地区数据
const regionOptions = [
  {
    value: '北京',
    label: '北京',
    children: [
      {
        value: '北京市',
        label: '北京市',
        children: [
          { value: '朝阳区', label: '朝阳区' },
          { value: '海淀区', label: '海淀区' },
          { value: '西城区', label: '西城区' }
        ]
      }
    ]
  },
  {
    value: '广东',
    label: '广东',
    children: [
      {
        value: '广州市',
        label: '广州市',
        children: [
          { value: '天河区', label: '天河区' },
          { value: '越秀区', label: '越秀区' }
        ]
      },
      {
        value: '深圳市',
        label: '深圳市',
        children: [
          { value: '南山区', label: '南山区' },
          { value: '福田区', label: '福田区' }
        ]
      }
    ]
  }
]

/**
 * 加载地址列表
 */
const loadAddresses = async () => {
  try {
    const list = await getAddressList()
    addressList.value = list
  } catch (error) {
    console.error('加载地址失败:', error)
    ElMessage.error('加载地址失败')
  }
}

/**
 * 新增地址
 */
const handleAdd = () => {
  dialogTitle.value = '新增地址'
  resetForm()
  dialogVisible.value = true
}

/**
 * 编辑地址
 */
const handleEdit = (address) => {
  dialogTitle.value = '编辑地址'
  Object.assign(addressForm, {
    ...address,
    region: [address.province, address.city, address.district]
  })
  dialogVisible.value = true
}

/**
 * 保存地址
 */
const handleSave = async () => {
  try {
    await addressFormRef.value.validate()
    
    saving.value = true
    
    const data = {
      receiverName: addressForm.receiverName,
      phone: addressForm.phone,
      province: addressForm.province,
      city: addressForm.city,
      district: addressForm.district,
      detail: addressForm.detail,
      isDefault: addressForm.isDefault
    }

    if (addressForm.id) {
      await updateAddress(addressForm.id, data)
      ElMessage.success('地址更新成功')
    } else {
      await addAddress(data)
      ElMessage.success('地址添加成功')
    }
    
    dialogVisible.value = false
    loadAddresses()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('保存地址失败:', error)
      ElMessage.error('保存地址失败')
    }
  } finally {
    saving.value = false
  }
}

/**
 * 设为默认地址
 */
const handleSetDefault = async (id) => {
  try {
    await setDefaultAddress(id)
    ElMessage.success('已设为默认地址')
    loadAddresses()
  } catch (error) {
    console.error('设置默认地址失败:', error)
    ElMessage.error('设置默认地址失败')
  }
}

/**
 * 删除地址
 */
const handleDelete = async (id) => {
  try {
    await deleteAddress(id)
    ElMessage.success('地址删除成功')
    loadAddresses()
  } catch (error) {
    console.error('删除地址失败:', error)
    ElMessage.error('删除地址失败')
  }
}

/**
 * 地区改变
 */
const handleRegionChange = (value) => {
  if (value && value.length === 3) {
    addressForm.province = value[0]
    addressForm.city = value[1]
    addressForm.district = value[2]
  }
}

/**
 * 重置表单
 */
const resetForm = () => {
  addressForm.id = null
  addressForm.receiverName = ''
  addressForm.phone = ''
  addressForm.region = []
  addressForm.province = ''
  addressForm.city = ''
  addressForm.district = ''
  addressForm.detail = ''
  addressForm.isDefault = false
  addressFormRef.value?.clearValidate()
}

onMounted(() => {
  loadAddresses()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
}

.address-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 15px;
}

.address-item {
  position: relative;
  padding: 20px;
  border: 2px solid #e4e7ed;
  border-radius: 8px;
  transition: all 0.3s;
}

.address-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 12px rgba(64, 158, 255, 0.2);
}

.address-item.is-default {
  border-color: #f56c6c;
  background: #fff5f5;
}

.address-tag {
  position: absolute;
  top: 10px;
  right: 10px;
}

.address-content {
  margin-bottom: 15px;
}

.address-header {
  margin-bottom: 10px;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.receiver-name {
  margin-right: 15px;
}

.receiver-phone {
  color: #606266;
}

.address-detail {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
}

.address-actions {
  display: flex;
  gap: 10px;
  padding-top: 15px;
  border-top: 1px solid #e4e7ed;
}
</style>

