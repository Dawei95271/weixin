const { request } = require('../../../utils/request')

Page({
  data: {
    privateRoomReservations: [],
    banquetReservations: [],
    loading: true
  },

  onShow() {
    this.loadReservations()
  },

  async loadReservations() {
    try {
      const [privateRoomReservations, banquetReservations] = await Promise.all([
        request('/api/private-room/reservation/list'),
        request('/api/banquet/reservation/list')
      ])
      this.setData({
        privateRoomReservations,
        banquetReservations,
        loading: false
      })
    } catch (error) {
      this.setData({ loading: false })
      wx.showToast({
        title: '预约记录加载失败',
        icon: 'none'
      })
    }
  },

  goPrivateRoom() {
    wx.navigateTo({
      url: '/pages/private-room/index'
    })
  },

  goBanquet() {
    wx.navigateTo({
      url: '/pages/banquet/index'
    })
  }
})
