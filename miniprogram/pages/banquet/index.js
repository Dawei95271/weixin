const { request } = require('../../utils/request')

Page({
  data: {
    banquetTypes: ['婚宴', '寿宴', '商务宴', '满月宴', '其他'],
    banquetTypeIndex: 0,
    reserveDate: '',
    guestCount: 100,
    budgetAmount: 10000,
    contactName: '张先生',
    contactPhone: '13800000000',
    requirementDesc: '',
    submitting: false
  },

  onLoad() {
    const today = new Date()
    const reserveDate = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}-${String(today.getDate()).padStart(2, '0')}`
    this.setData({ reserveDate })
  },

  onBanquetTypeChange(event) {
    this.setData({
      banquetTypeIndex: Number(event.detail.value)
    })
  },

  onDateChange(event) {
    this.setData({
      reserveDate: event.detail.value
    })
  },

  onGuestCountInput(event) {
    this.setData({
      guestCount: Number(event.detail.value) || 1
    })
  },

  onBudgetInput(event) {
    this.setData({
      budgetAmount: Number(event.detail.value) || 0
    })
  },

  onNameInput(event) {
    this.setData({
      contactName: event.detail.value
    })
  },

  onPhoneInput(event) {
    this.setData({
      contactPhone: event.detail.value
    })
  },

  onRequirementInput(event) {
    this.setData({
      requirementDesc: event.detail.value
    })
  },

  async submitReservation() {
    try {
      this.setData({ submitting: true })
      const banquetType = this.data.banquetTypes[this.data.banquetTypeIndex]
      const result = await request('/api/banquet/reservation/create', 'POST', {
        banquetType,
        reserveDate: this.data.reserveDate,
        guestCount: this.data.guestCount,
        budgetAmount: this.data.budgetAmount,
        contactName: this.data.contactName,
        contactPhone: this.data.contactPhone,
        requirementDesc: this.data.requirementDesc
      })
      this.setData({ submitting: false })
      wx.showModal({
        title: '预约提交成功',
        content: `预约号：${result.reservationNo}\n当前状态：${result.status}`,
        showCancel: false
      })
    } catch (error) {
      this.setData({ submitting: false })
      wx.showToast({
        title: '宴席预约提交失败',
        icon: 'none'
      })
    }
  }
})
