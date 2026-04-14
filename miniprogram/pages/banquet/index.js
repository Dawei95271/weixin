const { request } = require('../../utils/request')
const { getBusinessStatus } = require('../../utils/business')

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
    contactPhoneConfig: '',
    homeNotice: '',
    businessStatusTitle: '',
    businessStatusDetail: '',
    submitting: false
  },

  onLoad() {
    const today = new Date()
    const reserveDate = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}-${String(today.getDate()).padStart(2, '0')}`
    this.setData({ reserveDate })
    this.loadConfig()
  },

  async loadConfig() {
    try {
      const config = await request('/api/config/public')
      const businessStatus = getBusinessStatus(config)
      this.setData({
        contactPhoneConfig: config.CONTACT_PHONE || '',
        homeNotice: config.HOME_NOTICE || '',
        businessStatusTitle: businessStatus.title,
        businessStatusDetail: businessStatus.reserveHint
      })
    } catch (error) {
      // keep page usable even if config loading fails
    }
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
    if (!this.data.contactName.trim()) {
      wx.showToast({
        title: '请填写联系人',
        icon: 'none'
      })
      return false
    }
    if (!/^1\d{10}$/.test(this.data.contactPhone)) {
      wx.showToast({
        title: '请输入正确手机号',
        icon: 'none'
      })
      return false
    }
    if (Number(this.data.guestCount) < 1) {
      wx.showToast({
        title: '预计人数至少为 1',
        icon: 'none'
      })
      return false
    }
    return true
  },

  async submitReservation() {
    if (!this.validateForm()) {
      return
    }

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
        content: `预约号：${result.reservationNo}\n当前状态：${this.formatBanquetStatus(result.status)}`,
        confirmText: '查看预约',
        cancelText: '继续填写',
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
        title: '宴席预约提交失败',
        icon: 'none'
      })
    }
  },

  formatBanquetStatus(value) {
    const map = {
      WAIT_FOLLOW: '待跟进',
      CONTACTED: '已联系',
      CONFIRMED: '已确认',
      CANCELLED: '已取消'
    }
    return map[value] || value || '未知状态'
  }
})
