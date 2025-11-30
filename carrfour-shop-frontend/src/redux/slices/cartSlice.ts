import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { cartService } from '../../services/cartService';
import type { CartState, Cart } from '../types';
import type { RootState } from '../store';

const initialState: CartState = {
  cart: null,
  loading: false,
  error: null,
};

export const fetchCart = createAsyncThunk<
  Cart,
  void,
  { rejectValue: string; state: RootState }
>(
  'cart/fetchCart',
  async (_, { rejectWithValue }) => {
    try {
      const cart = await cartService.getCart();
      return cart;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la récupération du panier');
    }
  },
  {
    condition: (_, { getState }) => {
      const { cart } = getState();
      if (cart.loading || cart.cart !== null) {
        return false;
      }
      return true;
    },
  }
);

export const addToCart = createAsyncThunk<
  Cart,
  { productId: number; quantity: number },
  { rejectValue: string }
>(
  'cart/addToCart',
  async ({ productId, quantity }, { rejectWithValue }) => {
    try {
      const cart = await cartService.addItemToCart(productId, quantity);
      return cart;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de l\'ajout au panier');
    }
  }
);

export const updateCartItem = createAsyncThunk<
  Cart,
  { itemId: number; quantity: number },
  { rejectValue: string }
>(
  'cart/updateCartItem',
  async ({ itemId, quantity }, { rejectWithValue }) => {
    try {
      const cart = await cartService.updateCartItem(itemId, quantity);
      return cart;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la mise à jour du panier');
    }
  }
);

export const removeFromCart = createAsyncThunk<
  Cart,
  number,
  { rejectValue: string }
>(
  'cart/removeFromCart',
  async (itemId, { rejectWithValue }) => {
    try {
      const cart = await cartService.removeItemFromCart(itemId);
      return cart;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la suppression du panier');
    }
  }
);

export const clearCart = createAsyncThunk<
  null,
  void,
  { rejectValue: string }
>(
  'cart/clearCart',
  async (_, { rejectWithValue }) => {
    try {
      await cartService.clearCart();
      return null;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la suppression du panier');
    }
  }
);

const cartSlice = createSlice({
  name: 'cart',
  initialState,
  reducers: {
    resetCart: (state) => {
      state.cart = null;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchCart.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchCart.fulfilled, (state, action) => {
        state.loading = false;
        state.cart = action.payload;
      })
      .addCase(fetchCart.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || 'Erreur lors de la récupération du panier';
      })
      .addCase(addToCart.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(addToCart.fulfilled, (state, action) => {
        state.loading = false;
        state.cart = action.payload;
      })
      .addCase(addToCart.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || 'Erreur lors de l\'ajout au panier';
      })
      .addCase(updateCartItem.fulfilled, (state, action) => {
        state.cart = action.payload;
      })
      .addCase(removeFromCart.fulfilled, (state, action) => {
        state.cart = action.payload;
      })
      .addCase(clearCart.fulfilled, (state) => {
        state.cart = null;
      });
  },
});

export const { resetCart } = cartSlice.actions;
export default cartSlice.reducer;

