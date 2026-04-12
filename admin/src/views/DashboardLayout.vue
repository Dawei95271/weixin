<template>
  <div class="dashboard">
    <aside class="sidebar">
      <div>
        <p class="brand">CATERING OPS</p>
        <h2>餐饮后台</h2>
      </div>
      <div class="menu">
        <button :class="{ active: currentTab === 'orders' }" @click="currentTab = 'orders'">订单管理</button>
        <button :class="{ active: currentTab === 'categories' }" @click="currentTab = 'categories'">分类管理</button>
        <button :class="{ active: currentTab === 'dishes' }" @click="currentTab = 'dishes'">菜品管理</button>
        <button :class="{ active: currentTab === 'privateRooms' }" @click="currentTab = 'privateRooms'">包间预约</button>
        <button :class="{ active: currentTab === 'banquets' }" @click="currentTab = 'banquets'">宴席预约</button>
      </div>
      <button class="logout-btn" @click="logout">退出登录</button>
    </aside>

    <main class="content">
      <header class="header">
        <div>
          <p class="hello">欢迎回来</p>
          <h1>{{ realName }}</h1>
        </div>
        <el-button @click="loadAll">刷新数据</el-button>
      </header>

      <section class="stats">
        <div class="stat-card">
          <span>订单</span>
          <strong>{{ orders.length }}</strong>
        </div>
        <div class="stat-card">
          <span>分类</span>
          <strong>{{ categories.length }}</strong>
        </div>
        <div class="stat-card">
          <span>菜品</span>
          <strong>{{ dishes.length }}</strong>
        </div>
      </section>

      <section class="panel">
        <div class="panel-head">
          <h3>{{ title }}</h3>
          <el-button v-if="currentTab === 'categories'" type="primary" @click="openCategoryDialog()">新增分类</el-button>
          <el-button v-if="currentTab === 'dishes'" type="primary" @click="openDishDialog()">新增菜品</el-button>
        </div>

        <div v-if="currentTab === 'orders'" class="filter-row">
          <el-select v-model="orderFilters.orderStatus" clearable placeholder="按订单状态筛选" style="width: 220px">
            <el-option label="待支付" value="WAIT_PAY" />
            <el-option label="待接单" value="WAIT_ACCEPT" />
            <el-option label="制作中" value="COOKING" />
            <el-option label="配送中" value="DELIVERING" />
            <el-option label="已完成" value="COMPLETED" />
            <el-option label="已取消" value="CANCELLED" />
          </el-select>
          <el-select v-model="orderFilters.orderScene" clearable placeholder="按下单场景筛选" style="width: 220px">
            <el-option label="普通点餐" value="NORMAL" />
            <el-option label="客房送餐" value="ROOM_DELIVERY" />
            <el-option label="包间预点菜" value="PRIVATE_ROOM_PREORDER" />
          </el-select>
          <el-button type="primary" @click="loadAll">应用筛选</el-button>
          <el-button @click="resetOrderFilters">重置</el-button>
        </div>

        <el-table v-if="currentTab === 'orders'" :data="orders" stripe>
          <el-table-column prop="orderNo" label="订单号" min-width="220" />
          <el-table-column prop="orderScene" label="场景" width="170" />
          <el-table-column prop="contactName" label="联系人" width="120" />
          <el-table-column prop="contactPhone" label="联系电话" width="160" />
          <el-table-column prop="orderStatus" label="状态" width="140" />
          <el-table-column prop="payableAmount" label="金额" width="120" />
          <el-table-column label="操作" width="240" fixed="right">
            <template #default="{ row }">
              <div class="action-row">
                <el-button size="small" @click="changeOrderStatus(row.id, 'WAIT_ACCEPT')">接单</el-button>
                <el-button size="small" @click="changeOrderStatus(row.id, 'COOKING')">制作中</el-button>
                <el-button size="small" type="success" @click="changeOrderStatus(row.id, 'COMPLETED')">完成</el-button>
                <el-button size="small" type="info" @click="showOrderDetail(row.id)">详情</el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>

        <el-table v-else-if="currentTab === 'categories'" :data="categories" stripe>
          <el-table-column prop="name" label="分类名称" min-width="180" />
          <el-table-column prop="sort" label="排序" width="100" />
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'info'">
                {{ row.status === 1 ? '启用' : '停用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="240" fixed="right">
            <template #default="{ row }">
              <div class="action-row">
                <el-button size="small" @click="openCategoryDialog(row)">编辑</el-button>
                <el-button
                  size="small"
                  :type="row.status === 1 ? 'warning' : 'success'"
                  @click="changeCategoryStatus(row.id, row.status === 1 ? 0 : 1)"
                >
                  {{ row.status === 1 ? '停用' : '启用' }}
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>

        <el-table v-else-if="currentTab === 'dishes'" :data="dishes" stripe>
          <el-table-column prop="name" label="菜品名称" min-width="180" />
          <el-table-column prop="categoryName" label="分类" width="120" />
          <el-table-column prop="subtitle" label="副标题" min-width="180" />
          <el-table-column prop="basePrice" label="价格" width="100" />
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'info'">
                {{ row.status === 1 ? '上架' : '下架' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="240" fixed="right">
            <template #default="{ row }">
              <div class="action-row">
                <el-button size="small" @click="openDishDialog(row)">编辑</el-button>
                <el-button
                  size="small"
                  :type="row.status === 1 ? 'warning' : 'success'"
                  @click="changeDishStatus(row.id, row.status === 1 ? 0 : 1)"
                >
                  {{ row.status === 1 ? '下架' : '上架' }}
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>

        <el-table v-else-if="currentTab === 'privateRooms'" :data="privateRooms" stripe>
          <el-table-column prop="reservationNo" label="预约号" min-width="220" />
          <el-table-column prop="privateRoomId" label="包间ID" width="120" />
          <el-table-column prop="reserveDate" label="日期" width="140" />
          <el-table-column prop="timeslotCode" label="时段" width="140" />
          <el-table-column prop="contactName" label="联系人" width="120" />
          <el-table-column prop="reservationStatus" label="状态" width="140" />
          <el-table-column label="操作" width="240" fixed="right">
            <template #default="{ row }">
              <div class="action-row">
                <el-button size="small" @click="changePrivateRoomStatus(row.id, 'RESERVED')">确认预约</el-button>
                <el-button size="small" @click="changePrivateRoomStatus(row.id, 'ARRIVED')">已到店</el-button>
                <el-button size="small" type="danger" @click="changePrivateRoomStatus(row.id, 'CANCELLED')">取消</el-button>
                <el-button size="small" type="info" @click="showPrivateRoomDetail(row.id)">详情</el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>

        <el-table v-else :data="banquets" stripe>
          <el-table-column prop="reservationNo" label="预约号" min-width="220" />
          <el-table-column prop="banquetType" label="宴席类型" width="140" />
          <el-table-column prop="reserveDate" label="日期" width="140" />
          <el-table-column prop="guestCount" label="人数" width="100" />
          <el-table-column prop="contactName" label="联系人" width="120" />
          <el-table-column prop="status" label="状态" width="140" />
          <el-table-column label="操作" width="260" fixed="right">
            <template #default="{ row }">
              <div class="action-row">
                <el-button size="small" @click="changeBanquetStatus(row.id, 'CONTACTED')">已联系</el-button>
                <el-button size="small" type="success" @click="changeBanquetStatus(row.id, 'CONFIRMED')">已确认</el-button>
                <el-button size="small" type="danger" @click="changeBanquetStatus(row.id, 'CANCELLED')">取消</el-button>
                <el-button size="small" type="info" @click="showBanquetDetail(row.id)">详情</el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </section>
    </main>

    <el-dialog v-model="categoryDialogVisible" title="分类信息" width="460px">
      <el-form label-width="90px">
        <el-form-item label="分类名称">
          <el-input v-model="categoryForm.name" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="categoryForm.sort" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="categoryDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCategory">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="dishDialogVisible" title="菜品信息" width="560px">
      <el-form label-width="90px">
        <el-form-item label="分类">
          <el-select v-model="dishForm.categoryId" style="width: 100%">
            <el-option
              v-for="item in categories"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="菜品名称">
          <el-input v-model="dishForm.name" />
        </el-form-item>
        <el-form-item label="副标题">
          <el-input v-model="dishForm.subtitle" />
        </el-form-item>
        <el-form-item label="价格">
          <el-input-number v-model="dishForm.basePrice" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="dishForm.description" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dishDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitDish">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="orderDetailVisible" title="订单详情" width="720px">
      <template v-if="orderDetail">
        <div class="detail-grid">
          <div class="detail-block">
            <div class="detail-label">订单号</div>
            <div class="detail-value">{{ orderDetail.orderNo }}</div>
          </div>
          <div class="detail-block">
            <div class="detail-label">场景</div>
            <div class="detail-value">{{ orderDetail.orderScene }}</div>
          </div>
          <div class="detail-block">
            <div class="detail-label">联系人</div>
            <div class="detail-value">{{ orderDetail.contactName }}</div>
          </div>
          <div class="detail-block">
            <div class="detail-label">联系电话</div>
            <div class="detail-value">{{ orderDetail.contactPhone }}</div>
          </div>
          <div class="detail-block">
            <div class="detail-label">房号</div>
            <div class="detail-value">{{ orderDetail.roomNo || '无' }}</div>
          </div>
          <div class="detail-block">
            <div class="detail-label">状态</div>
            <div class="detail-value">{{ orderDetail.orderStatus }}</div>
          </div>
        </div>

        <el-divider />

        <el-table :data="orderDetail.items" stripe>
          <el-table-column prop="dishName" label="菜品" min-width="180" />
          <el-table-column prop="quantity" label="数量" width="100" />
          <el-table-column prop="unitPrice" label="单价" width="120" />
          <el-table-column prop="totalPrice" label="小计" width="120" />
        </el-table>
      </template>
    </el-dialog>

    <el-dialog v-model="privateRoomDetailVisible" title="包间预约详情" width="720px">
      <template v-if="privateRoomDetail">
        <div class="detail-grid">
          <div class="detail-block">
            <div class="detail-label">预约号</div>
            <div class="detail-value">{{ privateRoomDetail.reservationNo }}</div>
          </div>
          <div class="detail-block">
            <div class="detail-label">包间ID</div>
            <div class="detail-value">{{ privateRoomDetail.privateRoomId }}</div>
          </div>
          <div class="detail-block">
            <div class="detail-label">预约日期</div>
            <div class="detail-value">{{ privateRoomDetail.reserveDate }}</div>
          </div>
          <div class="detail-block">
            <div class="detail-label">时段</div>
            <div class="detail-value">{{ privateRoomDetail.timeslotCode }}</div>
          </div>
          <div class="detail-block">
            <div class="detail-label">联系人</div>
            <div class="detail-value">{{ privateRoomDetail.contactName }}</div>
          </div>
          <div class="detail-block">
            <div class="detail-label">联系电话</div>
            <div class="detail-value">{{ privateRoomDetail.contactPhone }}</div>
          </div>
          <div class="detail-block">
            <div class="detail-label">人数</div>
            <div class="detail-value">{{ privateRoomDetail.guestCount }}</div>
          </div>
          <div class="detail-block">
            <div class="detail-label">状态</div>
            <div class="detail-value">{{ privateRoomDetail.reservationStatus }}</div>
          </div>
        </div>

        <el-divider />

        <el-table :data="privateRoomDishRows" stripe>
          <el-table-column prop="name" label="预点菜品" min-width="220" />
        </el-table>
      </template>
    </el-dialog>

    <el-dialog v-model="banquetDetailVisible" title="宴席预约详情" width="720px">
      <template v-if="banquetDetail">
        <div class="detail-grid">
          <div class="detail-block">
            <div class="detail-label">预约号</div>
            <div class="detail-value">{{ banquetDetail.reservationNo }}</div>
          </div>
          <div class="detail-block">
            <div class="detail-label">宴席类型</div>
            <div class="detail-value">{{ banquetDetail.banquetType }}</div>
          </div>
          <div class="detail-block">
            <div class="detail-label">预约日期</div>
            <div class="detail-value">{{ banquetDetail.reserveDate }}</div>
          </div>
          <div class="detail-block">
            <div class="detail-label">预计人数</div>
            <div class="detail-value">{{ banquetDetail.guestCount }}</div>
          </div>
          <div class="detail-block">
            <div class="detail-label">联系人</div>
            <div class="detail-value">{{ banquetDetail.contactName }}</div>
          </div>
          <div class="detail-block">
            <div class="detail-label">联系电话</div>
            <div class="detail-value">{{ banquetDetail.contactPhone }}</div>
          </div>
          <div class="detail-block">
            <div class="detail-label">预算</div>
            <div class="detail-value">¥{{ banquetDetail.budgetAmount }}</div>
          </div>
          <div class="detail-block">
            <div class="detail-label">状态</div>
            <div class="detail-value">{{ banquetDetail.status }}</div>
          </div>
        </div>

        <el-divider />

        <div class="detail-block">
          <div class="detail-label">需求说明</div>
          <div class="detail-value">{{ banquetDetail.requirementDesc || '无' }}</div>
        </div>

        <el-divider />

        <div class="panel-head">
          <h3>跟进记录</h3>
          <el-button type="primary" @click="openBanquetFollowDialog">新增跟进</el-button>
        </div>

        <el-table :data="banquetFollowRecords" stripe>
          <el-table-column prop="followContent" label="跟进内容" min-width="260" />
          <el-table-column prop="nextFollowTime" label="下次跟进时间" min-width="180" />
        </el-table>
      </template>
    </el-dialog>

    <el-dialog v-model="banquetFollowDialogVisible" title="新增宴席跟进" width="560px">
      <el-form label-width="110px">
        <el-form-item label="跟进内容">
          <el-input v-model="banquetFollowForm.followContent" type="textarea" />
        </el-form-item>
        <el-form-item label="下次跟进时间">
          <el-input
            v-model="banquetFollowForm.nextFollowTime"
            placeholder="例如 2026-04-13T10:00:00"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="banquetFollowDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitBanquetFollow">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  fetchBanquetReservations,
  fetchBanquetReservationDetail,
  fetchBanquetFollowRecords,
  fetchDishCategories,
  fetchDishes,
  fetchOrderDetail,
  fetchOrders,
  fetchPrivateRoomReservationDetail,
  fetchPrivateRoomReservations,
  createBanquetFollowRecord,
  saveDishCategory,
  saveDish,
  updateBanquetReservationStatus,
  updateDishCategoryStatus,
  updateDishStatus,
  updateOrderStatus,
  updatePrivateRoomReservationStatus
} from '../services/api'

const router = useRouter()
const currentTab = ref<'orders' | 'categories' | 'dishes' | 'privateRooms' | 'banquets'>('orders')
const orders = ref<any[]>([])
const categories = ref<any[]>([])
const dishes = ref<any[]>([])
const privateRooms = ref<any[]>([])
const banquets = ref<any[]>([])
const orderDetailVisible = ref(false)
const orderDetail = ref<any | null>(null)
const privateRoomDetailVisible = ref(false)
const privateRoomDetail = ref<any | null>(null)
const banquetDetailVisible = ref(false)
const banquetDetail = ref<any | null>(null)
const banquetFollowRecords = ref<any[]>([])
const banquetFollowDialogVisible = ref(false)
const banquetFollowForm = ref({
  reservationId: 0,
  followContent: '',
  nextFollowTime: ''
})
const categoryDialogVisible = ref(false)
const dishDialogVisible = ref(false)
const categoryForm = ref({
  id: undefined as number | undefined,
  name: '',
  sort: 0
})
const dishForm = ref({
  id: undefined as number | undefined,
  categoryId: 1,
  name: '',
  subtitle: '',
  description: '',
  basePrice: 0,
  supportsRoomDelivery: 1,
  isRecommend: 0
})
const orderFilters = ref({
  orderStatus: '',
  orderScene: ''
})

const realName = computed(() => localStorage.getItem('admin_real_name') || '管理员')
const title = computed(() => {
  if (currentTab.value === 'orders') return '订单总览'
  if (currentTab.value === 'categories') return '分类总览'
  if (currentTab.value === 'dishes') return '菜品总览'
  if (currentTab.value === 'privateRooms') return '包间预约总览'
  return '宴席预约总览'
})

const privateRoomDishRows = computed(() =>
  (privateRoomDetail.value?.preorderDishes || []).map((name: string) => ({ name }))
)

async function loadAll() {
  try {
    const [orderData, categoryData, dishData, roomData, banquetData] = await Promise.all([
      fetchOrders({
        orderStatus: orderFilters.value.orderStatus || undefined,
        orderScene: orderFilters.value.orderScene || undefined
      }),
      fetchDishCategories(),
      fetchDishes(),
      fetchPrivateRoomReservations(),
      fetchBanquetReservations()
    ])
    orders.value = orderData
    categories.value = categoryData
    dishes.value = dishData
    privateRooms.value = roomData
    banquets.value = banquetData
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '加载失败')
  }
}

function resetOrderFilters() {
  orderFilters.value = {
    orderStatus: '',
    orderScene: ''
  }
  loadAll()
}

async function changeOrderStatus(orderId: number, orderStatus: string) {
  try {
    await updateOrderStatus({ orderId, orderStatus })
    ElMessage.success('订单状态已更新')
    await loadAll()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '更新失败')
  }
}

async function showOrderDetail(orderId: number) {
  try {
    orderDetail.value = await fetchOrderDetail(orderId)
    orderDetailVisible.value = true
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '详情加载失败')
  }
}

