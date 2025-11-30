import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { orderService } from '../../services/orderService';
import type { OrderState, Order, CreateOrderData } from '../types';
import type { RootState } from '../store';

const initialState: OrderState = {
  orders: [],
  currentOrder: null,
  loading: false,
  error: null,
};

export const createOrder = createAsyncThunk<
  Order,
  CreateOrderData,
  { rejectValue: string }
>(
  'orders/createOrder',
  async (data, { rejectWithValue }) => {
    try {
      const order = await orderService.createOrder(data);
      return order;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la création de la commande');
    }
  }
);

export const fetchUserOrders = createAsyncThunk<
  Order[],
  void,
  { rejectValue: string; state: RootState }
>(
  'orders/fetchUserOrders',
  async (_, { rejectWithValue }) => {
    try {
      const orders = await orderService.getUserOrders();
      return orders;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la récupération des commandes');
    }
  },
  {
    condition: (_, { getState }) => {
      const { orders } = getState();
      // Don't fetch if already loading
      if (orders.loading) {
        return false;
      }
      return true;
    },
  }
);

export const fetchOrderById = createAsyncThunk<
  Order,
  number,
  { rejectValue: string }
>(
  'orders/fetchOrderById',
  async (id, { rejectWithValue }) => {
    try {
      const order = await orderService.getOrderById(id);
      return order;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la récupération de la commande');
    }
  }
);

const orderSlice = createSlice({
  name: 'orders',
  initialState,
  reducers: {
    clearCurrentOrder: (state) => {
      state.currentOrder = null;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(createOrder.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(createOrder.fulfilled, (state, action) => {
        state.loading = false;
        state.currentOrder = action.payload;
        state.orders.push(action.payload);
      })
      .addCase(createOrder.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || 'Erreur lors de la création de la commande';
      })
      .addCase(fetchUserOrders.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchUserOrders.fulfilled, (state, action) => {
        state.loading = false;
        state.orders = action.payload;
      })
      .addCase(fetchUserOrders.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || 'Erreur lors de la récupération des commandes';
      })
      .addCase(fetchOrderById.fulfilled, (state, action) => {
        state.currentOrder = action.payload;
      });
  },
});

export const { clearCurrentOrder } = orderSlice.actions;
export default orderSlice.reducer;

