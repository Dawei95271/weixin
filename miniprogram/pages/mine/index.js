Page({
  data: {
    quickActions: [
      {
        title: '我的订单',
        desc: '查看点餐订单、客房送餐和订单详情',
        badge: 'ORDER',
        url: '/pages/order/list/index'
      },
      {
        title: '我的预约',
        desc: '集中查看包间预约和宴席预约记录',
        badge: 'BOOKING',
        url: '/pages/reservation/list/index'
      },
      {
        title: '客房点餐',
        desc: '先识别房间，再进入客房送餐场景下单',
        badge: 'ROOM',
        url: '/pages/room/index'
      },
      {
        title: '包间预约',
        desc: '选择日期、时段和包间，提前锁定用餐安排',
        badge: 'VIP',
        url: '/pages/private-room/index'
      },
      {
        title: '宴席预约',
        desc: '婚宴、寿宴、商务宴线上留资，后续人工跟进',
        badge: 'BANQUET',
        url: '/pages/banquet/index'
      }
    ],
    tips: [
      '客房住客建议先扫码识别房间，再提交送餐订单。',
      '包间预约支持先预约时段，再补充预点菜信息。',
      '宴席预约提交后，后台会尽快联系你确认档期和需求。'
    ]
  },

  goFeature(event) {
    const { url } = event.currentTarget.dataset
    if (!url) {
      wx.showToast({
        title: '该功能下一步接入',
        icon: 'none'
      })
      return
    }
    wx.navigateTo({
      url
    })
  },

  goMenu() {
    wx.switchTab({
      url: '/pages/menu/index'
    })
  }
})