async function showPrivateRoomDetail(reservationId: number) {
  try {
    privateRoomDetail.value = await fetchPrivateRoomReservationDetail(reservationId)
    privateRoomDetailVisible.value = true
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '详情加载失败')
  }
}

async function showBanquetDetail(reservationId: number) {
  try {
    const [detail, follows] = await Promise.all([
      fetchBanquetReservationDetail(reservationId),
      fetchBanquetFollowRecords(reservationId)
    ])
    banquetDetail.value = detail
    banquetFollowRecords.value = follows
    banquetDetailVisible.value = true
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '详情加载失败')
  }
}

function openBanquetFollowDialog() {
  banquetFollowForm.value = {
    reservationId: banquetDetail.value?.id || 0,
    followContent: '',
    nextFollowTime: ''
  }
  banquetFollowDialogVisible.value = true
}

async function submitBanquetFollow() {
  try {
    await createBanquetFollowRecord({
      reservationId: banquetFollowForm.value.reservationId,
      followContent: banquetFollowForm.value.followContent,
      nextFollowTime: banquetFollowForm.value.nextFollowTime || undefined
    })
    ElMessage.success('跟进记录已保存')
    banquetFollowDialogVisible.value = false
    if (banquetDetail.value?.id) {
      banquetFollowRecords.value = await fetchBanquetFollowRecords(banquetDetail.value.id)
    }
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '保存失败')
  }
}

