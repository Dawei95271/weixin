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
          <span>{{ statCards[0].label }}</span>
          <strong>{{ statCards[0].value }}</strong>
        </div>
        <div class="stat-card">
          <span>{{ statCards[1].label }}</span>
          <strong>{{ statCards[1].value }}</strong>
        </div>
        <div class="stat-card">
          <span>{{ statCards[2].label }}</span>
          <strong>{{ statCards[2].value }}</strong>
        </div>
      </section>

      <section class="workspace-grid">
        <div class="panel workspace-panel">
          <div class="panel-head">
            <h3>今日待处理</h3>
            <span class="workspace-caption">点击即可跳转到对应列表</span>
          </div>
          <div class="workspace-cards">
            <button
              v-for="item in workspaceHighlights"
              :key="item.key"
              class="workspace-card"
              @click="applyWorkspaceShortcut(item.key)"
            >
              <span class="workspace-label">{{ item.label }}</span>
              <strong>{{ item.count }}</strong>
              <span class="workspace-desc">{{ item.desc }}</span>
            </button>
          </div>
        </div>

        <div class="panel workspace-panel">
          <div class="panel-head">
            <h3>工作建议</h3>
            <span class="workspace-caption">帮助当班人员快速进入处理节奏</span>
          </div>
          <div class="workspace-tips">
            <div v-for="tip in workspaceTips" :key="tip.title" class="workspace-tip">
              <span class="workspace-tip-title">{{ tip.title }}</span>
              <span class="workspace-tip-desc">{{ tip.desc }}</span>
            </div>
          </div>
        </div>
      </section>

      <section class="panel">
        <div class="panel-head">
          <h3>优先处理清单</h3>
          <span class="workspace-caption">按订单、包间、宴席三类任务汇总展示</span>
        </div>
        <div v-if="pendingTasks.length" class="task-list">
          <div v-for="task in pendingTasks" :key="task.key" class="task-item">
            <div class="task-copy">
              <div class="task-head">
                <span class="task-type">{{ task.typeLabel }}</span>
                <el-tag :type="task.tagType">{{ task.statusLabel }}</el-tag>
              </div>
              <strong class="task-title">{{ task.title }}</strong>
              <span class="task-desc">{{ task.desc }}</span>
            </div>
            <div class="task-actions">
              <el-button size="small" @click="openPendingTask(task)">查看详情</el-button>
              <el-button size="small" type="primary" @click="applyWorkspaceShortcut(task.shortcutKey)">进入列表</el-button>
            </div>
          </div>
        </div>
        <div v-else class="task-empty">
          当前没有需要优先处理的任务，列表会在有新订单或预约待办时自动展示。
        </div>
      </section>

      <section class="panel">
        <div class="panel-head">
          <h3>{{ title }}</h3>
          <el-button v-if="currentTab === 'categories'" type="primary" @click="openCategoryDialog()">新增分类</el-button>
          <el-button v-if="currentTab === 'dishes'" type="primary" @click="openDishDialog()">新增菜品</el-button>
        </div>

        <div v-if="currentTab === 'categories'" class="result-summary">
          当前共找到 {{ filteredCategories.length }} 条分类记录
          <span class="result-summary-detail">
            已启用 {{ categoryStatusSummary[0]?.count || 0 }} 条，已停用 {{ categoryStatusSummary[1]?.count || 0 }} 条
          </span>
        </div>

        <div v-if="currentTab === 'categories'" class="filter-row">
          <el-input
            v-model="categoryFilters.keyword"
            clearable
            placeholder="搜索分类名称"
            style="width: 260px"
          />
          <el-button @click="resetCategoryFilters">重置</el-button>
        </div>
        <div v-if="currentTab === 'categories'" class="status-summary">
          <button
            v-for="item in categoryStatusSummary"
            :key="item.value"
            class="status-pill"
            :class="{ active: categoryFilters.status === item.value }"
            @click="toggleCategoryStatusFilter(item.value)"
          >
            {{ item.label }} {{ item.count }}
          </button>
        </div>

        <div v-if="currentTab === 'orders'" class="filter-row">
          <el-input
            v-model="orderFilters.keyword"
            clearable
            placeholder="搜索订单号 / 联系人 / 电话"
            style="width: 260px"
          />
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
        <div v-if="currentTab === 'orders'" class="result-summary">
          当前共找到 {{ filteredOrders.length }} 条订单记录
        </div>
        <div v-if="currentTab === 'orders'" class="status-summary">
          <button
            v-for="item in orderStatusSummary"
            :key="item.value"
            class="status-pill"
            :class="{ active: orderFilters.orderStatus === item.value }"
            @click="toggleOrderStatusFilter(item.value)"
          >
            {{ item.label }} {{ item.count }}
          </button>
        </div>

        <div v-if="currentTab === 'privateRooms'" class="filter-row">
          <el-input
            v-model="privateRoomFilters.keyword"
            clearable
            placeholder="搜索预约号 / 联系人 / 电话"
            style="width: 260px"
          />
          <el-select
            v-model="privateRoomFilters.reservationStatus"
            clearable
            placeholder="按预约状态筛选"
            style="width: 220px"
          >
            <el-option label="待支付" value="WAIT_PAY" />
            <el-option label="已预约" value="RESERVED" />
            <el-option label="已到店" value="ARRIVED" />
            <el-option label="已完成" value="COMPLETED" />
            <el-option label="已取消" value="CANCELLED" />
          </el-select>
          <el-button type="primary" @click="loadAll">应用筛选</el-button>
          <el-button @click="resetPrivateRoomFilters">重置</el-button>
        </div>
        <div v-if="currentTab === 'privateRooms'" class="result-summary">
          当前共找到 {{ filteredPrivateRooms.length }} 条包间预约记录
        </div>
        <div v-if="currentTab === 'privateRooms'" class="status-summary">
          <button
            v-for="item in privateRoomStatusSummary"
            :key="item.value"
            class="status-pill"
            :class="{ active: privateRoomFilters.reservationStatus === item.value }"
            @click="togglePrivateRoomStatusFilter(item.value)"
          >
            {{ item.label }} {{ item.count }}
          </button>
        </div>

        <div v-if="currentTab === 'banquets'" class="filter-row">
          <el-input
            v-model="banquetFilters.keyword"
            clearable
            placeholder="搜索预约号 / 联系人 / 电话"
            style="width: 260px"
          />
          <el-select
            v-model="banquetFilters.status"
            clearable
            placeholder="按宴席状态筛选"
            style="width: 220px"
          >
            <el-option label="待跟进" value="WAIT_FOLLOW" />
            <el-option label="已联系" value="CONTACTED" />
            <el-option label="已确认" value="CONFIRMED" />
            <el-option label="已取消" value="CANCELLED" />
          </el-select>
          <el-button type="primary" @click="loadAll">应用筛选</el-button>
          <el-button @click="resetBanquetFilters">重置</el-button>
        </div>
        <div v-if="currentTab === 'banquets'" class="result-summary">
          当前共找到 {{ filteredBanquets.length }} 条宴席预约记录
        </div>
        <div v-if="currentTab === 'banquets'" class="status-summary">
          <button
            v-for="item in banquetStatusSummary"
            :key="item.value"
            class="status-pill"
            :class="{ active: banquetFilters.status === item.value }"
            @click="toggleBanquetStatusFilter(item.value)"
          >
            {{ item.label }} {{ item.count }}
          </button>
        </div>

        <div v-if="currentTab === 'dishes'" class="filter-row">
          <el-input
            v-model="dishFilters.keyword"
            clearable
            placeholder="搜索菜品名称 / 副标题"
            style="width: 260px"
          />
          <el-select
            v-model="dishFilters.categoryId"
            clearable
            placeholder="按分类筛选"
            style="width: 220px"
          >
            <el-option
              v-for="item in categories"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
          <el-button @click="resetDishFilters">重置</el-button>
        </div>
        <div v-if="currentTab === 'dishes'" class="result-summary">
          当前共找到 {{ filteredDishes.length }} 条菜品记录
          <span class="result-summary-detail">
            已上架 {{ dishStatusSummary[0]?.count || 0 }} 条，已下架 {{ dishStatusSummary[1]?.count || 0 }} 条
          </span>
        </div>
        <div v-if="currentTab === 'dishes'" class="status-summary">
          <button
            v-for="item in dishStatusSummary"
            :key="item.value"
            class="status-pill"
            :class="{ active: dishFilters.status === item.value }"
            @click="toggleDishStatusFilter(item.value)"
          >
            {{ item.label }} {{ item.count }}
          </button>
        </div>

        <el-table
          v-if="currentTab === 'orders'"
          :data="filteredOrders"
          stripe
          empty-text="暂无符合条件的订单记录"
        >
          <el-table-column label="订单号" min-width="220">
            <template #default="{ row }">
              <div class="cell-inline-action">
                <span>{{ row.orderNo }}</span>
                <el-button size="small" text type="primary" @click="copyText(row.orderNo)">复制</el-button>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="场景" width="170">
            <template #default="{ row }">
              <el-tag :type="orderSceneTagType(row.orderScene)">
                {{ orderSceneLabel(row.orderScene) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="contactName" label="联系人" width="120" />
          <el-table-column prop="contactPhone" label="联系电话" width="160" />
          <el-table-column label="快捷操作" width="120">
            <template #default="{ row }">
              <el-button size="small" text type="primary" @click="copyText(row.contactPhone)">复制电话</el-button>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="140">
            <template #default="{ row }">
              <el-tag :type="orderStatusTagType(row.orderStatus)">
                {{ orderStatusLabel(row.orderStatus) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="金额" width="120">
            <template #default="{ row }">
              {{ formatCurrency(row.payableAmount) }}
            </template>
          </el-table-column>
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

        <el-table
          v-else-if="currentTab === 'categories'"
          :data="filteredCategories"
          stripe
          empty-text="暂无分类记录"
        >
          <el-table-column prop="name" label="分类名称" min-width="180" />
          <el-table-column prop="sort" label="排序" width="100" />
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'info'">
                {{ row.status === 1 ? '启用' : '停用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="快捷信息" min-width="180">
            <template #default="{ row }">
              <div class="cell-inline-action">
                <span>{{ row.name }}</span>
                <el-button size="small" text type="primary" @click="copyText(row.name)">复制名称</el-button>
              </div>
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

        <el-table
          v-else-if="currentTab === 'dishes'"
          :data="filteredDishes"
          stripe
          empty-text="暂无符合条件的菜品记录"
        >
          <el-table-column prop="name" label="菜品名称" min-width="180" />
          <el-table-column prop="categoryName" label="分类" width="120" />
          <el-table-column prop="subtitle" label="副标题" min-width="180" />
          <el-table-column label="价格" width="120">
            <template #default="{ row }">
              {{ formatCurrency(row.basePrice) }}
            </template>
          </el-table-column>
          <el-table-column label="推荐" width="100">
            <template #default="{ row }">
              <el-tag :type="Number(row.isRecommend) === 1 ? 'warning' : 'info'">
                {{ Number(row.isRecommend) === 1 ? '推荐' : '普通' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="客房配送" width="120">
            <template #default="{ row }">
              <el-tag :type="Number(row.supportsRoomDelivery) === 1 ? 'success' : 'info'">
                {{ Number(row.supportsRoomDelivery) === 1 ? '支持' : '不支持' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'info'">
                {{ row.status === 1 ? '上架' : '下架' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="快捷信息" min-width="180">
            <template #default="{ row }">
              <div class="cell-inline-action">
                <span>{{ row.name }}</span>
                <el-button size="small" text type="primary" @click="copyText(row.name)">复制名称</el-button>
              </div>
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

        <el-table
          v-else-if="currentTab === 'privateRooms'"
          :data="filteredPrivateRooms"
          stripe
          empty-text="暂无符合条件的包间预约记录"
        >
          <el-table-column label="预约号" min-width="220">
            <template #default="{ row }">
              <div class="cell-inline-action">
                <span>{{ row.reservationNo }}</span>
                <el-button size="small" text type="primary" @click="copyText(row.reservationNo)">复制</el-button>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="privateRoomName" label="包间" width="140" />
          <el-table-column label="日期" width="140">
            <template #default="{ row }">
              {{ formatDate(row.reserveDate) }}
            </template>
          </el-table-column>
          <el-table-column prop="timeslotName" label="时段" width="120" />
          <el-table-column prop="contactName" label="联系人" width="120" />
          <el-table-column prop="contactPhone" label="联系电话" width="160" />
          <el-table-column label="快捷操作" width="120">
            <template #default="{ row }">
              <el-button size="small" text type="primary" @click="copyText(row.contactPhone)">复制电话</el-button>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="140">
            <template #default="{ row }">
              <el-tag :type="reservationStatusTagType(row.reservationStatus)">
                {{ reservationStatusLabel(row.reservationStatus) }}
              </el-tag>
            </template>
          </el-table-column>
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

        <el-table
          v-else
          :data="filteredBanquets"
          stripe
          empty-text="暂无符合条件的宴席预约记录"
        >
          <el-table-column label="预约号" min-width="220">
            <template #default="{ row }">
              <div class="cell-inline-action">
                <span>{{ row.reservationNo }}</span>
                <el-button size="small" text type="primary" @click="copyText(row.reservationNo)">复制</el-button>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="banquetType" label="宴席类型" width="140" />
          <el-table-column label="日期" width="140">
            <template #default="{ row }">
              {{ formatDate(row.reserveDate) }}
            </template>
          </el-table-column>
          <el-table-column prop="guestCount" label="人数" width="100" />
          <el-table-column prop="contactName" label="联系人" width="120" />
          <el-table-column prop="contactPhone" label="联系电话" width="160" />
          <el-table-column label="快捷操作" width="120">
            <template #default="{ row }">
              <el-button size="small" text type="primary" @click="copyText(row.contactPhone)">复制电话</el-button>
            </template>
          </el-table-column>
          <el-table-column label="预算" width="120">
            <template #default="{ row }">
              ¥{{ row.budgetAmount }}
            </template>
          </el-table-column>
          <el-table-column label="状态" width="140">
            <template #default="{ row }">
              <el-tag :type="banquetStatusTagType(row.status)">
                {{ banquetStatusLabel(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
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
        <el-form-item label="推荐菜">
          <el-switch
            v-model="dishForm.isRecommend"
            :active-value="1"
            :inactive-value="0"
          />
        </el-form-item>
        <el-form-item label="客房配送">
          <el-switch
            v-model="dishForm.supportsRoomDelivery"
            :active-value="1"
            :inactive-value="0"
          />
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
        <div class="dialog-actions">
          <el-button size="small" @click="changeOrderStatus(orderDetail.id, 'WAIT_ACCEPT')">接单</el-button>
          <el-button size="small" @click="changeOrderStatus(orderDetail.id, 'COOKING')">制作中</el-button>
          <el-button size="small" type="success" @click="changeOrderStatus(orderDetail.id, 'COMPLETED')">完成</el-button>
        </div>

        <div class="detail-grid">
          <div class="detail-block">
            <div class="detail-label">订单号</div>
            <div class="detail-value detail-inline-action">
              <span>{{ orderDetail.orderNo }}</span>
              <el-button
                size="small"
                text
                type="primary"
                @click="copyText(orderDetail.orderNo)"
              >
                复制
              </el-button>
            </div>
          </div>
          <div class="detail-block">
            <div class="detail-label">场景</div>
            <div class="detail-value">{{ orderSceneLabel(orderDetail.orderScene) }}</div>
          </div>
          <div class="detail-block">
            <div class="detail-label">联系人</div>
            <div class="detail-value">{{ orderDetail.contactName }}</div>
          </div>
          <div class="detail-block">
            <div class="detail-label">联系电话</div>
            <div class="detail-value detail-inline-action">
              <span>{{ orderDetail.contactPhone }}</span>
              <el-button
                size="small"
                text
                type="primary"
                @click="copyText(orderDetail.contactPhone)"
              >
                复制
              </el-button>
            </div>
          </div>
          <div class="detail-block">
            <div class="detail-label">房号</div>
            <div class="detail-value">{{ orderDetail.roomNo || '无' }}</div>
          </div>
          <div class="detail-block">
            <div class="detail-label">状态</div>
            <div class="detail-value">{{ orderStatusLabel(orderDetail.orderStatus) }}</div>
          </div>
        </div>

        <el-divider />

        <el-table :data="orderDetail.items" stripe>
          <el-table-column prop="dishName" label="菜品" min-width="180" />
          <el-table-column prop="quantity" label="数量" width="100" />
          <el-table-column label="单价" width="120">
            <template #default="{ row }">
              {{ formatCurrency(row.unitPrice) }}
            </template>
          </el-table-column>
          <el-table-column label="小计" width="120">
            <template #default="{ row }">
              {{ formatCurrency(row.totalPrice) }}
            </template>
          </el-table-column>
        </el-table>
      </template>
    </el-dialog>

    <el-dialog v-model="privateRoomDetailVisible" title="包间预约详情" width="720px">
      <template v-if="privateRoomDetail">
        <div class="dialog-actions">
          <el-button size="small" @click="changePrivateRoomStatus(privateRoomDetail.id, 'RESERVED')">确认预约</el-button>
          <el-button size="small" @click="changePrivateRoomStatus(privateRoomDetail.id, 'ARRIVED')">已到店</el-button>
          <el-button size="small" type="danger" @click="changePrivateRoomStatus(privateRoomDetail.id, 'CANCELLED')">取消</el-button>
        </div>

        <div class="detail-grid">
          <div class="detail-block">
            <div class="detail-label">预约号</div>
            <div class="detail-value detail-inline-action">
              <span>{{ privateRoomDetail.reservationNo }}</span>
              <el-button
                size="small"
                text
                type="primary"
                @click="copyText(privateRoomDetail.reservationNo)"
              >
                复制
              </el-button>
            </div>
          </div>
          <div class="detail-block">
            <div class="detail-label">包间</div>
            <div class="detail-value">{{ privateRoomDetail.privateRoomName || `包间-${privateRoomDetail.privateRoomId}` }}</div>
          </div>
          <div class="detail-block">
            <div class="detail-label">预约日期</div>
            <div class="detail-value">{{ formatDate(privateRoomDetail.reserveDate) }}</div>
          </div>
          <div class="detail-block">
            <div class="detail-label">时段</div>
            <div class="detail-value">{{ privateRoomDetail.timeslotName || privateRoomDetail.timeslotCode }}</div>
          </div>
          <div class="detail-block">
            <div class="detail-label">联系人</div>
            <div class="detail-value">{{ privateRoomDetail.contactName }}</div>
          </div>
          <div class="detail-block">
            <div class="detail-label">联系电话</div>
            <div class="detail-value detail-inline-action">
              <span>{{ privateRoomDetail.contactPhone }}</span>
              <el-button
                size="small"
                text
                type="primary"
                @click="copyText(privateRoomDetail.contactPhone)"
              >
                复制
              </el-button>
            </div>
          </div>
          <div class="detail-block">
            <div class="detail-label">人数</div>
            <div class="detail-value">{{ privateRoomDetail.guestCount }}</div>
          </div>
          <div class="detail-block">
            <div class="detail-label">状态</div>
            <div class="detail-value">{{ reservationStatusLabel(privateRoomDetail.reservationStatus) }}</div>
          </div>
          <div class="detail-block">
            <div class="detail-label">定金</div>
            <div class="detail-value detail-value--highlight">{{ formatCurrency(privateRoomDetail.depositAmount) }}</div>
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
        <div class="dialog-actions">
          <el-button size="small" @click="changeBanquetStatus(banquetDetail.id, 'CONTACTED')">已联系</el-button>
          <el-button size="small" type="success" @click="changeBanquetStatus(banquetDetail.id, 'CONFIRMED')">已确认</el-button>
          <el-button size="small" type="danger" @click="changeBanquetStatus(banquetDetail.id, 'CANCELLED')">取消</el-button>
        </div>

        <div class="detail-grid">
          <div class="detail-block">
            <div class="detail-label">预约号</div>
            <div class="detail-value detail-inline-action">
              <span>{{ banquetDetail.reservationNo }}</span>
              <el-button
                size="small"
                text
                type="primary"
                @click="copyText(banquetDetail.reservationNo)"
              >
                复制
              </el-button>
            </div>
          </div>
          <div class="detail-block">
            <div class="detail-label">宴席类型</div>
            <div class="detail-value">{{ banquetDetail.banquetType }}</div>
          </div>
          <div class="detail-block">
            <div class="detail-label">预约日期</div>
            <div class="detail-value">{{ formatDate(banquetDetail.reserveDate) }}</div>
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
            <div class="detail-value detail-inline-action">
              <span>{{ banquetDetail.contactPhone }}</span>
              <el-button
                size="small"
                text
                type="primary"
                @click="copyText(banquetDetail.contactPhone)"
              >
                复制
              </el-button>
            </div>
          </div>
          <div class="detail-block">
            <div class="detail-label">预算</div>
            <div class="detail-value">{{ formatCurrency(banquetDetail.budgetAmount) }}</div>
          </div>
          <div class="detail-block">
            <div class="detail-label">状态</div>
            <div class="detail-value">{{ banquetStatusLabel(banquetDetail.status) }}</div>
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

        <el-table :data="banquetFollowRecords" stripe empty-text="暂无宴席跟进记录">
          <el-table-column prop="followContent" label="跟进内容" min-width="260" />
          <el-table-column prop="createdAt" label="记录时间" min-width="180" />
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
const categoryFilters = ref({
  keyword: '',
  status: undefined as number | undefined
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
const dishFilters = ref({
  keyword: '',
  categoryId: undefined as number | undefined,
  status: undefined as number | undefined
})
const orderFilters = ref({
  keyword: '',
  orderStatus: '',
  orderScene: ''
})
const privateRoomFilters = ref({
  keyword: '',
  reservationStatus: ''
})
const banquetFilters = ref({
  keyword: '',
  status: ''
})

const realName = computed(() => localStorage.getItem('admin_real_name') || '管理员')
const title = computed(() => {
  if (currentTab.value === 'orders') return '订单总览'
  if (currentTab.value === 'categories') return '分类总览'
  if (currentTab.value === 'dishes') return '菜品总览'
  if (currentTab.value === 'privateRooms') return '包间预约总览'
  return '宴席预约总览'
})

const statCards = computed(() => {
  if (currentTab.value === 'orders') {
    return [
      { label: '筛选后订单', value: filteredOrders.value.length },
      { label: '分类总数', value: categories.value.length },
      { label: '菜品总数', value: dishes.value.length }
    ]
  }
  if (currentTab.value === 'categories') {
    return [
      { label: '订单总数', value: orders.value.length },
      { label: '筛选后分类', value: filteredCategories.value.length },
      { label: '菜品总数', value: dishes.value.length }
    ]
  }
  if (currentTab.value === 'dishes') {
    return [
      { label: '订单总数', value: orders.value.length },
      { label: '分类总数', value: categories.value.length },
      { label: '筛选后菜品', value: filteredDishes.value.length }
    ]
  }
  if (currentTab.value === 'privateRooms') {
    return [
      { label: '订单总数', value: orders.value.length },
      { label: '分类总数', value: categories.value.length },
      { label: '筛选后包间预约', value: filteredPrivateRooms.value.length }
    ]
  }
  return [
    { label: '订单总数', value: orders.value.length },
    { label: '分类总数', value: categories.value.length },
    { label: '筛选后宴席预约', value: filteredBanquets.value.length }
  ]
})

const workspaceHighlights = computed(() => [
  {
    key: 'waitAcceptOrders',
    label: '待接单订单',
    count: orders.value.filter((item) => item.orderStatus === 'WAIT_ACCEPT').length,
    desc: '尽快确认厨房是否开始制作'
  },
  {
    key: 'deliveringOrders',
    label: '配送中订单',
    count: orders.value.filter((item) => item.orderStatus === 'DELIVERING').length,
    desc: '重点关注客房送餐的到达情况'
  },
  {
    key: 'reservedPrivateRooms',
    label: '待到店包间',
    count: privateRooms.value.filter((item) => item.reservationStatus === 'RESERVED').length,
    desc: '查看今天和近期的包间预约安排'
  },
  {
    key: 'waitingBanquets',
    label: '待跟进宴席',
    count: banquets.value.filter((item) => item.status === 'WAIT_FOLLOW').length,
    desc: '优先联系高意向宴席客户'
  }
])

const workspaceTips = computed(() => [
  {
    title: '先看待接单订单',
    desc: `当前还有 ${orders.value.filter((item) => item.orderStatus === 'WAIT_ACCEPT').length} 笔订单待确认，适合先处理即时收入。`
  },
  {
    title: '关注客房送餐',
    desc: `当前客房送餐订单 ${orders.value.filter((item) => item.orderScene === 'ROOM_DELIVERY').length} 笔，建议留意配送进度。`
  },
  {
    title: '检查宴席跟进',
    desc: `待跟进宴席 ${banquets.value.filter((item) => item.status === 'WAIT_FOLLOW').length} 条，及时联系更容易提高转化。`
  }
])

const pendingTasks = computed(() => {
  const orderTasks = orders.value
    .filter((item) => item.orderStatus === 'WAIT_ACCEPT' || item.orderStatus === 'DELIVERING')
    .slice(0, 4)
    .map((item) => ({
      key: `order-${item.id}`,
      entityType: 'order',
      id: item.id,
      shortcutKey: item.orderStatus === 'WAIT_ACCEPT' ? 'waitAcceptOrders' : 'deliveringOrders',
      typeLabel: '订单',
      statusLabel: orderStatusLabel(item.orderStatus),
      tagType: orderStatusTagType(item.orderStatus),
      title: `${item.orderNo} / ${item.contactName || '未填写联系人'}`,
      desc: `${orderSceneLabel(item.orderScene)}，联系电话 ${item.contactPhone || '未填写'}`
    }))

  const privateRoomTasks = privateRooms.value
    .filter((item) => item.reservationStatus === 'RESERVED')
    .slice(0, 3)
    .map((item) => ({
      key: `private-room-${item.id}`,
      entityType: 'privateRoom',
      id: item.id,
      shortcutKey: 'reservedPrivateRooms',
      typeLabel: '包间',
      statusLabel: reservationStatusLabel(item.reservationStatus),
      tagType: reservationStatusTagType(item.reservationStatus),
      title: `${item.reservationNo} / ${item.privateRoomName || '未命名包间'}`,
      desc: `${formatDate(item.reserveDate)} ${item.timeslotName || item.timeslotCode}，联系人 ${item.contactName || '未填写'}`
    }))

  const banquetTasks = banquets.value
    .filter((item) => item.status === 'WAIT_FOLLOW')
    .slice(0, 3)
    .map((item) => ({
      key: `banquet-${item.id}`,
      entityType: 'banquet',
      id: item.id,
      shortcutKey: 'waitingBanquets',
      typeLabel: '宴席',
      statusLabel: banquetStatusLabel(item.status),
      tagType: banquetStatusTagType(item.status),
      title: `${item.reservationNo} / ${item.banquetType || '宴席预约'}`,
      desc: `${formatDate(item.reserveDate)}，联系人 ${item.contactName || '未填写'} / ${item.contactPhone || '未填写'}`
    }))

  return [...orderTasks, ...privateRoomTasks, ...banquetTasks]
})

const privateRoomDishRows = computed(() =>
  ((privateRoomDetail.value?.preorderDishes?.length
    ? privateRoomDetail.value.preorderDishes
    : ['暂未预点菜']) as string[]).map((name: string) => ({ name }))
)

const filteredCategories = computed(() => {
  const keyword = categoryFilters.value.keyword.trim().toLowerCase()
  const status = categoryFilters.value.status
  return categories.value.filter((item) => {
    const matchesKeyword = !keyword || String(item.name || '').toLowerCase().includes(keyword)
    const matchesStatus = status === undefined || Number(item.status) === status
    return matchesKeyword && matchesStatus
  })
})

const categoryStatusSummary = computed(() => {
  const items = [
    { value: 1, label: '已启用' },
    { value: 0, label: '已停用' }
  ]
  return items.map((item) => ({
    ...item,
    count: categories.value.filter((category) => Number(category.status) === item.value).length
  }))
})

const filteredOrders = computed(() => {
  const keyword = orderFilters.value.keyword.trim().toLowerCase()
  if (!keyword) {
    return orders.value
  }
  return orders.value.filter((item) =>
    [item.orderNo, item.contactName, item.contactPhone].some((field) =>
      String(field || '').toLowerCase().includes(keyword)
    )
  )
})

const orderStatusSummary = computed(() => {
  const items = [
    { value: 'WAIT_PAY', label: '待支付' },
    { value: 'WAIT_ACCEPT', label: '待接单' },
    { value: 'COOKING', label: '制作中' },
    { value: 'DELIVERING', label: '配送中' },
    { value: 'COMPLETED', label: '已完成' },
    { value: 'CANCELLED', label: '已取消' }
  ]
  return items.map((item) => ({
    ...item,
    count: orders.value.filter((order) => order.orderStatus === item.value).length
  }))
})

const filteredDishes = computed(() => {
  const keyword = dishFilters.value.keyword.trim().toLowerCase()
  const categoryId = dishFilters.value.categoryId
  const status = dishFilters.value.status
  return dishes.value.filter((item) => {
    const matchesKeyword = !keyword
      || [item.name, item.subtitle].some((field) => String(field || '').toLowerCase().includes(keyword))
    const matchesCategory = !categoryId || item.categoryId === categoryId
    const matchesStatus = status === undefined || Number(item.status) === status
    return matchesKeyword && matchesCategory && matchesStatus
  })
})

const dishStatusSummary = computed(() => {
  const items = [
    { value: 1, label: '已上架' },
    { value: 0, label: '已下架' }
  ]
  return items.map((item) => ({
    ...item,
    count: dishes.value.filter((dish) => Number(dish.status) === item.value).length
  }))
})

const filteredPrivateRooms = computed(() => {
  const keyword = privateRoomFilters.value.keyword.trim().toLowerCase()
  if (!keyword) {
    return privateRooms.value
  }
  return privateRooms.value.filter((item) =>
    [item.reservationNo, item.contactName, item.contactPhone, item.privateRoomName].some((field) =>
      String(field || '').toLowerCase().includes(keyword)
    )
  )
})

const privateRoomStatusSummary = computed(() => {
  const items = [
    { value: 'WAIT_PAY', label: '待支付' },
    { value: 'RESERVED', label: '已预约' },
    { value: 'ARRIVED', label: '已到店' },
    { value: 'COMPLETED', label: '已完成' },
    { value: 'CANCELLED', label: '已取消' }
  ]
  return items.map((item) => ({
    ...item,
    count: privateRooms.value.filter((reservation) => reservation.reservationStatus === item.value).length
  }))
})

const filteredBanquets = computed(() => {
  const keyword = banquetFilters.value.keyword.trim().toLowerCase()
  if (!keyword) {
    return banquets.value
  }
  return banquets.value.filter((item) =>
    [item.reservationNo, item.contactName, item.contactPhone, item.banquetType].some((field) =>
      String(field || '').toLowerCase().includes(keyword)
    )
  )
})

const banquetStatusSummary = computed(() => {
  const items = [
    { value: 'WAIT_FOLLOW', label: '待跟进' },
    { value: 'CONTACTED', label: '已联系' },
    { value: 'CONFIRMED', label: '已确认' },
    { value: 'CANCELLED', label: '已取消' }
  ]
  return items.map((item) => ({
    ...item,
    count: banquets.value.filter((reservation) => reservation.status === item.value).length
  }))
})

function orderSceneLabel(value: string) {
  const map: Record<string, string> = {
    NORMAL: '普通点餐',
    ROOM_DELIVERY: '客房送餐',
    PRIVATE_ROOM_PREORDER: '包间预点菜'
  }
  return map[value] || value
}

function orderSceneTagType(value: string) {
  const map: Record<string, string> = {
    NORMAL: '',
    ROOM_DELIVERY: 'primary',
    PRIVATE_ROOM_PREORDER: 'warning'
  }
  return map[value] || 'info'
}

function orderStatusLabel(value: string) {
  const map: Record<string, string> = {
    WAIT_PAY: '待支付',
    WAIT_ACCEPT: '待接单',
    COOKING: '制作中',
    DELIVERING: '配送中',
    COMPLETED: '已完成',
    CANCELLED: '已取消'
  }
  return map[value] || value
}

function orderStatusTagType(value: string) {
  const map: Record<string, string> = {
    WAIT_PAY: 'warning',
    WAIT_ACCEPT: '',
    COOKING: 'warning',
    DELIVERING: 'primary',
    COMPLETED: 'success',
    CANCELLED: 'info'
  }
  return map[value] || 'info'
}

function reservationStatusLabel(value: string) {
  const map: Record<string, string> = {
    WAIT_PAY: '待支付',
    RESERVED: '已预约',
    ARRIVED: '已到店',
    COMPLETED: '已完成',
    CANCELLED: '已取消'
  }
  return map[value] || value
}

function reservationStatusTagType(value: string) {
  const map: Record<string, string> = {
    WAIT_PAY: 'warning',
    RESERVED: 'success',
    ARRIVED: 'primary',
    COMPLETED: 'success',
    CANCELLED: 'info'
  }
  return map[value] || 'info'
}

function banquetStatusLabel(value: string) {
  const map: Record<string, string> = {
    WAIT_FOLLOW: '待跟进',
    CONTACTED: '已联系',
    CONFIRMED: '已确认',
    CANCELLED: '已取消'
  }
  return map[value] || value
}

function banquetStatusTagType(value: string) {
  const map: Record<string, string> = {
    WAIT_FOLLOW: 'warning',
    CONTACTED: 'primary',
    CONFIRMED: 'success',
    CANCELLED: 'info'
  }
  return map[value] || 'info'
}

function formatDate(value?: string) {
  if (!value) {
    return '-'
  }
  return value.replaceAll('-', '.')
}

function formatCurrency(value?: number | string) {
  const amount = Number(value ?? 0)
  if (Number.isNaN(amount)) {
    return '¥0.00'
  }
  return `¥${amount.toFixed(2)}`
}

async function loadAll() {
  try {
    const [orderData, categoryData, dishData, roomData, banquetData] = await Promise.all([
      fetchOrders({
        orderStatus: orderFilters.value.orderStatus || undefined,
        orderScene: orderFilters.value.orderScene || undefined
      }),
      fetchDishCategories(),
      fetchDishes(),
      fetchPrivateRoomReservations({
        reservationStatus: privateRoomFilters.value.reservationStatus || undefined
      }),
      fetchBanquetReservations({
        status: banquetFilters.value.status || undefined
      })
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
    keyword: '',
    orderStatus: '',
    orderScene: ''
  }
  loadAll()
}

function toggleOrderStatusFilter(value: string) {
  orderFilters.value.orderStatus = orderFilters.value.orderStatus === value ? '' : value
  loadAll()
}

function resetCategoryFilters() {
  categoryFilters.value = {
    keyword: '',
    status: undefined
  }
}

function toggleCategoryStatusFilter(value: number) {
  categoryFilters.value.status = categoryFilters.value.status === value ? undefined : value
}

function resetDishFilters() {
  dishFilters.value = {
    keyword: '',
    categoryId: undefined,
    status: undefined
  }
}

function toggleDishStatusFilter(value: number) {
  dishFilters.value.status = dishFilters.value.status === value ? undefined : value
}

function resetPrivateRoomFilters() {
  privateRoomFilters.value = {
    keyword: '',
    reservationStatus: ''
  }
  loadAll()
}

function togglePrivateRoomStatusFilter(value: string) {
  privateRoomFilters.value.reservationStatus = privateRoomFilters.value.reservationStatus === value ? '' : value
  loadAll()
}

function resetBanquetFilters() {
  banquetFilters.value = {
    keyword: '',
    status: ''
  }
  loadAll()
}

function toggleBanquetStatusFilter(value: string) {
  banquetFilters.value.status = banquetFilters.value.status === value ? '' : value
  loadAll()
}

function applyWorkspaceShortcut(key: string) {
  if (key === 'waitAcceptOrders') {
    currentTab.value = 'orders'
    orderFilters.value.orderStatus = 'WAIT_ACCEPT'
    orderFilters.value.orderScene = ''
    loadAll()
    return
  }
  if (key === 'deliveringOrders') {
    currentTab.value = 'orders'
    orderFilters.value.orderStatus = 'DELIVERING'
    orderFilters.value.orderScene = ''
    loadAll()
    return
  }
  if (key === 'reservedPrivateRooms') {
    currentTab.value = 'privateRooms'
    privateRoomFilters.value.reservationStatus = 'RESERVED'
    loadAll()
    return
  }
  currentTab.value = 'banquets'
  banquetFilters.value.status = 'WAIT_FOLLOW'
  loadAll()
}

function openPendingTask(task: any) {
  if (task.entityType === 'order') {
    showOrderDetail(task.id)
    return
  }
  if (task.entityType === 'privateRoom') {
    showPrivateRoomDetail(task.id)
    return
  }
  showBanquetDetail(task.id)
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

async function copyText(value?: string) {
  if (!value) {
    ElMessage.warning('暂无可复制的内容')
    return
  }
  try {
    await navigator.clipboard.writeText(value)
    ElMessage.success('已复制到剪贴板')
  } catch {
    ElMessage.error('复制失败，请手动复制')
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

.workspace-grid {
  display: grid;
  grid-template-columns: 1.5fr 1fr;
  gap: 18px;
  margin-bottom: 24px;
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

.workspace-panel {
  padding-bottom: 18px;
}

.workspace-caption {
  color: #a58a72;
  font-size: 13px;
}

.workspace-cards {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.workspace-card {
  border: 0;
  border-radius: 18px;
  padding: 18px;
  text-align: left;
  cursor: pointer;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.96), rgba(247, 238, 227, 0.96));
  color: #2b2118;
}

.workspace-card strong {
  display: block;
  margin: 12px 0 8px;
  font-size: 30px;
}

.workspace-label {
  color: #8b5e34;
  font-size: 13px;
}

.workspace-desc {
  color: #6f5a46;
  font-size: 13px;
  line-height: 1.5;
}

.workspace-tips {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.workspace-tip {
  padding: 16px 18px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.88);
}

.workspace-tip-title {
  display: block;
  color: #8b5e34;
  font-weight: 600;
}

.workspace-tip-desc {
  display: block;
  margin-top: 8px;
  color: #6f5a46;
  line-height: 1.6;
  font-size: 13px;
}

.task-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.task-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18px;
  padding: 18px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.9);
}

.task-copy {
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.task-head {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.task-type {
  color: #8b5e34;
  font-size: 13px;
  font-weight: 600;
}

.task-title {
  color: #2b2118;
  font-size: 16px;
}

.task-desc {
  color: #6f5a46;
  font-size: 13px;
  line-height: 1.6;
}

.task-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.task-empty {
  color: #8b5e34;
  font-size: 14px;
}

.filter-row {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.dialog-actions {
  display: flex;
  gap: 10px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.result-summary {
  margin: -4px 0 16px;
  color: #8b5e34;
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.result-summary-detail {
  color: #a58a72;
}

.status-summary {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin: -4px 0 18px;
}

.status-pill {
  border: 0;
  border-radius: 999px;
  padding: 8px 14px;
  background: rgba(244, 239, 231, 0.95);
  color: #8b5e34;
  cursor: pointer;
  font-size: 13px;
}

.status-pill.active {
  background: linear-gradient(135deg, #d8a35d, #9f6731);
  color: #fff7ec;
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

.detail-value--highlight {
  color: #b7792f;
}

.detail-inline-action {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.cell-inline-action {
  display: inline-flex;
  align-items: center;
  gap: 8px;
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
