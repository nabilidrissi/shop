import { configureStore } from '@reduxjs/toolkit';
import authReducer from '../slices/authSlice';
import { apiSlice } from '../../services/apiSlice';
import { toastMiddleware } from '../middleware/toastMiddleware';

export const store = configureStore({
  reducer: {
    auth: authReducer,
    [apiSlice.reducerPath]: apiSlice.reducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: {
        ignoredActions: ['persist/PERSIST'],
      },
    })
      .concat(apiSlice.middleware)
      .concat(toastMiddleware),
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;

