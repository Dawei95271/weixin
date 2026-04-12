const { getCart, updateQuantity, clearCart } = require('../../utils/cart')

Page({
  data: {
    cart: [],
    totalAmount: '0.00'
  },

  onShow() {
    this.refreshCart()
  },

  refreshCart() {
    const cart = getCart()
    const totalAmount = cart
      .reduce((sum, item) => sum + Number(item.price) * item.quantity, 0)
      .toFixed(2)
    this.setData({
      cart,
      totalAmount
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
