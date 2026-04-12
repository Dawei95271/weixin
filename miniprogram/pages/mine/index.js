Page({
  data: {
    sections: [
      {
        title: '我的订单',
        desc: '查看已提交订单和订单详情',
        url: '/pages/order/list/index'
      },
      {
        title: '包间预约',
        desc: '查看包间预约和宴席预约记录',
        url: '/pages/reservation/list/index'
      },
      {
        title: '宴席预约',
        desc: '提交婚宴、寿宴等宴席咨询需求',
        url: '/pages/banquet/index'
      }
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
  }
})
