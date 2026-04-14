const app = getApp()

function getCart() {
  return app.globalData.cart || []
}

function setCart(cart) {
  app.globalData.cart = cart
}

function normalizeCartItem(dish = {}, quantity = 1) {
  return {
    id: dish.id,
    name: dish.name,
    subtitle: dish.subtitle || '',
    description: dish.description || '',
    coverImage: dish.coverImage || '',
    isRecommend: dish.isRecommend || 0,
    supportsRoomDelivery: dish.supportsRoomDelivery || 0,
    price: dish.price,
    quantity
  }
}

function addToCart(dish, quantity = 1) {
  const cart = getCart()
  const existing = cart.find((item) => item.id === dish.id)
  if (existing) {
    existing.quantity += quantity
  } else {
    cart.push(normalizeCartItem(dish, quantity))
  }
  setCart(cart)
  return cart
}

function updateQuantity(id, quantity) {
  let cart = getCart()
  cart = cart
    .map((item) => (item.id === id ? { ...item, quantity } : item))
    .filter((item) => item.quantity > 0)
  setCart(cart)
  return cart
}

function clearCart() {
  setCart([])
  return []
}

function replaceCart(items = []) {
  const normalized = items
    .filter((item) => item && item.id)
    .map((item) => normalizeCartItem(item, item.quantity || 1))
  setCart(normalized)
  return normalized
}

function buildOrderItems() {
  return getCart().map((item) => ({
    dishId: item.id,
    quantity: item.quantity
  }))
}

module.exports = {
  getCart,
  addToCart,
  updateQuantity,
  clearCart,
  replaceCart,
  buildOrderItems
}
