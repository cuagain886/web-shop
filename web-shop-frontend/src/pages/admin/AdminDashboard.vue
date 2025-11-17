<template>
  <div class="admin-dashboard">
    <!-- 核心指标卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #409eff;">
              <el-icon :size="32"><ShoppingCart /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">今日订单</div>
              <div class="stat-value">{{ overview.today?.orderCount || 0 }}</div>
              <div class="stat-sub">已支付：{{ overview.today?.paidOrderCount || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #67c23a;">
              <el-icon :size="32"><Money /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">今日销售额</div>
              <div class="stat-value">¥{{ overview.today?.sales?.toFixed(2) || '0.00' }}</div>
              <div class="stat-sub">&nbsp;</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #e6a23c;">
              <el-icon :size="32"><TrendCharts /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">本月订单</div>
              <div class="stat-value">{{ overview.thisMonth?.orderCount || 0 }}</div>
              <div class="stat-sub">已支付：{{ overview.thisMonth?.paidOrderCount || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #f56c6c;">
              <el-icon :size="32"><Wallet /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">本月销售额</div>
              <div class="stat-value">¥{{ overview.thisMonth?.sales?.toFixed(2) || '0.00' }}</div>
              <div class="stat-sub">客单价：¥{{ overview.thisMonth?.avgOrderAmount?.toFixed(2) || '0.00' }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="charts-row">
      <!-- 销售趋势图 -->
      <el-col :span="14">
        <el-card>
          <template #header>
            <div class="card-header">
              <h3>近7天销售趋势</h3>
              <el-button text @click="refreshTrend">
                <el-icon><Refresh /></el-icon>
                刷新
              </el-button>
            </div>
          </template>
          <div ref="trendChartRef" class="chart-container" style="height: 350px;"></div>
        </el-card>
      </el-col>

      <!-- 订单状态分布 -->
      <el-col :span="10">
        <el-card>
          <template #header>
            <div class="card-header">
              <h3>订单状态分布</h3>
            </div>
          </template>
          <div ref="statusChartRef" class="chart-container" style="height: 350px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 商品销售排行 -->
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <h3>商品销售排行 Top10</h3>
              <el-radio-group v-model="rankingType" size="small" @change="loadRanking">
                <el-radio-button value="sales">按销售额</el-radio-button>
                <el-radio-button value="quantity">按销量</el-radio-button>
              </el-radio-group>
            </div>
          </template>

          <el-table :data="ranking" stripe style="width: 100%">
            <el-table-column label="排名" width="80" align="center">
              <template #default="{ $index }">
                <el-tag v-if="$index < 3" :type="['danger', 'warning', 'success'][$index]" effect="dark">
                  {{ $index + 1 }}
                </el-tag>
                <span v-else>{{ $index + 1 }}</span>
              </template>
            </el-table-column>

            <el-table-column label="商品信息" min-width="300">
              <template #default="{ row }">
                <div class="product-info">
                  <el-image
                    :src="row.image"
                    fit="cover"
                    style="width: 50px; height: 50px; border-radius: 4px; margin-right: 10px;"
                  />
                  <span>{{ row.productName }}</span>
                </div>
              </template>
            </el-table-column>

            <el-table-column label="销量" prop="quantity" width="120" align="center">
              <template #default="{ row }">
                <span class="highlight-text">{{ row.quantity }}</span>
              </template>
            </el-table-column>

            <el-table-column label="销售额" width="150" align="center">
              <template #default="{ row }">
                <span class="price-text">¥{{ row.sales.toFixed(2) }}</span>
              </template>
            </el-table-column>

            <el-table-column label="占比" width="150" align="center">
              <template #default="{ row }">
                <el-progress
                  :percentage="parseFloat(row.percentage)"
                  :stroke-width="16"
                  :text-inside="true"
                />
              </template>
            </el-table-column>
          </el-table>

          <el-empty v-if="ranking.length === 0" description="暂无销售数据" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import {
  ShoppingCart,
  Money,
  TrendCharts,
  Wallet,
  Refresh
} from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import {
  getSalesOverview,
  getSalesTrend,
  getProductRanking,
  getOrderStatusDistribution
} from '@/api/statistics'

const overview = reactive({
  today: {},
  thisMonth: {}
})

const trendData = ref([])
const statusData = ref([])
const ranking = ref([])
const rankingType = ref('sales')

const trendChartRef = ref(null)
const statusChartRef = ref(null)
let trendChartInstance = null
let statusChartInstance = null

/**
 * 加载概览数据
 */
const loadOverview = async () => {
  try {
    const data = await getSalesOverview()
    Object.assign(overview, data)
  } catch (error) {
    console.error('加载概览数据失败:', error)
    ElMessage.error('加载概览数据失败')
  }
}

/**
 * 加载销售趋势
 */
const loadTrend = async () => {
  try {
    trendData.value = await getSalesTrend(7)
    await nextTick()
    renderTrendChart()
  } catch (error) {
    console.error('加载销售趋势失败:', error)
    ElMessage.error('加载销售趋势失败')
  }
}

/**
 * 加载订单状态分布
 */
const loadStatusDistribution = async () => {
  try {
    statusData.value = await getOrderStatusDistribution()
    await nextTick()
    renderStatusChart()
  } catch (error) {
    console.error('加载订单状态分布失败:', error)
    ElMessage.error('加载订单状态分布失败')
  }
}

/**
 * 加载商品排行
 */
const loadRanking = async () => {
  try {
    ranking.value = await getProductRanking(rankingType.value, 10)
  } catch (error) {
    console.error('加载商品排行失败:', error)
    ElMessage.error('加载商品排行失败')
  }
}

/**
 * 渲染销售趋势图
 */
const renderTrendChart = () => {
  if (!trendChartRef.value) return

  if (!trendChartInstance) {
    trendChartInstance = echarts.init(trendChartRef.value)
  }

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross'
      }
    },
    legend: {
      data: ['销售额', '订单数']
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: trendData.value.map(item => item.date)
    },
    yAxis: [
      {
        type: 'value',
        name: '销售额(元)',
        position: 'left',
        axisLabel: {
          formatter: '¥{value}'
        }
      },
      {
        type: 'value',
        name: '订单数',
        position: 'right',
        axisLabel: {
          formatter: '{value}单'
        }
      }
    ],
    series: [
      {
        name: '销售额',
        type: 'line',
        smooth: true,
        data: trendData.value.map(item => item.sales),
        itemStyle: {
          color: '#409eff'
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64, 158, 255, 0.5)' },
            { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
          ])
        }
      },
      {
        name: '订单数',
        type: 'line',
        smooth: true,
        yAxisIndex: 1,
        data: trendData.value.map(item => item.orderCount),
        itemStyle: {
          color: '#67c23a'
        }
      }
    ]
  }

  trendChartInstance.setOption(option)
}

