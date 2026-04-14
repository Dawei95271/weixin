const { request } = require('../../../utils/request')
const { replaceCart } = require('../../../utils/cart')

Page({
  data: {
    order: null,
    loading: true
  },

  onLoad(options) {
    this.loadDetail(options.id)
  },

  async loadDetail(id) {
    try {
      const order = await request(`/api/order/detail/${id}`)
      this.setData({
        order: {
          ...order,
          orderSceneLabel: this.formatOrderScene(order.orderScene),
          orderStatusLabel: this.formatOrderStatus(order.orderStatus)
        },
        loading: false
      })
    } catch (error) {
      this.setData({ loading: false })
      wx.showToast({
        title: '订单详情加载失败',
        icon: 'none'
      })
    }
  },

  formatOrderScene(value) {
    const map = {
      NORMAL: '普通点餐',
      ROOM_DELIVERY: '客房送餐',
      PRIVATE_ROOM_PREORDER: '包间预点菜'
    }
    return map[value] || value || '未知场景'
  },

  formatOrderStatus(value) {
    const map = {
      WAIT_PAY: '待支付',
      WAIT_ACCEPT: '待接单',
      COOKING: '制作中',
      DELIVERING: '配送中',
      COMPLETED: '已完成',
      CANCELLED: '已取消'
    }
    return map[value] || value || '未知状态'
  },

  reorder() {
    const order = this.data.order
    if (!order || !order.items || !order.items.length) {
      wx.showToast({
        title: '当前订单没有可复购菜品',
        icon: 'none'
      })
      return
    }
    replaceCart(order.items.map((item) => ({
      id: item.dishId,
      name: item.dishName,
      price: item.unitPrice,
      quantity: item.quantity
    })))
    wx.showToast({
      title: '已加入购物车',
      icon: 'success'
    })
    setTimeout(() => {
      wx.switchTab({
        url: '/pages/cart/index'
      })
    }, 300)
  }
})
