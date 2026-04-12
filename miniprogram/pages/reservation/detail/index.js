const { request } = require('../../../utils/request')

Page({
  data: {
    type: '',
    detail: null,
    loading: true
  },

  onLoad(options) {
    this.loadDetail(options.type, options.id)
  },

  async loadDetail(type, id) {
    try {
      const url = type === 'banquet'
        ? `/api/banquet/reservation/detail/${id}`
        : `/api/private-room/reservation/detail/${id}`
      const detail = await request(url)
      this.setData({
        type,
        detail,
        loading: false
      })
    } catch (error) {
      this.setData({ loading: false })
      wx.showToast({
        title: '预约详情加载失败',
        icon: 'none'
      })
    }
  }
})