/**
 * 渲染订单状态分布图
 */
const renderStatusChart = () => {
  if (!statusChartRef.value) return

  if (!statusChartInstance) {
    statusChartInstance = echarts.init(statusChartRef.value)
  }

  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c}单 ({d}%)'
    },
    legend: {
      orient: 'horizontal',
      bottom: '5%',
      left: 'center',
      itemWidth: 12,
      itemHeight: 12,
      textStyle: {
        fontSize: 12
      }
    },
    series: [
      {
        name: '订单状态',
        type: 'pie',
        radius: ['35%', '60%'],
        center: ['50%', '45%'],
        avoidLabelOverlap: true,
        itemStyle: {
          borderRadius: 8,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: true,
          fontSize: 11,
          formatter: '{b}\n{d}%',
          lineHeight: 14
        },
        labelLine: {
          length: 10,
          length2: 10
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 14,
            fontWeight: 'bold'
          }
        },
        data: statusData.value,
        color: ['#e6a23c', '#409eff', '#67c23a', '#909399', '#f56c6c']
      }
    ]
  }

  statusChartInstance.setOption(option)
}

/**
 * 刷新趋势图
 */
const refreshTrend = async () => {
  await loadTrend()
  ElMessage.success('数据已刷新')
}

/**
 * 窗口大小改变时重绘图表
 */
const handleResize = () => {
  trendChartInstance?.resize()
  statusChartInstance?.resize()
}

/**
 * 初始化
 */
const init = async () => {
  await Promise.all([
    loadOverview(),
    loadTrend(),
    loadStatusDistribution(),
    loadRanking()
  ])
}

onMounted(() => {
  init()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  trendChartInstance?.dispose()
  statusChartInstance?.dispose()
})
</script>

<style scoped>
.admin-dashboard {
  padding: 0;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  cursor: pointer;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 15px;
}

.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.stat-info {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.stat-sub {
  font-size: 12px;
  color: #909399;
}

.charts-row {
  margin-bottom: 20px;
}

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

.chart-container {
  width: 100%;
}

.product-info {
  display: flex;
  align-items: center;
}

.highlight-text {
  font-size: 16px;
  font-weight: 600;
  color: #409eff;
}

.price-text {
  font-size: 16px;
  font-weight: 600;
  color: #f56c6c;
}
</style>
