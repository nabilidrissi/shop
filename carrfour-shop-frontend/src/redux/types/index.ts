export * from './entities';

import type { User, Product, Cart, Order } from './entities';

export interface AuthState {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  loading: boolean;
  error: string | null;
}

export interface ProductState {
  products: Product[];
  currentProduct: Product | null;
  loading: boolean;
  error: string | null;
}

export interface CartState {
  cart: Cart | null;
  loading: boolean;
  error: string | null;
}

export interface OrderState {
  orders: Order[];
  currentOrder: Order | null;
  loading: boolean;
  error: string | null;
}

