const app = getApp()

function request(url, method = 'GET', data = {}) {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${app.globalData.apiBaseUrl}${url}`,
      method,
      data,
      success(res) {
        if (res.data && res.data.code === 0) {
          resolve(res.data.data)
          return
        }
        reject(new Error((res.data && res.data.message) || '请求失败'))
      },
      fail(error) {
        reject(error)
      }
    })
  })
}

module.exports = {
  request
}
