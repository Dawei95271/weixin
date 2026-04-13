const { getCart, updateQuantity, clearCart } = require('../../utils/cart')

Page({
  data: {
    cart: [],
    totalAmount: '0.00',
    totalCount: 0,
    roomInfo: null,
    summaryText: '当前为普通点餐场景'
  },

  onShow() {
    this.refreshCart()
  },

  refreshCart() {
    const cart = getCart()
    const roomInfo = wx.getStorageSync('currentRoomDelivery') || null
    const totalCount = cart.reduce((sum, item) => sum + item.quantity, 0)
    const totalAmount = cart
      .reduce((sum, item) => sum + Number(item.price) * item.quantity, 0)
      .toFixed(2)
    this.setData({
      cart,
      totalAmount,
      totalCount,
      roomInfo,
      summaryText: roomInfo ? '当前为客房送餐场景' : '当前为普通点餐场景'
    })
  },

  increase(event) {
    const id = event.currentTarget.dataset.id
    const item = this.data.cart.find((cartItem) => cartItem.id === id)
    updateQuantity(id, item.quantity + 1)
    this.refreshCart()
  },

  decrease(event) {
    const id = event.currentTarget.dataset.id
    const item = this.data.cart.find((cartItem) => cartItem.id === id)
    updateQuantity(id, item.quantity - 1)
    this.refreshCart()
  },

  clearAll() {
    clearCart()
    this.refreshCart()
  },

  goMenu() {
    wx.switchTab({
      url: '/pages/menu/index'
    })
  },

  clearRoomDelivery() {
    wx.removeStorageSync('currentRoomDelivery')
    this.refreshCart()
    wx.showToast({
      title: '已切换回普通点餐',
      icon: 'success'
    })
  },

  submitOrder() {
    const cart = getCart()
    if (!cart.length) {
      wx.showToast({
        title: '购物车为空',
        icon: 'none'
      })
      return
    }
    wx.navigateTo({
      url: '/pages/order/confirm/index'
    })
  }
})
