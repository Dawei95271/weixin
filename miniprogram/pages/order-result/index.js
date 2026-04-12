Page({
  data: {
    orderNo: '',
    orderScene: '',
    payableAmount: ''
  },

  onLoad(options) {
    this.setData({
      orderNo: options.orderNo || '',
      orderScene: options.orderScene || '',
      payableAmount: options.payableAmount || '0.00'
    })
  },

  backToMenu() {
    wx.switchTab({
      url: '/pages/menu/index'
    })
  }
})
