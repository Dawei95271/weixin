const { request } = require('../../utils/request')
const { addToCart } = require('../../utils/cart')
const { getBusinessStatus } = require('../../utils/business')
const { getCurrentRoom, clearCurrentRoom, getRoomDeliverySummary } = require('../../utils/room-delivery')

Page({
  data: {
    categories: [],
    dishes: [],
    currentCategoryId: null,
    deliveryFee: '',
    minOrderAmount: '',
    breakfastHours: '',
    lunchHours: '',
    dinnerHours: '',
    roomDeliveryNotice: '',
    businessStatusTitle: '',
    businessStatusDetail: '',
    businessOpen: false,
    roomDeliveryActive: false,
    roomDeliveryTitle: '',
    roomDeliveryDetail: '',
    loading: true
  },

  onLoad() {
    this.loadData()
  },

  onShow() {
    this.refreshRoomDeliveryState()
  },

  async loadData() {
    try {
      const categories = await request('/api/dish/category/list')
      const config = await request('/api/config/public')
      const currentCategoryId = categories.length ? categories[0].id : null
      const dishes = await request('/api/dish/list', 'GET', currentCategoryId ? { categoryId: currentCategoryId } : {})
      const businessStatus = getBusinessStatus(config)
      this.setData({
        categories,
        currentCategoryId,
        deliveryFee: config.DELIVERY_FEE || '',
        minOrderAmount: config.MIN_ORDER_AMOUNT || '',
        breakfastHours: config.BREAKFAST_HOURS || '',
        lunchHours: config.LUNCH_HOURS || '',
        dinnerHours: config.DINNER_HOURS || '',
        roomDeliveryNotice: config.ROOM_DELIVERY_NOTICE || '',
        businessStatusTitle: businessStatus.title,
        businessStatusDetail: businessStatus.detail,
        businessOpen: businessStatus.open,
        dishes,
        loading: false
      })
    } catch (error) {
      wx.showToast({
        title: '菜单加载失败',
        icon: 'none'
      })
      this.setData({ loading: false })
    }
  },

  async switchCategory(event) {
    const categoryId = event.currentTarget.dataset.id
    try {
      const dishes = await request('/api/dish/list', 'GET', { categoryId })
      this.setData({
        currentCategoryId: categoryId,
        dishes
      })
    } catch (error) {
      wx.showToast({
        title: '分类切换失败',
        icon: 'none'
      })
    }
  },

  addDish(event) {
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

  clearRoomDelivery() {
    clearCurrentRoom()
    this.refreshRoomDeliveryState()
    wx.showToast({
      title: '已切回普通点餐',
      icon: 'success'
    })
  },

  refreshRoomDeliveryState() {
    const summary = getRoomDeliverySummary(getCurrentRoom())
    this.setData({
      roomDeliveryActive: summary.active,
      roomDeliveryTitle: summary.title,
      roomDeliveryDetail: summary.detail
    })
  }
})
