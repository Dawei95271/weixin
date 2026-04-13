const { request } = require('../../utils/request')

Page({
  data: {
    code: 'ROOM-801',
    room: null,
    loading: false
  },

  onInput(event) {
    this.setData({
      code: event.detail.value
    })
  },

  async parseRoom() {
    if (!this.data.code) {
      wx.showToast({
        title: '请输入房间码',
        icon: 'none'
      })
      return
    }

    try {
      this.setData({ loading: true })
      const room = await request(`/api/room/scan/${this.data.code}`)
      this.setData({
        room,
        loading: false
      })
      wx.setStorageSync('currentRoomDelivery', room)
      wx.showToast({
        title: '已识别房间',
        icon: 'success'
      })
    } catch (error) {
      this.setData({ loading: false })
      wx.showToast({
        title: '识别失败',
        icon: 'none'
      })
    }
  },

  goMenu() {
    wx.switchTab({
      url: '/pages/menu/index'
    })
  },

  clearRoom() {
    wx.removeStorageSync('currentRoomDelivery')
    this.setData({
      room: null
    })
    wx.showToast({
      title: '已清除房间识别',
      icon: 'success'
    })
  }
})
