const { request } = require('../../../utils/request')

Page({
  data: {
    order: null,
    loading: true
  },

  onLoad(options) {
    this.loadDetail(options.id)
  },

  async loadDetail(id) {
    try {
      const order = await request(`/api/order/detail/${id}`)
      this.setData({
        order,
        loading: false
      })
    } catch (error) {
      this.setData({ loading: false })
      wx.showToast({
        title: '订单详情加载失败',
        icon: 'none'
      })
    }
  }
})
