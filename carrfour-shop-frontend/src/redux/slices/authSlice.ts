import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { authService } from '../../services/authService';
import type { AuthState, User, RegisterData, LoginData } from '../types';

const getStoredUser = (): User | null => {
  try {
    const userStr = localStorage.getItem('user');
    if (!userStr) return null;
    return JSON.parse(userStr);
  } catch {
    return null;
  }
};

const initialState: AuthState = {
  user: getStoredUser(),
  token: localStorage.getItem('token'),
  isAuthenticated: !!localStorage.getItem('token'),
  loading: false,
  error: null,
};

export const register = createAsyncThunk<
  { message: string },
  RegisterData,
  { rejectValue: string }
>(
  'auth/register',
  async (data, { rejectWithValue }) => {
    try {
      const response = await authService.register(data);
      return response;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de l\'inscription');
    }
  }
);

export const login = createAsyncThunk<
  { token: string; user: User },
  LoginData,
  { rejectValue: string }
>(
  'auth/login',
  async (data, { rejectWithValue }) => {
    try {
      const response = await authService.login(data);
      localStorage.setItem('token', response.token);
      localStorage.setItem('user', JSON.stringify(response.user));
      return response;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la connexion');
    }
  }
);

export const getCurrentUser = createAsyncThunk<
  User,
  void,
  { rejectValue: string }
>(
  'auth/getCurrentUser',
  async (_, { rejectWithValue }) => {
    try {
      const user = await authService.getCurrentUser();
      return user;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la récupération de l\'utilisateur');
    }
  }
);

export const logoutAsync = createAsyncThunk<
  { message: string },
  void,
  { rejectValue: string }
>(
  'auth/logout',
  async (_, { rejectWithValue }) => {
    try {
      const response = await authService.logout();
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      return response;
    } catch (error: any) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la déconnexion');
    }
  }
);

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(register.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(register.fulfilled, (state) => {
        state.loading = false;
        state.error = null;
      })
      .addCase(register.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || 'Erreur lors de l\'inscription';
      })
      .addCase(login.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(login.fulfilled, (state, action) => {
        state.loading = false;
        state.user = action.payload.user;
        state.token = action.payload.token;
        state.isAuthenticated = true;
      })
      .addCase(login.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || 'Erreur lors de la connexion';
      })
      .addCase(getCurrentUser.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(getCurrentUser.fulfilled, (state, action) => {
        state.loading = false;
        state.user = action.payload;
        state.isAuthenticated = true;
        localStorage.setItem('user', JSON.stringify(action.payload));
      })
      .addCase(getCurrentUser.rejected, (state, action) => {
        state.loading = false;
        state.user = null;
        state.token = null;
        state.isAuthenticated = false;
        state.error = action.payload || 'Erreur lors de la récupération de l\'utilisateur';
        localStorage.removeItem('token');
        localStorage.removeItem('user');
      })
      .addCase(logoutAsync.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(logoutAsync.fulfilled, (state) => {
        state.loading = false;
        state.user = null;
        state.token = null;
        state.isAuthenticated = false;
        state.error = null;
      })
      .addCase(logoutAsync.rejected, (state, action) => {
        state.loading = false;
        state.user = null;
        state.token = null;
        state.isAuthenticated = false;
        state.error = action.payload || 'Erreur lors de la déconnexion';
      });
  },
});

export const { clearError } = authSlice.actions;
export default authSlice.reducer;

