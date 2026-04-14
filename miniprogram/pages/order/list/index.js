const { request } = require('../../../utils/request')

Page({
  data: {
    allOrders: [],
    orders: [],
    searchKeyword: '',
    activeStatus: 'ALL',
    activeScene: 'ALL',
    statusSummaries: [],
    sceneSummaries: [],
    contactPhone: '',
    roomDeliveryNotice: '',
    loading: true
  },

  onShow() {
    this.loadPageData()
  },

  async loadPageData() {
    try {
      const [orders, config] = await Promise.all([
        request('/api/order/list'),
        request('/api/config/public')
      ])
      const formattedOrders = (orders || []).map((item) => ({
        ...item,
        orderSceneLabel: this.formatOrderScene(item.orderScene),
        orderStatusLabel: this.formatOrderStatus(item.orderStatus)
      }))
      this.setData({
        allOrders: formattedOrders,
        contactPhone: config.CONTACT_PHONE || '',
        roomDeliveryNotice: config.ROOM_DELIVERY_NOTICE || '',
        loading: false
      })
      this.applyFilters()
    } catch (error) {
      this.setData({ loading: false })
      wx.showToast({
        title: '订单加载失败',
        icon: 'none'
      })
    }
  },

  goDetail(event) {
    const { id } = event.currentTarget.dataset
    wx.navigateTo({
      url: `/pages/order/detail/index?id=${id}`
    })
  },

  onSearchInput(event) {
    this.setData({
      searchKeyword: event.detail.value
    })
    this.applyFilters()
  },

  selectStatus(event) {
    this.setData({
      activeStatus: event.currentTarget.dataset.status
    })
    this.applyFilters()
  },

  selectScene(event) {
    this.setData({
      activeScene: event.currentTarget.dataset.scene
    })
    this.applyFilters()
  },

  resetFilters() {
    this.setData({
      searchKeyword: '',
      activeStatus: 'ALL',
      activeScene: 'ALL'
    })
    this.applyFilters()
  },

  copyOrderNo(event) {
    const { orderNo } = event.currentTarget.dataset
    wx.setClipboardData({
      data: orderNo
    })
  },

  copyContactPhone(event) {
    const { phone } = event.currentTarget.dataset
    if (!phone) {
      wx.showToast({
        title: '当前订单未填写电话',
        icon: 'none'
      })
      return
    }
    wx.setClipboardData({
      data: phone
    })
  },

  goMenu() {
    wx.switchTab({
      url: '/pages/menu/index'
    })
  },

  callMerchant() {
    if (!this.data.contactPhone) {
      wx.showToast({
        title: '暂未配置联系电话',
        icon: 'none'
      })
      return
    }
    wx.makePhoneCall({
      phoneNumber: this.data.contactPhone
    })
  },

  applyFilters() {
    const keyword = (this.data.searchKeyword || '').trim().toLowerCase()
    const statusSummaries = this.buildStatusSummaries(this.data.allOrders)
    const sceneSummaries = this.buildSceneSummaries(this.data.allOrders)
    const filteredOrders = this.data.allOrders.filter((item) => {
      const matchesKeyword = !keyword || [
        item.orderNo,
        item.contactName,
        item.contactPhone,
        item.roomNo,
        item.orderSceneLabel,
        item.orderStatusLabel
      ].some((field) => String(field || '').toLowerCase().includes(keyword))
      const matchesStatus = this.data.activeStatus === 'ALL' || item.orderStatus === this.data.activeStatus
      const matchesScene = this.data.activeScene === 'ALL' || item.orderScene === this.data.activeScene
      return matchesKeyword && matchesStatus && matchesScene
    })
    this.setData({
      orders: filteredOrders,
      statusSummaries,
      sceneSummaries
    })
  },

  buildStatusSummaries(orders) {
    const options = [
      { key: 'ALL', label: '全部状态' },
      { key: 'WAIT_PAY', label: '待支付' },
      { key: 'WAIT_ACCEPT', label: '待接单' },
      { key: 'COOKING', label: '制作中' },
      { key: 'DELIVERING', label: '配送中' },
      { key: 'COMPLETED', label: '已完成' },
      { key: 'CANCELLED', label: '已取消' }
    ]
    return options.map((item) => ({
      ...item,
      count: item.key === 'ALL'
        ? orders.length
        : orders.filter((order) => order.orderStatus === item.key).length
    }))
  },

  buildSceneSummaries(orders) {
    const options = [
      { key: 'ALL', label: '全部场景' },
      { key: 'NORMAL', label: '普通点餐' },
      { key: 'ROOM_DELIVERY', label: '客房送餐' },
      { key: 'PRIVATE_ROOM_PREORDER', label: '包间预点菜' }
    ]
    return options.map((item) => ({
      ...item,
      count: item.key === 'ALL'
        ? orders.length
        : orders.filter((order) => order.orderScene === item.key).length
    }))
  },

  formatOrderScene(value) {
    const map = {
      NORMAL: '普通点餐',
      ROOM_DELIVERY: '客房送餐',
      PRIVATE_ROOM_PREORDER: '包间预点菜'
    }
    return map[value] || value || '未知场景'
  },

  formatOrderStatus(value) {
    const map = {
      WAIT_PAY: '待支付',
      WAIT_ACCEPT: '待接单',
      COOKING: '制作中',
      DELIVERING: '配送中',
      COMPLETED: '已完成',
      CANCELLED: '已取消'
    }
    return map[value] || value || '未知状态'
  }
})
