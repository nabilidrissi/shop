import api from './api';
import type { Product } from '../redux/types';

export const productService = {
  getAllProducts: async () => {
    const response = await api.get<Product[]>('/products');
    return response.data;
  },

  getProductById: async (id: number) => {
    const response = await api.get<Product>(`/products/${id}`);
    return response.data;
  },

  getProductsByCategory: async (category: string) => {
    const response = await api.get<Product[]>(`/products/category/${category}`);
    return response.data;
  },

  searchProducts: async (keyword: string) => {
    const response = await api.get<Product[]>(`/products/search?keyword=${keyword}`);
    return response.data;
  },
};

