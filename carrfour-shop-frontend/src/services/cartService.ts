import api from './api';
import type { Cart, CartItem } from '../redux/types';

export const cartService = {
  getCart: async () => {
    const response = await api.get<any>('/cart');
    return {
      ...response.data,
      totalAmount: response.data.totalPrice || response.data.totalAmount || 0,
    } as Cart;
  },

  addItemToCart: async (productId: number, quantity: number) => {
    await api.post<CartItem>('/cart/items', { productId, quantity });
    const cartResponse = await api.get<any>('/cart');
    return {
      ...cartResponse.data,
      totalAmount: cartResponse.data.totalPrice || cartResponse.data.totalAmount || 0,
    } as Cart;
  },

  updateCartItem: async (itemId: number, quantity: number) => {
    await api.put<CartItem>(`/cart/items/${itemId}?quantity=${quantity}`);
    const cartResponse = await api.get<any>('/cart');
    return {
      ...cartResponse.data,
      totalAmount: cartResponse.data.totalPrice || cartResponse.data.totalAmount || 0,
    } as Cart;
  },

  removeItemFromCart: async (itemId: number) => {
    await api.delete(`/cart/items/${itemId}`);
    const cartResponse = await api.get<any>('/cart');
    return {
      ...cartResponse.data,
      totalAmount: cartResponse.data.totalPrice || cartResponse.data.totalAmount || 0,
    } as Cart;
  },

  clearCart: async () => {
    await api.delete('/cart');
  },
};

