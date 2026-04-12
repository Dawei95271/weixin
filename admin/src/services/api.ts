import axios from 'axios'

const request = axios.create({
  baseURL: '/',
  timeout: 10000
})

request.interceptors.request.use((config) => {
  const token = localStorage.getItem('admin_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

request.interceptors.response.use((response) => {
  if (response.data.code !== 0) {
    return Promise.reject(new Error(response.data.message || '请求失败'))
  }
  return response.data.data
})

export const login = (payload: { username: string; password: string }) =>
  request.post('/admin/auth/login', payload)

export const fetchOrders = () => request.get('/admin/order/list')
export const fetchOrderDetail = (id: number) => request.get(`/admin/order/detail/${id}`)

export const fetchPrivateRoomReservations = () => request.get('/admin/private-room/reservation/list')
export const fetchPrivateRoomReservationDetail = (id: number) =>
  request.get(`/admin/private-room/reservation/detail/${id}`)

export const fetchBanquetReservations = () => request.get('/admin/banquet/list')
export const fetchBanquetReservationDetail = (id: number) => request.get(`/admin/banquet/detail/${id}`)
export const fetchDishes = () => request.get('/admin/dish/list')
export const fetchDishCategories = () => request.get('/admin/dish-category/list')

export const updateOrderStatus = (payload: { orderId: number; orderStatus: string }) =>
  request.post('/admin/order/status', payload)

export const updatePrivateRoomReservationStatus = (payload: { reservationId: number; reservationStatus: string }) =>
  request.post('/admin/private-room/reservation/status', payload)

export const updateBanquetReservationStatus = (payload: { reservationId: number; status: string }) =>
  request.post('/admin/banquet/status', payload)

export const saveDish = (payload: {
  id?: number
  categoryId: number
  name: string
  subtitle: string
  description: string
  basePrice: number
  supportsRoomDelivery: number
  isRecommend: number
}) => request.post('/admin/dish/save', payload)

export const updateDishStatus = (payload: { id: number; status: number }) =>
  request.post('/admin/dish/status', payload)

export const saveDishCategory = (payload: { id?: number; name: string; sort: number }) =>
  request.post('/admin/dish-category/save', payload)

export const updateDishCategoryStatus = (payload: { id: number; status: number }) =>
  request.post('/admin/dish-category/status', payload)
