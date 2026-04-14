const { request } = require('../../utils/request')
const { getBusinessStatus } = require('../../utils/business')

Page({
  data: {
    rooms: [],
    roomIndex: 0,
    timeslots: [],
    timeslotIndex: 0,
    selectedRoomName: '待选择',
    selectedTimeslotLabel: '待选择',
    selectedDepositAmount: 0,
    reserveDate: '',
    guestCount: 6,
    contactName: '张先生',
    contactPhone: '13800000000',
    remark: '',
    contactPhoneConfig: '',
    breakfastHours: '',
    lunchHours: '',
    dinnerHours: '',
    roomDeliveryNotice: '',
    businessStatusTitle: '',
    businessStatusDetail: '',
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
      const [rooms, timeslots, config] = await Promise.all([
        request('/api/private-room/list'),
        request('/api/private-room/available-timeslots', 'GET', { reserveDate }),
        request('/api/config/public')
      ])
      const mappedTimeslots = (timeslots || []).map((code) => ({
        code,
        label: this.formatTimeslot(code)
      }))
      this.setData({
        rooms: rooms || [],
        timeslots: mappedTimeslots,
        roomIndex: 0,
        timeslotIndex: 0,
        selectedRoomName: rooms && rooms.length ? rooms[0].name : '待选择',
        selectedTimeslotLabel: mappedTimeslots.length ? mappedTimeslots[0].label : '待选择',
        selectedDepositAmount: rooms && rooms.length ? rooms[0].depositAmount || 0 : 0,
        contactPhoneConfig: config.CONTACT_PHONE || '',
        breakfastHours: config.BREAKFAST_HOURS || '',
        lunchHours: config.LUNCH_HOURS || '',
        dinnerHours: config.DINNER_HOURS || '',
        roomDeliveryNotice: config.ROOM_DELIVERY_NOTICE || '',
        businessStatusTitle: getBusinessStatus(config).title,
        businessStatusDetail: getBusinessStatus(config).detail
      })
    } catch (error) {
      wx.showToast({
        title: '预约信息加载失败',
        icon: 'none'
      })
    }
  },

  onRoomChange(event) {
    const roomIndex = Number(event.detail.value)
    const room = this.data.rooms[roomIndex]
    this.setData({
      roomIndex,
      selectedRoomName: room ? room.name : '待选择',
      selectedDepositAmount: room ? room.depositAmount || 0 : 0
    })
  },

  onTimeslotChange(event) {
    const timeslotIndex = Number(event.detail.value)
    const timeslot = this.data.timeslots[timeslotIndex]
    this.setData({
      timeslotIndex,
      selectedTimeslotLabel: timeslot ? timeslot.label : '待选择'
    })
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

  callMerchant() {
    if (!this.data.contactPhoneConfig) {
      wx.showToast({
        title: '暂未配置联系电话',
        icon: 'none'
      })
      return
    }
    wx.makePhoneCall({
      phoneNumber: this.data.contactPhoneConfig
    })
  },

  validateForm() {
    const room = this.data.rooms[this.data.roomIndex]
    const timeslot = this.data.timeslots[this.data.timeslotIndex]
    const timeslotCode = timeslot && timeslot.code
    if (!room || !timeslotCode) {
      wx.showToast({
        title: '请选择包间和时段',
        icon: 'none'
      })
      return null
    }
    if (!this.data.contactName.trim()) {
      wx.showToast({
        title: '请填写联系人',
        icon: 'none'
      })
      return null
    }
    if (!/^1\d{10}$/.test(this.data.contactPhone)) {
      wx.showToast({
        title: '请输入正确手机号',
        icon: 'none'
      })
      return null
    }
    if (Number(this.data.guestCount) < 1) {
      wx.showToast({
        title: '人数至少为 1',
        icon: 'none'
      })
      return null
    }
    return { room, timeslotCode }
  },

  async submitReservation() {
    const validated = this.validateForm()
    if (!validated) {
      return
    }

    try {
      this.setData({ submitting: true })
      const result = await request('/api/private-room/reserve', 'POST', {
        privateRoomId: validated.room.id,
        reserveDate: this.data.reserveDate,
        timeslotCode: validated.timeslotCode,
        guestCount: this.data.guestCount,
        contactName: this.data.contactName,
        contactPhone: this.data.contactPhone,
        remark: this.data.remark,
        items: []
      })
      this.setData({ submitting: false })
      wx.showModal({
        title: '预约成功',
        content: `预约号：${result.reservationNo}\n当前状态：${this.formatReservationStatus(result.reservationStatus)}`,
        confirmText: '查看预约',
        cancelText: '继续浏览',
        success: ({ confirm }) => {
          if (confirm) {
            wx.navigateTo({
              url: '/pages/reservation/list/index'
            })
          }
        }
      })
    } catch (error) {
      this.setData({ submitting: false })
      wx.showToast({
        title: '预约提交失败',
        icon: 'none'
      })
    }
  },

  formatTimeslot(value) {
    const map = {
      BREAKFAST: '早餐',
      LUNCH: '中餐',
      DINNER: '晚餐'
    }
    return map[value] || value || '未选择'
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
  }
})
