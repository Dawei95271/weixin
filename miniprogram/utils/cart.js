const app = getApp()

function getCart() {
  return app.globalData.cart || []
}

function setCart(cart) {
  app.globalData.cart = cart
}

function addToCart(dish) {
  const cart = getCart()
  const existing = cart.find((item) => item.id === dish.id)
  if (existing) {
    existing.quantity += 1
  } else {
    cart.push({
      id: dish.id,
      name: dish.name,
      price: dish.price,
      quantity: 1
    })
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
  buildOrderItems
}
