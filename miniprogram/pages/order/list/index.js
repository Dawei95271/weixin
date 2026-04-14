const { request } = require('../../../utils/request')

Page({
  data: {
    orders: [],
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
      this.setData({
        orders: (orders || []).map((item) => ({
          ...item,
          orderSceneLabel: this.formatOrderScene(item.orderScene),
          orderStatusLabel: this.formatOrderStatus(item.orderStatus)
        })),
        contactPhone: config.CONTACT_PHONE || '',
        roomDeliveryNotice: config.ROOM_DELIVERY_NOTICE || '',
        loading: false
      })
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
