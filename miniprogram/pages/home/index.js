const { request } = require('../../utils/request')

Page({
  data: {
    projectName: '',
    businessScopes: [],
    featuredDishes: [],
    contactPhone: '',
    homeNotice: '',
    businessHours: [],
    deliveryFee: '',
    minOrderAmount: '',
    roomDeliveryNotice: '',
    loading: true
  },

  onLoad() {
    this.loadHome()
  },

  async loadHome() {
    try {
      const data = await request('/api/home/index')
      const dishes = await request('/api/dish/list')
      this.setData({
        projectName: data.projectName,
        businessScopes: data.businessScopes,
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
  }
})
