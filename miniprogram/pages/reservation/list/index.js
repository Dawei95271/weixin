const { request } = require('../../../utils/request')

Page({
  data: {
    allPrivateRoomReservations: [],
    allBanquetReservations: [],
    privateRoomReservations: [],
    banquetReservations: [],
    searchKeyword: '',
    activeTab: 'all',
    activeStatus: 'ALL',
    tabSummaries: [],
    statusSummaries: [],
    contactPhone: '',
    homeNotice: '',
    loading: true
  },

  onShow() {
    this.loadPageData()
  },

  async loadPageData() {
    try {
      const [privateRoomReservations, banquetReservations, config] = await Promise.all([
        request('/api/private-room/reservation/list'),
        request('/api/banquet/reservation/list'),
        request('/api/config/public')
      ])
      const formattedPrivateRoomReservations = (privateRoomReservations || []).map((item) => ({
        ...item,
        reservationStatusLabel: this.formatReservationStatus(item.reservationStatus),
        timeslotLabel: item.timeslotName || this.formatTimeslot(item.timeslotCode)
      }))
      const formattedBanquetReservations = (banquetReservations || []).map((item) => ({
        ...item,
        statusLabel: this.formatBanquetStatus(item.status)
      }))
      this.setData({
        allPrivateRoomReservations: formattedPrivateRoomReservations,
        allBanquetReservations: formattedBanquetReservations,
        contactPhone: config.CONTACT_PHONE || '',
        homeNotice: config.HOME_NOTICE || '',
        loading: false
      })
      this.applyFilters()
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

  onSearchInput(event) {
    this.setData({
      searchKeyword: event.detail.value
    })
    this.applyFilters()
  },

  selectTab(event) {
    this.setData({
      activeTab: event.currentTarget.dataset.tab,
      activeStatus: 'ALL'
    })
    this.applyFilters()
  },

  selectStatus(event) {
    this.setData({
      activeStatus: event.currentTarget.dataset.status
    })
    this.applyFilters()
  },

  resetFilters() {
    this.setData({
      searchKeyword: '',
      activeTab: 'all',
      activeStatus: 'ALL'
    })
    this.applyFilters()
  },

  copyReservationNo(event) {
    wx.setClipboardData({
      data: event.currentTarget.dataset.no
    })
  },

  copyContactPhone(event) {
    const phone = event.currentTarget.dataset.phone
    if (!phone) {
      wx.showToast({
        title: '当前预约未填写电话',
        icon: 'none'
      })
      return
    }
    wx.setClipboardData({
      data: phone
    })
  },

  callMerchant() {
    if (!this.data.contactPhone) {
      wx.showToast({
        title: '暂未配置联系电话',
        icon: 'none'
      })
      return
    }
    wx.makePhoneCall({
      phoneNumber: this.data.contactPhone
    })
  },

  applyFilters() {
    const keyword = (this.data.searchKeyword || '').trim().toLowerCase()
    const privateRoomReservations = this.data.allPrivateRoomReservations.filter((item) => {
      const matchesKeyword = !keyword || [
        item.reservationNo,
        item.privateRoomName,
        item.contactName,
        item.contactPhone,
        item.timeslotLabel
      ].some((field) => String(field || '').toLowerCase().includes(keyword))
      const matchesTab = this.data.activeTab === 'all' || this.data.activeTab === 'private-room'
      const matchesStatus = this.data.activeTab === 'all'
        || this.data.activeStatus === 'ALL'
        || item.reservationStatus === this.data.activeStatus
      return matchesKeyword && matchesTab && matchesStatus
    })
    const banquetReservations = this.data.allBanquetReservations.filter((item) => {
      const matchesKeyword = !keyword || [
        item.reservationNo,
        item.banquetType,
        item.contactName,
        item.contactPhone,
        item.statusLabel
      ].some((field) => String(field || '').toLowerCase().includes(keyword))
      const matchesTab = this.data.activeTab === 'all' || this.data.activeTab === 'banquet'
      const matchesStatus = this.data.activeTab === 'all'
        || this.data.activeStatus === 'ALL'
        || item.status === this.data.activeStatus
      return matchesKeyword && matchesTab && matchesStatus
    })
    this.setData({
      privateRoomReservations,
      banquetReservations,
      tabSummaries: this.buildTabSummaries(),
      statusSummaries: this.buildStatusSummaries()
    })
  },

  buildTabSummaries() {
    return [
      {
        key: 'all',
        label: '全部预约',
        count: this.data.allPrivateRoomReservations.length + this.data.allBanquetReservations.length
      },
      {
        key: 'private-room',
        label: '包间预约',
        count: this.data.allPrivateRoomReservations.length
      },
      {
        key: 'banquet',
        label: '宴席预约',
        count: this.data.allBanquetReservations.length
      }
    ]
  },

  buildStatusSummaries() {
    if (this.data.activeTab === 'all') {
      return []
    }
    if (this.data.activeTab === 'banquet') {
      const options = [
        { key: 'ALL', label: '全部状态' },
        { key: 'WAIT_FOLLOW', label: '待跟进' },
        { key: 'CONTACTED', label: '已联系' },
        { key: 'CONFIRMED', label: '已确认' },
        { key: 'CANCELLED', label: '已取消' }
      ]
      return options.map((item) => ({
        ...item,
        count: item.key === 'ALL'
          ? this.data.allBanquetReservations.length
          : this.data.allBanquetReservations.filter((reservation) => reservation.status === item.key).length
      }))
    }
    const options = [
      { key: 'ALL', label: '全部状态' },
      { key: 'WAIT_PAY', label: '待支付' },
      { key: 'RESERVED', label: '已预约' },
      { key: 'ARRIVED', label: '已到店' },
      { key: 'COMPLETED', label: '已完成' },
      { key: 'CANCELLED', label: '已取消' }
    ]
    return options.map((item) => ({
      ...item,
      count: item.key === 'ALL'
        ? this.data.allPrivateRoomReservations.length
        : this.data.allPrivateRoomReservations.filter((reservation) => reservation.reservationStatus === item.key).length
    }))
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
