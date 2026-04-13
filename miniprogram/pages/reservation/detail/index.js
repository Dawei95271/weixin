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
      const mappedDetail = type === 'banquet'
        ? {
            ...detail,
            statusLabel: this.formatBanquetStatus(detail.status)
          }
        : {
            ...detail,
            privateRoomDisplayName: detail.privateRoomName || `包间-${detail.privateRoomId}`,
            reservationStatusLabel: this.formatReservationStatus(detail.reservationStatus),
            timeslotLabel: this.formatTimeslot(detail.timeslotCode)
          }
      this.setData({
        type,
        detail: mappedDetail,
        loading: false
      })
    } catch (error) {
      this.setData({ loading: false })
      wx.showToast({
        title: '预约详情加载失败',
        icon: 'none'
      })
    }
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
