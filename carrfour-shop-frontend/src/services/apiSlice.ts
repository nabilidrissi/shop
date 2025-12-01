import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';
import type {
  User,
  Product,
  Cart,
  Order,
  RegisterData,
  LoginData,
  CreateOrderData,
  ApiError,
} from '../types';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

const baseQuery = fetchBaseQuery({
  baseUrl: API_BASE_URL,
  prepareHeaders: (headers, { getState }) => {
    const token = localStorage.getItem('token');
    if (token) {
      headers.set('authorization', `Bearer ${token}`);
    }
    return headers;
  },
});

const baseQueryWithReauth = async (args: any, api: any, extraOptions: any) => {
  let result = await baseQuery(args, api, extraOptions);
  
  if (result.error && result.error.status === 401) {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    if (window.location.pathname !== '/login' && window.location.pathname !== '/register') {
      window.location.href = '/login';
    }
  }
  
  return result;
};

export const apiSlice = createApi({
  reducerPath: 'api',
  baseQuery: baseQueryWithReauth,
  tagTypes: ['User', 'Product', 'Cart', 'Order'],
  endpoints: (builder) => ({
    register: builder.mutation<{ message: string }, RegisterData>({
      query: (data) => ({
        url: '/auth/register',
        method: 'POST',
        body: data,
      }),
    }),

    login: builder.mutation<{ token: string; user: User }, LoginData>({
      query: (data) => ({
        url: '/auth/login',
        method: 'POST',
        body: data,
      }),
      invalidatesTags: ['User', 'Cart'],
    }),

    getCurrentUser: builder.query<User, void>({
      query: () => '/auth/me',
      providesTags: ['User'],
    }),

    logout: builder.mutation<{ message: string }, void>({
      query: () => ({
        url: '/auth/logout',
        method: 'POST',
      }),
      invalidatesTags: ['User', 'Cart'],
    }),

    getProducts: builder.query<Product[], void>({
      query: () => '/products',
      providesTags: ['Product'],
    }),

    getProductById: builder.query<Product, number>({
      query: (id) => `/products/${id}`,
      providesTags: (result, error, id) => [{ type: 'Product', id }],
    }),

    getProductsByCategory: builder.query<Product[], string>({
      query: (category) => `/products/category/${category}`,
      providesTags: ['Product'],
    }),

    searchProducts: builder.query<Product[], string>({
      query: (keyword) => `/products/search?keyword=${keyword}`,
      providesTags: ['Product'],
    }),

    getCart: builder.query<Cart, void>({
      query: () => '/cart',
      providesTags: ['Cart'],
      transformResponse: (response: any) => ({
        ...response,
        totalAmount: response.totalPrice || response.totalAmount || 0,
      }),
    }),

    addToCart: builder.mutation<Cart, { productId: number; quantity: number }>({
      query: ({ productId, quantity }) => ({
        url: '/cart/items',
        method: 'POST',
        body: { productId, quantity },
      }),
      invalidatesTags: ['Cart'],
      async onQueryStarted({ productId, quantity }, { dispatch, queryFulfilled }) {
        try {
          await queryFulfilled;
          dispatch(apiSlice.endpoints.getCart.initiate(undefined, { forceRefetch: true }));
        } catch {}
      },
    }),

    updateCartItem: builder.mutation<Cart, { itemId: number; quantity: number }>({
      query: ({ itemId, quantity }) => ({
        url: `/cart/items/${itemId}?quantity=${quantity}`,
        method: 'PUT',
      }),
      invalidatesTags: ['Cart'],
      async onQueryStarted({ itemId, quantity }, { dispatch, queryFulfilled }) {
        try {
          await queryFulfilled;
          dispatch(apiSlice.endpoints.getCart.initiate(undefined, { forceRefetch: true }));
        } catch {}
      },
    }),

    removeFromCart: builder.mutation<Cart, number>({
      query: (itemId) => ({
        url: `/cart/items/${itemId}`,
        method: 'DELETE',
      }),
      invalidatesTags: ['Cart'],
      async onQueryStarted(itemId, { dispatch, queryFulfilled }) {
        try {
          await queryFulfilled;
          dispatch(apiSlice.endpoints.getCart.initiate(undefined, { forceRefetch: true }));
        } catch {}
      },
    }),

    clearCart: builder.mutation<void, void>({
      query: () => ({
        url: '/cart',
        method: 'DELETE',
      }),
      invalidatesTags: ['Cart'],
    }),

    createOrder: builder.mutation<Order, CreateOrderData>({
      query: (data) => ({
        url: '/orders',
        method: 'POST',
        body: data,
      }),
      invalidatesTags: ['Order', 'Cart'],
      transformResponse: (response: any) => ({
        ...response,
        totalAmount: response.totalPrice || response.totalAmount || 0,
      }),
    }),

    getUserOrders: builder.query<Order[], void>({
      query: () => '/orders',
      providesTags: ['Order'],
      transformResponse: (response: any[]) =>
        response.map((order) => ({
          ...order,
          totalAmount: order.totalPrice || order.totalAmount || 0,
        })),
    }),

    getOrderById: builder.query<Order, number>({
      query: (id) => `/orders/${id}`,
      providesTags: (result, error, id) => [{ type: 'Order', id }],
      transformResponse: (response: any) => ({
        ...response,
        totalAmount: response.totalPrice || response.totalAmount || 0,
      }),
    }),

    updateOrderStatus: builder.mutation<Order, { orderId: number; status: string }>({
      query: ({ orderId, status }) => ({
        url: `/orders/${orderId}/status?status=${status}`,
        method: 'PUT',
      }),
      invalidatesTags: ['Order'],
      transformResponse: (response: any) => ({
        ...response,
        totalAmount: response.totalPrice || response.totalAmount || 0,
      }),
    }),
  }),
});

export const {
  useRegisterMutation,
  useLoginMutation,
  useGetCurrentUserQuery,
  useLogoutMutation,
  useGetProductsQuery,
  useGetProductByIdQuery,
  useGetProductsByCategoryQuery,
  useSearchProductsQuery,
  useGetCartQuery,
  useAddToCartMutation,
  useUpdateCartItemMutation,
  useRemoveFromCartMutation,
  useClearCartMutation,
  useCreateOrderMutation,
  useGetUserOrdersQuery,
  useGetOrderByIdQuery,
  useUpdateOrderStatusMutation,
} = apiSlice;
