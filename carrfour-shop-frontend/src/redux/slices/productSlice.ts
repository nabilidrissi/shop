import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { productService } from '../../services/productService';
import type { ProductState, Product } from '../types';
import type { RootState } from '../store';

const initialState: ProductState = {
  products: [],
  currentProduct: null,
  loading: false,
  error: null,
};

export const fetchProducts = createAsyncThunk<
  Product[],
  void,
  { rejectValue: string; state: RootState }
>(
  'products/fetchProducts',
  async (_, { rejectWithValue }) => {
    try {
      const products = await productService.getAllProducts();
      return products;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la récupération des produits');
    }
  },
  {
    condition: (_, { getState }) => {
      const { products } = getState();
      if (products.loading) {
        return false;
      }
      return true;
    },
  }
);

export const fetchProductById = createAsyncThunk<
  Product,
  number,
  { rejectValue: string }
>(
  'products/fetchProductById',
  async (id, { rejectWithValue }) => {
    try {
      const product = await productService.getProductById(id);
      return product;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la récupération du produit');
    }
  }
);

export const searchProducts = createAsyncThunk<
  Product[],
  string,
  { rejectValue: string }
>(
  'products/searchProducts',
  async (keyword, { rejectWithValue }) => {
    try {
      const products = await productService.searchProducts(keyword);
      return products;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la recherche');
    }
  }
);

const productSlice = createSlice({
  name: 'products',
  initialState,
  reducers: {
    clearCurrentProduct: (state) => {
      state.currentProduct = null;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchProducts.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchProducts.fulfilled, (state, action) => {
        state.loading = false;
        state.products = action.payload;
      })
      .addCase(fetchProducts.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || 'Erreur lors de la récupération des produits';
      })
      .addCase(fetchProductById.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchProductById.fulfilled, (state, action) => {
        state.loading = false;
        state.currentProduct = action.payload;
      })
      .addCase(fetchProductById.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || 'Erreur lors de la récupération du produit';
      })
      .addCase(searchProducts.fulfilled, (state, action) => {
        state.products = action.payload;
      });
  },
});

export const { clearCurrentProduct } = productSlice.actions;
export default productSlice.reducer;

