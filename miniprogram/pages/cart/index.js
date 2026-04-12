const { getCart, updateQuantity, clearCart, buildOrderItems } = require('../../utils/cart')
const { request } = require('../../utils/request')

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

    const currentRoom = wx.getStorageSync('currentRoomDelivery')
    const orderScene = currentRoom ? 'ROOM_DELIVERY' : 'NORMAL'

    request('/api/order/submit', 'POST', {
      orderScene,
      roomId: currentRoom ? currentRoom.roomId : null,
      roomNo: currentRoom ? currentRoom.roomNo : '',
      contactName: currentRoom ? `房客-${currentRoom.roomNo}` : '堂食顾客',
      contactPhone: '13800000000',
      remark: currentRoom ? '客房送餐' : '普通点餐',
      items: buildOrderItems()
    }).then((order) => {
      clearCart()
      this.refreshCart()
      wx.navigateTo({
        url: `/pages/order-result/index?orderNo=${order.orderNo}&orderScene=${order.orderScene}&payableAmount=${order.payableAmount}`
      })
    }).catch(() => {
      wx.showToast({
        title: '提交订单失败',
        icon: 'none'
      })
    })
  }
})
