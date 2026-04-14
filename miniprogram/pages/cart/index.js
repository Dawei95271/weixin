const { getCart, updateQuantity, clearCart } = require('../../utils/cart')
const { request } = require('../../utils/request')
const { getCurrentRoom, clearCurrentRoom, getRoomDeliverySummary } = require('../../utils/room-delivery')

Page({
  data: {
    cart: [],
    totalAmount: '0.00',
    totalCount: 0,
    roomInfo: null,
    summaryText: '当前为普通点餐场景',
    roomDeliveryTitle: '',
    roomDeliveryDetail: '',
    deliveryFee: '0.00',
    minOrderAmount: '0.00',
    contactPhone: '',
    roomDeliveryNotice: ''
  },

  onShow() {
    this.loadConfigAndCart()
  },

  async loadConfigAndCart() {
    try {
      const config = await request('/api/config/public')
      this.setData({
        deliveryFee: config.DELIVERY_FEE || '0.00',
        minOrderAmount: config.MIN_ORDER_AMOUNT || '0.00',
        contactPhone: config.CONTACT_PHONE || '',
        roomDeliveryNotice: config.ROOM_DELIVERY_NOTICE || ''
      })
    } catch (error) {
      // keep page usable even if config loading fails
    }
    this.refreshCart()
  },

  refreshCart() {
    const cart = getCart()
    const roomInfo = getCurrentRoom()
    const roomSummary = getRoomDeliverySummary(roomInfo)
    const totalCount = cart.reduce((sum, item) => sum + item.quantity, 0)
    const itemAmount = cart.reduce((sum, item) => sum + Number(item.price) * item.quantity, 0)
    const deliveryFee = roomInfo ? Number(this.data.deliveryFee || 0) : 0
    const totalAmount = (itemAmount + deliveryFee).toFixed(2)
    this.setData({
      cart,
      totalAmount,
      totalCount,
      roomInfo,
      summaryText: roomInfo ? '当前为客房送餐场景' : '当前为普通点餐场景',
      roomDeliveryTitle: roomSummary.title,
      roomDeliveryDetail: roomSummary.detail
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

  callMerchant() {
    if (!this.data.contactPhone) {
      wx.showToast({
        title: '暂未配置联系电话',
        icon: 'none'
      })
      return
    }
    wx.makePhoneCall({
      phoneNumber: this.data.contactPhone
    })
  },

  clearRoomDelivery() {
    clearCurrentRoom()
    this.refreshCart()
    wx.showToast({
      title: '已切换回普通点餐',
      icon: 'success'
    })
  },

  goRoomDelivery() {
    wx.navigateTo({
      url: '/pages/room/index'
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
