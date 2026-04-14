const { request } = require('../../../utils/request')
const { addToCart } = require('../../../utils/cart')
const { getBusinessStatus } = require('../../../utils/business')

Page({
  data: {
    dish: null,
    quantity: 1,
    merchantPhone: '',
    deliveryFee: '0.00',
    minOrderAmount: '0.00',
    roomDeliveryNotice: '',
    businessStatusTitle: '',
    businessStatusDetail: '',
    businessOpen: false,
    loading: true
  },

  onLoad(options) {
    this.loadPageData(options.id)
  },

  async loadPageData(id) {
    try {
      const [dish, config] = await Promise.all([
        request(`/api/dish/detail/${id}`),
        request('/api/config/public')
      ])
      const businessStatus = getBusinessStatus(config)
      this.setData({
        dish,
        merchantPhone: config.CONTACT_PHONE || '',
        deliveryFee: config.DELIVERY_FEE || '0.00',
        minOrderAmount: config.MIN_ORDER_AMOUNT || '0.00',
        roomDeliveryNotice: config.ROOM_DELIVERY_NOTICE || '',
        businessStatusTitle: businessStatus.title,
        businessStatusDetail: businessStatus.detail,
        businessOpen: businessStatus.open,
        loading: false
      })
    } catch (error) {
      this.setData({ loading: false })
      wx.showToast({
        title: '菜品详情加载失败',
        icon: 'none'
      })
    }
  },

  decreaseQuantity() {
    if (this.data.quantity <= 1) {
      return
    }
    this.setData({
      quantity: this.data.quantity - 1
    })
  },

  increaseQuantity() {
    this.setData({
      quantity: this.data.quantity + 1
    })
  },

  addCurrentDish() {
    if (!this.data.dish) {
      return
    }
    addToCart(this.data.dish, this.data.quantity)
    wx.showToast({
      title: '已加入购物车',
      icon: 'success'
    })
  },

  goCart() {
    wx.switchTab({
      url: '/pages/cart/index'
    })
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
  }
})
