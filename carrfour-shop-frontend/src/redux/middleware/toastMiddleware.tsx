import type { Middleware } from '@reduxjs/toolkit';
import { toast } from 'react-toastify';
import type { ApiError } from '../../types';


export const toastMiddleware: Middleware = () => (next) => (action: any) => {
  // Vérifie si l'action est rejetée et contient une erreur API
  if (action.type?.endsWith('/rejected') && action.payload) {
    const error = action.payload as ApiError | null;
    if (error && error.code && error.message) {
      // Affiche le toast avec le code et le message
      toast.error(
        <div>
          <div className="font-semibold">{error.message}</div>
          <div className="text-sm text-gray-600 mt-1">Code: {error.code}</div>
        </div>,
        {
          position: 'top-right',
          autoClose: 5000,
          hideProgressBar: false,
          closeOnClick: true,
          pauseOnHover: true,
          draggable: true,
        }
      );
    }
  }
  
  return next(action);
};

