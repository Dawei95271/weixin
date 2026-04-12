const { getCart, buildOrderItems, clearCart } = require('../../../utils/cart')
const { request } = require('../../../utils/request')

Page({
  data: {
    cart: [],
    totalAmount: '0.00',
    orderScene: 'NORMAL',
    roomNo: '',
    contactName: '堂食顾客',
    contactPhone: '13800000000',
    remark: '普通点餐',
    submitting: false
  },

  onShow() {
    this.refreshData()
  },

  refreshData() {
    const cart = getCart()
    const currentRoom = wx.getStorageSync('currentRoomDelivery')
    const totalAmount = cart
      .reduce((sum, item) => sum + Number(item.price) * item.quantity, 0)
      .toFixed(2)

    this.setData({
      cart,
      totalAmount,
      orderScene: currentRoom ? 'ROOM_DELIVERY' : 'NORMAL',
      roomNo: currentRoom ? currentRoom.roomNo : '',
      contactName: currentRoom ? `房客-${currentRoom.roomNo}` : '堂食顾客',
      contactPhone: '13800000000',
      remark: currentRoom ? '客房送餐' : '普通点餐'
    })
  },

  onNameInput(event) {
    this.setData({ contactName: event.detail.value })
  },

  onPhoneInput(event) {
    this.setData({ contactPhone: event.detail.value })
  },

  onRemarkInput(event) {
    this.setData({ remark: event.detail.value })
  },

  async submitOrder() {
    if (!this.data.cart.length) {
      wx.showToast({
        title: '购物车为空',
        icon: 'none'
      })
      return
    }

    const currentRoom = wx.getStorageSync('currentRoomDelivery')

    try {
      this.setData({ submitting: true })
      const order = await request('/api/order/submit', 'POST', {
        orderScene: this.data.orderScene,
        roomId: currentRoom ? currentRoom.roomId : null,
        roomNo: currentRoom ? currentRoom.roomNo : '',
        contactName: this.data.contactName,
        contactPhone: this.data.contactPhone,
        remark: this.data.remark,
        items: buildOrderItems()
      })
      clearCart()
      this.setData({ submitting: false })
      wx.navigateTo({
        url: `/pages/order-result/index?orderNo=${order.orderNo}&orderScene=${order.orderScene}&payableAmount=${order.payableAmount}`
      })
    } catch (error) {
      this.setData({ submitting: false })
      wx.showToast({
        title: '提交订单失败',
        icon: 'none'
      })
    }
  }
})
