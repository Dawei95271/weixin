function getCurrentRoom() {
  return wx.getStorageSync('currentRoomDelivery') || null
}

function saveCurrentRoom(room) {
  const payload = {
    ...room,
    recognizedAt: Date.now()
  }
  wx.setStorageSync('currentRoomDelivery', payload)
  return payload
}

function clearCurrentRoom() {
  wx.removeStorageSync('currentRoomDelivery')
}

function getRoomDeliverySummary(room) {
  if (!room) {
    return {
      active: false,
      title: '当前未启用客房送餐',
      detail: '你可以先识别房间，再按客房送餐场景下单。'
    }
  }
  return {
    active: true,
    title: `已启用客房送餐：${room.roomNo}`,
    detail: `当前房间为 ${room.floorNo} 层 ${room.roomNo}，后续下单会自动带入客房送餐场景。`
  }
}

module.exports = {
  getCurrentRoom,
  saveCurrentRoom,
  clearCurrentRoom,
  getRoomDeliverySummary
}
