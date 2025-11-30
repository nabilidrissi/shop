export interface User {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  phone: string;
  role?: string;
}

export interface Product {
  id: number;
  name: string;
  description: string;
  price: number;
  imageUrl?: string;
  category: string;
  stock: number;
}

export interface CartItem {
  id: number;
  productId: number;
  quantity: number;
  product: Product;
}

export interface Cart {
  id: number;
  items: CartItem[];
  totalAmount: number;
}

export interface Order {
  id: number;
  userId: number;
  items: CartItem[];
  totalAmount: number;
  shippingAddress: string;
  billingAddress: string;
  phone: string;
  email: string;
  status: OrderStatus;
  createdAt: string;
}

export type OrderStatus = 
  | 'PENDING' 
  | 'CONFIRMED' 
  | 'PROCESSING' 
  | 'SHIPPED' 
  | 'DELIVERED' 
  | 'CANCELLED';

export interface RegisterData {
  password: string;
  email: string;
  firstName: string;
  lastName: string;
  phone: string;
}

export interface LoginData {
  email: string;
  password: string;
}

export interface CreateOrderData {
  shippingAddress: string;
  billingAddress: string;
  phone: string;
  email: string;
}

