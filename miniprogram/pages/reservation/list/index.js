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
        privateRoomReservations: (privateRoomReservations || []).map((item) => ({
          ...item,
          reservationStatusLabel: this.formatReservationStatus(item.reservationStatus),
          timeslotLabel: this.formatTimeslot(item.timeslotCode)
        })),
        banquetReservations: (banquetReservations || []).map((item) => ({
          ...item,
          statusLabel: this.formatBanquetStatus(item.status)
        })),
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
  },

  goDetail(event) {
    const { id, type } = event.currentTarget.dataset
    wx.navigateTo({
      url: `/pages/reservation/detail/index?id=${id}&type=${type}`
    })
  },

  formatReservationStatus(value) {
    const map = {
      WAIT_PAY: '待支付',
      RESERVED: '已预约',
      ARRIVED: '已到店',
      COMPLETED: '已完成',
      CANCELLED: '已取消'
    }
    return map[value] || value || '未知状态'
  },

  formatBanquetStatus(value) {
    const map = {
      WAIT_FOLLOW: '待跟进',
      CONTACTED: '已联系',
      CONFIRMED: '已确认',
      CANCELLED: '已取消'
    }
    return map[value] || value || '未知状态'
  },

  formatTimeslot(value) {
    const map = {
      BREAKFAST: '早餐',
      LUNCH: '中餐',
      DINNER: '晚餐'
    }
    return map[value] || value || '未设置'
  }
})
