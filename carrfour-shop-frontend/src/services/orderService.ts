import api from './api';
import type { Order, CreateOrderData } from '../redux/types';

export const orderService = {
  createOrder: async (data: CreateOrderData) => {
    const response = await api.post<any>('/orders', data);
    return {
      ...response.data,
      totalAmount: response.data.totalPrice || response.data.totalAmount || 0,
    } as Order;
  },

  getUserOrders: async () => {
    const response = await api.get<any[]>('/orders');
    return response.data.map((order) => ({
      ...order,
      totalAmount: order.totalPrice || order.totalAmount || 0,
    })) as Order[];
  },

  getOrderById: async (id: number) => {
    const response = await api.get<any>(`/orders/${id}`);
    return {
      ...response.data,
      totalAmount: response.data.totalPrice || response.data.totalAmount || 0,
    } as Order;
  },

  updateOrderStatus: async (orderId: number, status: string) => {
    const response = await api.put<any>(`/orders/${orderId}/status?status=${status}`);
    return {
      ...response.data,
      totalAmount: response.data.totalPrice || response.data.totalAmount || 0,
    } as Order;
  },
};