async function changePrivateRoomStatus(reservationId: number, reservationStatus: string) {
  try {
    await updatePrivateRoomReservationStatus({ reservationId, reservationStatus })
    ElMessage.success('包间预约状态已更新')
    await loadAll()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '更新失败')
  }
}

async function changeBanquetStatus(reservationId: number, status: string) {
  try {
    await updateBanquetReservationStatus({ reservationId, status })
    ElMessage.success('宴席预约状态已更新')
    await loadAll()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '更新失败')
  }
}

async function changeDishStatus(id: number, status: number) {
  try {
    await updateDishStatus({ id, status })
    ElMessage.success('菜品状态已更新')
    await loadAll()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '更新失败')
  }
}

async function changeCategoryStatus(id: number, status: number) {
  try {
    await updateDishCategoryStatus({ id, status })
    ElMessage.success('分类状态已更新')
    await loadAll()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '更新失败')
  }
}

function openCategoryDialog(row?: any) {
  if (row) {
    categoryForm.value = {
      id: row.id,
      name: row.name,
      sort: row.sort
    }
  } else {
    categoryForm.value = {
      id: undefined,
      name: '',
      sort: 0
    }
  }
  categoryDialogVisible.value = true
}

function openDishDialog(row?: any) {
  if (row) {
    dishForm.value = {
      id: row.id,
      categoryId: row.categoryId,
      name: row.name,
      subtitle: row.subtitle || '',
      description: row.description || '',
      basePrice: Number(row.basePrice),
      supportsRoomDelivery: row.supportsRoomDelivery ?? 1,
      isRecommend: row.isRecommend ?? 0
    }
  } else {
    dishForm.value = {
      id: undefined,
      categoryId: 1,
      name: '',
      subtitle: '',
      description: '',
      basePrice: 0,
      supportsRoomDelivery: 1,
      isRecommend: 0
    }
  }
  dishDialogVisible.value = true
}

