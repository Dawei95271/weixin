const { request } = require('../../utils/request')
const { getBusinessStatus } = require('../../utils/business')
const { getCurrentRoom, clearCurrentRoom, getRoomDeliverySummary } = require('../../utils/room-delivery')

Page({
  data: {
    quickActions: [
      {
        title: '我的订单',
        desc: '查看点餐订单、客房送餐和订单详情',
        badge: 'ORDER',
        url: '/pages/order/list/index',
        count: 0
      },
      {
        title: '我的预约',
        desc: '集中查看包间预约和宴席预约记录',
        badge: 'BOOKING',
        url: '/pages/reservation/list/index',
        count: 0
      },
      {
        title: '客房点餐',
        desc: '先识别房间，再进入客房送餐场景下单',
        badge: 'ROOM',
        url: '/pages/room/index',
        count: 0
      },
      {
        title: '包间预约',
        desc: '选择日期、时段和包间，提前锁定用餐安排',
        badge: 'VIP',
        url: '/pages/private-room/index'
      },
      {
        title: '宴席预约',
        desc: '婚宴、寿宴、商务宴线上留资，后续人工跟进',
        badge: 'BANQUET',
        url: '/pages/banquet/index'
      }
    ],
    tips: [
      '客房住客建议先扫码识别房间，再提交送餐订单。',
      '包间预约支持先预约时段，再补充预点菜信息。',
      '宴席预约提交后，后台会尽快联系你确认档期和需求。'
    ],
    contactPhone: '',
    homeNotice: '',
    breakfastHours: '',
    lunchHours: '',
    dinnerHours: '',
    businessStatusTitle: '',
    businessStatusDetail: '',
    currentRoomTitle: '',
    currentRoomDetail: '',
    roomDeliveryActive: false,
    dashboardCards: [],
    pendingOrderCount: 0,
    reservationCount: 0
  },

  onShow() {
    this.loadDashboard()
  },

  async loadDashboard() {
    try {
      const [config, orders, privateRoomReservations, banquetReservations] = await Promise.all([
        request('/api/config/public'),
        request('/api/order/list'),
        request('/api/private-room/reservation/list'),
        request('/api/banquet/reservation/list')
      ])
      const businessStatus = getBusinessStatus(config)
      const currentRoomSummary = getRoomDeliverySummary(getCurrentRoom())
      const orderList = orders || []
      const privateRoomList = privateRoomReservations || []
      const banquetList = banquetReservations || []
      const pendingOrderCount = orderList.filter((item) => ['WAIT_PAY', 'WAIT_ACCEPT', 'COOKING', 'DELIVERING'].includes(item.orderStatus)).length
      const privateRoomCount = privateRoomList.filter((item) => ['WAIT_PAY', 'RESERVED', 'ARRIVED'].includes(item.reservationStatus)).length
      const banquetCount = banquetList.filter((item) => ['WAIT_FOLLOW', 'CONTACTED', 'CONFIRMED'].includes(item.status)).length
      const reservationCount = privateRoomCount + banquetCount
      this.setData({
        contactPhone: config.CONTACT_PHONE || '',
        homeNotice: config.HOME_NOTICE || '',
        breakfastHours: config.BREAKFAST_HOURS || '',
        lunchHours: config.LUNCH_HOURS || '',
        dinnerHours: config.DINNER_HOURS || '',
        businessStatusTitle: businessStatus.title,
        businessStatusDetail: businessStatus.detail,
        currentRoomTitle: currentRoomSummary.title,
        currentRoomDetail: currentRoomSummary.detail,
        roomDeliveryActive: currentRoomSummary.active,
        pendingOrderCount,
        reservationCount,
        dashboardCards: [
          {
            title: '待处理订单',
            value: pendingOrderCount,
            desc: pendingOrderCount ? '建议优先查看待支付、待接单和配送中的订单。' : '当前没有待处理订单，可以继续浏览菜单或查看历史记录。'
          },
          {
            title: '进行中预约',
            value: reservationCount,
            desc: reservationCount ? '包间和宴席预约都可以在这里集中查看状态变化。' : '当前没有进行中的预约，可直接发起包间或宴席预约。'
          },
          {
            title: '客房送餐状态',
            value: currentRoomSummary.active ? '已启用' : '未启用',
            desc: currentRoomSummary.detail
          }
        ],
        quickActions: this.data.quickActions.map((item) => {
          if (item.badge === 'ORDER') {
            return { ...item, count: orderList.length }
          }
          if (item.badge === 'BOOKING') {
            return { ...item, count: privateRoomList.length + banquetList.length }
          }
          if (item.badge === 'ROOM') {
            return { ...item, count: currentRoomSummary.active ? 1 : 0 }
          }
          return item
        })
      })
    } catch (error) {
      // keep page usable even if config loading fails
    }
  },

  goFeature(event) {
    const { url } = event.currentTarget.dataset
    if (!url) {
      wx.showToast({
        title: '该功能下一步接入',
        icon: 'none'
      })
      return
    }
    wx.navigateTo({
      url
    })
  },

  goMenu() {
    wx.switchTab({
      url: '/pages/menu/index'
    })
  },

  goOrders() {
    wx.navigateTo({
      url: '/pages/order/list/index'
    })
  },

  goReservations() {
    wx.navigateTo({
      url: '/pages/reservation/list/index'
    })
  },

  goRoomDining() {
    wx.navigateTo({
      url: '/pages/room/index'
    })
  },

  clearRoomDelivery() {
    clearCurrentRoom()
    const currentRoomSummary = getRoomDeliverySummary(null)
    this.setData({
      currentRoomTitle: currentRoomSummary.title,
      currentRoomDetail: currentRoomSummary.detail,
      roomDeliveryActive: false,
      dashboardCards: this.data.dashboardCards.map((item) => (
        item.title === '客房送餐状态'
          ? { ...item, value: '未启用', desc: currentRoomSummary.detail }
          : item
      )),
      quickActions: this.data.quickActions.map((item) => (
        item.badge === 'ROOM' ? { ...item, count: 0 } : item
      ))
    })
    wx.showToast({
      title: '已切回普通点餐',
      icon: 'success'
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
  }
})
