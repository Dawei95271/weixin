const { request } = require('../../../utils/request')

Page({
  data: {
    orders: [],
    loading: true
  },

  onShow() {
    this.loadOrders()
  },

  async loadOrders() {
    try {
      const orders = await request('/api/order/list')
      this.setData({
        orders,
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
  }
})
