export * from './entities';

import type { User, Product, Cart, Order } from './entities';

export interface AuthState {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  loading: boolean;
  error: string | null;
}

