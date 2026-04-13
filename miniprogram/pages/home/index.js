const { request } = require('../../utils/request')

Page({
  data: {
    projectName: '',
    businessScopes: [],
    featuredDishes: [],
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
