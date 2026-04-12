const { request } = require('../../utils/request')
const { addToCart } = require('../../utils/cart')

Page({
  data: {
    categories: [],
    dishes: [],
    currentCategoryId: null,
    loading: true
  },

  onLoad() {
    this.loadData()
  },

  async loadData() {
    try {
      const categories = await request('/api/dish/category/list')
      const currentCategoryId = categories.length ? categories[0].id : null
      const dishes = await request('/api/dish/list', 'GET', currentCategoryId ? { categoryId: currentCategoryId } : {})
      this.setData({
        categories,
        currentCategoryId,
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
  }
})
