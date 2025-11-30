import api from './api';
import type { RegisterData, LoginData, User } from '../redux/types';

export const authService = {
  register: async (data: RegisterData) => {
    const response = await api.post<{ message: string }>('/auth/register', data);
    return response.data;
  },

  login: async (data: LoginData) => {
    const response = await api.post<{ token: string; user: User }>('/auth/login', data);
    return response.data;
  },

  getCurrentUser: async () => {
    const response = await api.get<User>('/auth/me');
    return response.data;
  },

  logout: async () => {
    const response = await api.post<{ message: string }>('/auth/logout');
    return response.data;
  },
};

