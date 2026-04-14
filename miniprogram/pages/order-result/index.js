Page({
  data: {
    orderNo: '',
    orderScene: '',
    payableAmount: '',
    contactPhone: '',
    roomDeliveryNotice: ''
  },

  onLoad(options) {
    this.setData({
      orderNo: options.orderNo || '',
      orderScene: options.orderScene || '',
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
  }
})
