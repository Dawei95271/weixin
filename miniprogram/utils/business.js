function parseRange(value) {
  if (!value || typeof value !== 'string' || !value.includes('-')) {
    return null
  }
  const [start, end] = value.split('-').map((item) => item.trim())
  if (!start || !end) {
    return null
  }
  return { start, end }
}

function toMinutes(value) {
  if (!value || !value.includes(':')) {
    return null
  }
  const [hour, minute] = value.split(':').map((item) => Number(item))
  if (Number.isNaN(hour) || Number.isNaN(minute)) {
    return null
  }
  return hour * 60 + minute
}

function getCurrentMinutes() {
  const now = new Date()
  return now.getHours() * 60 + now.getMinutes()
}

function buildBusinessPeriods(config = {}) {
  return [
    { key: 'BREAKFAST_HOURS', label: '早餐', range: parseRange(config.BREAKFAST_HOURS) },
    { key: 'LUNCH_HOURS', label: '中餐', range: parseRange(config.LUNCH_HOURS) },
    { key: 'DINNER_HOURS', label: '晚餐', range: parseRange(config.DINNER_HOURS) }
  ].filter((item) => item.range)
}

function getBusinessStatus(config = {}) {
  const periods = buildBusinessPeriods(config)
  if (!periods.length) {
    return {
      open: false,
      canOrder: false,
      title: '营业时段待配置',
      detail: '当前还没有可用的营业时段配置，请联系商家确认。',
      orderHint: '当前暂不支持在线点餐，请联系商家确认营业时间。',
      reserveHint: '当前仍可提交预约，商家会在营业后跟进。'
    }
  }

  const currentMinutes = getCurrentMinutes()
  for (let index = 0; index < periods.length; index += 1) {
    const item = periods[index]
    const startMinutes = toMinutes(item.range.start)
    const endMinutes = toMinutes(item.range.end)
    if (startMinutes === null || endMinutes === null) {
      continue
    }
    if (currentMinutes >= startMinutes && currentMinutes <= endMinutes) {
      return {
        open: true,
        canOrder: true,
        title: `当前营业：${item.label}`,
        detail: `当前处于${item.label}时段 ${item.range.start}-${item.range.end}，可以继续浏览并下单。`,
        orderHint: `当前处于${item.label}营业时段，可直接提交点餐订单。`,
        reserveHint: `当前处于${item.label}营业时段，可直接提交预约，商家会按流程确认。`
      }
    }
    if (currentMinutes < startMinutes) {
      return {
        open: false,
        canOrder: false,
        title: '当前未到营业时段',
        detail: `下一营业时段为${item.label} ${item.range.start}-${item.range.end}，可先浏览菜品或提前预约。`,
        orderHint: `当前还未到营业时段，在线点餐会在营业后开放。`,
        reserveHint: `当前虽未到营业时段，但仍可先提交预约，商家营业后会尽快确认。`
      }
    }
  }

  const nextPeriod = periods[0]
  return {
    open: false,
    canOrder: false,
    title: '今日营业已结束',
    detail: `下一营业时段为明日${nextPeriod.label} ${nextPeriod.range.start}-${nextPeriod.range.end}，欢迎提前安排点餐或预约。`,
    orderHint: '今日在线点餐已结束，欢迎明天营业时段再来下单。',
    reserveHint: '今日虽已结束营业，但仍可先提交预约，商家会在后续营业时段跟进。'
  }
}

module.exports = {
  buildBusinessPeriods,
  getBusinessStatus
}
