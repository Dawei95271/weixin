const { getCart, buildOrderItems, clearCart } = require('../../../utils/cart')
const { request } = require('../../../utils/request')
const { getBusinessStatus } = require('../../../utils/business')

Page({
  data: {
    cart: [],
    itemTotalAmount: '0.00',
    deliveryFee: '0.00',
    deliveryFeeConfig: '0.00',
    totalAmount: '0.00',
    orderScene: 'NORMAL',
    orderSceneLabel: '普通点餐',
    sceneHint: '当前订单按普通点餐处理，可直接确认提交。',
    roomNo: '',
    contactName: '堂食顾客',
    contactPhone: '13800000000',
    remark: '普通点餐',
    minOrderAmount: '0.00',
    roomDeliveryNotice: '',
    merchantPhone: '',
    businessStatusTitle: '',
    businessStatusDetail: '',
    submitting: false
  },

  onShow() {
    this.loadConfigAndRefresh()
  },

  async loadConfigAndRefresh() {
    try {
      const config = await request('/api/config/public')
      this.setData({
        minOrderAmount: config.MIN_ORDER_AMOUNT || '0.00',
        deliveryFeeConfig: config.DELIVERY_FEE || '0.00',
        roomDeliveryNotice: config.ROOM_DELIVERY_NOTICE || '',
        merchantPhone: config.CONTACT_PHONE || '',
        businessStatusTitle: getBusinessStatus(config).title,
        businessStatusDetail: getBusinessStatus(config).detail
      })
    } catch (error) {
      // keep page usable even if config loading fails
    }
    this.refreshData()
  },

  refreshData() {
    const cart = getCart().map((item) => ({
      ...item,
      lineTotal: (Number(item.price) * item.quantity).toFixed(2)
    }))
    const currentRoom = wx.getStorageSync('currentRoomDelivery')
    const itemTotalAmount = cart
      .reduce((sum, item) => sum + Number(item.price) * item.quantity, 0)
      .toFixed(2)
    const deliveryFee = currentRoom ? this.data.deliveryFeeConfig || '0.00' : '0.00'
    const totalAmount = (Number(itemTotalAmount) + Number(deliveryFee)).toFixed(2)

    this.setData({
      cart,
      itemTotalAmount,
      deliveryFee,
      totalAmount,
      orderScene: currentRoom ? 'ROOM_DELIVERY' : 'NORMAL',
      orderSceneLabel: currentRoom ? '客房送餐' : '普通点餐',
      sceneHint: currentRoom
        ? this.data.roomDeliveryNotice || '当前订单会按客房送餐处理，请确认房号与联系电话。'
        : '当前订单按普通点餐处理，可直接确认提交。',
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

  callMerchant() {
    if (!this.data.merchantPhone) {
      wx.showToast({
        title: '暂未配置联系电话',
        icon: 'none'
      })
      return
    }
    wx.makePhoneCall({
      phoneNumber: this.data.merchantPhone
    })
  },

  validateForm() {
    if (!this.data.cart.length) {
      wx.showToast({
        title: '购物车为空',
        icon: 'none'
      })
      return false
    }
    if (!this.data.contactName.trim()) {
      wx.showToast({
        title: '请填写联系人',
        icon: 'none'
      })
      return false
    }
    if (!/^1\d{10}$/.test(this.data.contactPhone)) {
      wx.showToast({
        title: '请输入正确手机号',
        icon: 'none'
      })
      return false
    }
    if (Number(this.data.orderScene === 'ROOM_DELIVERY' ? this.data.totalAmount : this.data.itemTotalAmount) < Number(this.data.minOrderAmount || 0)) {
      wx.showToast({
        title: `未达到起送金额 ¥${this.data.minOrderAmount}`,
        icon: 'none'
      })
      return false
    }
    return true
  },

  async submitOrder() {
    if (!this.validateForm()) {
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
