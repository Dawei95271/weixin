Page({
  data: {
    orderNo: '',
    orderScene: '',
    orderSceneLabel: '',
    payableAmount: '',
    contactPhone: '',
    roomDeliveryNotice: ''
  },

  onLoad(options) {
    this.setData({
      orderNo: options.orderNo || '',
      orderScene: options.orderScene || '',
      orderSceneLabel: this.formatOrderScene(options.orderScene),
      payableAmount: options.payableAmount || '0.00'
    })
    this.loadConfig()
  },

  async loadConfig() {
    const { request } = require('../../utils/request')
    try {
      const config = await request('/api/config/public')
      this.setData({
        contactPhone: config.CONTACT_PHONE || '',
        roomDeliveryNotice: config.ROOM_DELIVERY_NOTICE || ''
      })
    } catch (error) {
      // keep page usable even if config loading fails
    }
  },

  backToMenu() {
    wx.switchTab({
      url: '/pages/menu/index'
    })
  },

  goRoomDining() {
    wx.navigateTo({
      url: '/pages/room/index'
    })
  },

  goServiceCenter() {
    wx.switchTab({
      url: '/pages/mine/index'
    })
  },

  goOrders() {
    wx.navigateTo({
      url: '/pages/order/list/index'
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
  }
})
