const { request } = require('../../utils/request')

Page({
  data: {
    rooms: [],
    roomIndex: 0,
    timeslots: [],
    timeslotIndex: 0,
    reserveDate: '',
    guestCount: 6,
    contactName: '张先生',
    contactPhone: '13800000000',
    remark: '',
    submitting: false
  },

  onLoad() {
    const today = new Date()
    const reserveDate = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}-${String(today.getDate()).padStart(2, '0')}`
    this.setData({ reserveDate })
    this.loadInitialData(reserveDate)
  },

  async loadInitialData(reserveDate) {
    try {
      const [rooms, timeslots] = await Promise.all([
        request('/api/private-room/list'),
        request('/api/private-room/available-timeslots', 'GET', { reserveDate })
      ])
      this.setData({
        rooms,
        timeslots
      })
    } catch (error) {
      wx.showToast({
        title: '预约信息加载失败',
        icon: 'none'
      })
    }
  },

  onRoomChange(event) {
    this.setData({ roomIndex: Number(event.detail.value) })
  },

  onTimeslotChange(event) {
    this.setData({ timeslotIndex: Number(event.detail.value) })
  },

  onDateChange(event) {
    const reserveDate = event.detail.value
    this.setData({ reserveDate })
    this.loadInitialData(reserveDate)
  },

  onGuestInput(event) {
    this.setData({ guestCount: Number(event.detail.value) || 1 })
  },

  onNameInput(event) {
    this.setData({ contactName: event.detail.value })
  },

  onPhoneInput(event) {
    this.setData({ contactPhone: event.detail.value })
  },

  onRemarkInput(event) {
    this.setData({ remark: event.detail.value })
  },

  async submitReservation() {
    const room = this.data.rooms[this.data.roomIndex]
    const timeslotCode = this.data.timeslots[this.data.timeslotIndex]
    if (!room || !timeslotCode) {
      wx.showToast({
        title: '请选择包间和时段',
        icon: 'none'
      })
      return
    }

    try {
      this.setData({ submitting: true })
      const result = await request('/api/private-room/reserve', 'POST', {
        privateRoomId: room.id,
        reserveDate: this.data.reserveDate,
        timeslotCode,
        guestCount: this.data.guestCount,
        contactName: this.data.contactName,
        contactPhone: this.data.contactPhone,
        remark: this.data.remark,
        items: []
      })
      this.setData({ submitting: false })
      wx.showModal({
        title: '预约成功',
        content: `预约号：${result.reservationNo}\n当前状态：${result.reservationStatus}`,
        showCancel: false
      })
    } catch (error) {
      this.setData({ submitting: false })
      wx.showToast({
        title: '预约提交失败',
        icon: 'none'
      })
    }
  }
})
