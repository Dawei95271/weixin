const { request } = require('../../utils/request')

Page({
  data: {
    projectName: '',
    businessScopes: [],
    loading: true
  },

  onLoad() {
    this.loadHome()
  },

  async loadHome() {
    try {
      const data = await request('/api/home/index')
      this.setData({
        projectName: data.projectName,
        businessScopes: data.businessScopes,
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
  }
})