async function submitCategory() {
  try {
    await saveDishCategory({
      ...categoryForm.value,
      sort: Number(categoryForm.value.sort)
    })
    ElMessage.success('分类已保存')
    categoryDialogVisible.value = false
    await loadAll()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '保存失败')
  }
}

async function submitDish() {
  try {
    await saveDish({
      ...dishForm.value,
      basePrice: Number(dishForm.value.basePrice)
    })
    ElMessage.success('菜品已保存')
    dishDialogVisible.value = false
    await loadAll()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '保存失败')
  }
}

function logout() {
  localStorage.removeItem('admin_token')
  localStorage.removeItem('admin_real_name')
  router.push('/login')
}

onMounted(() => {
  loadAll()
})
</script>

<style scoped>
.dashboard {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 260px 1fr;
}

.sidebar {
  padding: 28px 22px;
  background: linear-gradient(180deg, #2b1d13 0%, #4b2e1d 100%);
  color: #f4eadb;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.brand {
  margin: 0;
  color: #dcb07a;
  font-size: 12px;
  letter-spacing: 0.24em;
}

.sidebar h2 {
  margin-top: 10px;
  font-size: 30px;
}

.menu {
  display: grid;
  gap: 12px;
}

.menu button,
.logout-btn {
  border: 0;
  border-radius: 14px;
  padding: 14px 16px;
  color: inherit;
  background: rgba(255, 255, 255, 0.08);
  cursor: pointer;
  text-align: left;
}

.menu button.active {
  background: linear-gradient(135deg, #d8a35d, #9f6731);
}

.content {
  padding: 28px;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.hello {
  margin: 0;
  color: #8b5e34;
}

.header h1 {
  margin: 8px 0 0;
  font-size: 34px;
}

.stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 18px;
  margin: 24px 0;
}

.stat-card,
.panel {
  border-radius: 22px;
  padding: 22px;
  background: rgba(255, 250, 242, 0.92);
  box-shadow: 0 24px 60px rgba(101, 67, 33, 0.08);
}

.action-row {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.panel-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.filter-row {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  margin-bottom: 12px;
}

.detail-block {
  padding: 14px 16px;
  border-radius: 14px;
  background: rgba(244, 239, 231, 0.8);
}

.detail-label {
  color: #8b5e34;
  font-size: 13px;
}

.detail-value {
  margin-top: 8px;
  font-size: 15px;
  font-weight: 600;
}

.stat-card span {
  color: #8b5e34;
}

.stat-card strong {
  display: block;
  margin-top: 16px;
  font-size: 42px;
}
</style>
