const { request } = require('../../utils/request')
const { getBusinessStatus } = require('../../utils/business')
const { getCurrentRoom, clearCurrentRoom, getRoomDeliverySummary } = require('../../utils/room-delivery')

Page({
  data: {
    projectName: '',
    businessScopes: [],
    homeBanners: [],
    serviceEntries: [],
    topicCards: [],
    featuredDishes: [],
    contactPhone: '',
    homeNotice: '',
    businessHours: [],
    businessStatusTitle: '',
    businessStatusDetail: '',
    businessOpen: false,
    roomDeliveryActive: false,
    roomDeliveryTitle: '',
    roomDeliveryDetail: '',
    currentRoom: null,
    deliveryFee: '',
    minOrderAmount: '',
    roomDeliveryNotice: '',
    loading: true
  },

  onLoad() {
    this.loadHome()
  },

  onShow() {
    this.refreshRoomDeliveryState()
  },

  async loadHome() {
    try {
      const data = await request('/api/home/index')
      const dishes = await request('/api/dish/list')
      const businessStatus = getBusinessStatus({
        BREAKFAST_HOURS: data.breakfastHours,
        LUNCH_HOURS: data.lunchHours,
        DINNER_HOURS: data.dinnerHours
      })
      this.setData({
        projectName: data.projectName,
        businessScopes: data.businessScopes,
        homeBanners: (data.homeBanners || []).map((item, index) => ({
          id: `${item.title || 'banner'}-${index}`,
          title: item.title || '酒店二楼餐饮服务',
          subtitle: item.subtitle || '欢迎进入首页运营位',
          linkType: item.linkType || 'NONE',
          linkValue: item.linkValue || '',
          tone: item.tone || 'amber'
        })),
        serviceEntries: (data.serviceEntries || []).map((item, index) => ({
          id: `${item.title || 'entry'}-${index}`,
          title: item.title || '服务入口',
          subtitle: item.subtitle || '点击进入对应服务',
          linkType: item.linkType || 'NONE',
          linkValue: item.linkValue || '',
          tone: item.tone || 'amber'
        })),
        topicCards: (data.topicCards || []).map((item, index) => ({
          id: `${item.title || 'topic'}-${index}`,
          eyebrow: item.eyebrow || 'SPECIAL',
          title: item.title || '专题活动',
          subtitle: item.subtitle || '点击查看当前活动专题',
          linkType: item.linkType || 'NONE',
          linkValue: item.linkValue || '',
          tone: item.tone || 'amber'
        })),
        contactPhone: data.contactPhone || '',
        homeNotice: data.homeNotice || '',
        deliveryFee: data.deliveryFee || '',
        minOrderAmount: data.minOrderAmount || '',
        roomDeliveryNotice: data.roomDeliveryNotice || '',
        businessHours: [
          { label: '早餐', value: data.breakfastHours || '待配置' },
          { label: '中餐', value: data.lunchHours || '待配置' },
          { label: '晚餐', value: data.dinnerHours || '待配置' }
        ],
        businessStatusTitle: businessStatus.title,
        businessStatusDetail: businessStatus.detail,
        businessOpen: businessStatus.open,
        featuredDishes: dishes.slice(0, 3),
        loading: false
      })
    } catch (error) {
      wx.showToast({
        title: '首页加载失败',
        icon: 'none'
      })
      this.setData({ loading: false })
    }
  },

  goMenu() {
    wx.switchTab({
      url: '/pages/menu/index'
    })
  },

  goRoomDining() {
    wx.navigateTo({
      url: '/pages/room/index'
    })
  },

  goCart() {
    wx.switchTab({
      url: '/pages/cart/index'
    })
  },

  onBannerTap(event) {
    const banner = event.currentTarget.dataset.banner
    if (!banner) {
      return
    }
    this.handleLinkAction(banner)
  },

  onServiceEntryTap(event) {
    const entry = event.currentTarget.dataset.entry
    if (!entry) {
      return
    }
    this.handleLinkAction(entry)
  },

  onTopicCardTap(event) {
    const topic = event.currentTarget.dataset.topic
    if (!topic) {
      return
    }
    this.handleLinkAction(topic)
  },

  handleLinkAction(target) {
    const linkType = target.linkType || 'NONE'
    const linkValue = target.linkValue || ''
    if (linkType === 'ROOM') {
      this.goRoomDining()
      return
    }
    if (linkType === 'MENU') {
      this.goMenu()
      return
    }
    if (linkType === 'CART') {
      this.goCart()
      return
    }
    if (linkType === 'PRIVATE_ROOM') {
      wx.navigateTo({ url: '/pages/private-room/index' })
      return
    }
    if (linkType === 'BANQUET') {
      wx.navigateTo({ url: '/pages/banquet/index' })
      return
    }
    if (linkType === 'RESERVATION') {
      wx.navigateTo({ url: '/pages/reservation/list/index' })
      return
    }
    if (linkType === 'MINE') {
      wx.switchTab({ url: '/pages/mine/index' })
      return
    }
    if (linkType === 'PHONE') {
      this.callMerchant()
      return
    }
    if (linkType === 'PATH' && linkValue) {
      const isTab = ['/pages/home/index', '/pages/menu/index', '/pages/cart/index', '/pages/mine/index'].includes(linkValue)
      if (isTab) {
        wx.switchTab({ url: linkValue })
        return
      }
      wx.navigateTo({ url: linkValue })
    }
  },

  clearRoomDelivery() {
    clearCurrentRoom()
    this.refreshRoomDeliveryState()
    wx.showToast({
      title: '已切回普通点餐',
      icon: 'success'
    })
  },

  callMerchant() {
    const phoneNumber = this.data.contactPhone
    if (!phoneNumber) {
      wx.showToast({
        title: '暂未配置联系电话',
        icon: 'none'
      })
      return
    }
    wx.makePhoneCall({
      phoneNumber
    })
  },

  addFeaturedDish(event) {
    const { addToCart } = require('../../utils/cart')
    const dish = event.currentTarget.dataset.dish
    addToCart(dish)
    wx.showToast({
      title: '已加入购物车',
      icon: 'success'
    })
  },

  openDishDetail(event) {
    const { id } = event.currentTarget.dataset
    wx.navigateTo({
      url: `/pages/dish/detail/index?id=${id}`
    })
  },

  refreshRoomDeliveryState() {
    const currentRoom = getCurrentRoom()
    const summary = getRoomDeliverySummary(currentRoom)
    this.setData({
      currentRoom,
      roomDeliveryActive: summary.active,
      roomDeliveryTitle: summary.title,
      roomDeliveryDetail: summary.detail
    })
  }
})
