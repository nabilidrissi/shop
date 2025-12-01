import { toast } from 'react-toastify';
import type { ApiError } from '../types';

export const extractApiError = (error: any): ApiError | null => {
  if (error?.response?.data) {
    const errorData = error.response.data;
    if (errorData.code && errorData.message && errorData.status) {
      return {
        code: errorData.code,
        message: errorData.message,
        status: errorData.status,
      };
    }
    if (errorData.message) {
      return {
        code: 'UNKNOWN_ERROR',
        message: errorData.message,
        status: error.response.status?.toString() || 'ERROR',
      };
    }
  }
  if (error?.message) {
    return {
      code: 'NETWORK_ERROR',
      message: error.message,
      status: 'NETWORK_ERROR',
    };
  }
  return null;
};


export const showApiError = (error: any, defaultMessage: string = 'Une erreur est survenue') => {
  const apiError = extractApiError(error);
  
  if (apiError) {
    toast.error(
      <div>
        <div className="font-semibold">{apiError.message}</div>
        <div className="text-sm text-gray-600 mt-1">Code: {apiError.code}</div>
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
    return apiError;
  } else {
    toast.error(defaultMessage, {
      position: 'top-right',
      autoClose: 5000,
      hideProgressBar: false,
      closeOnClick: true,
      pauseOnHover: true,
      draggable: true,
    });
    return null;
  }
};


export const showSuccess = (message: string) => {
  toast.success(message, {
    position: 'top-right',
    autoClose: 3000,
    hideProgressBar: false,
    closeOnClick: true,
    pauseOnHover: true,
    draggable: true,
  });
};

export const showInfo = (message: string) => {
  toast.info(message, {
    position: 'top-right',
    autoClose: 3000,
    hideProgressBar: false,
    closeOnClick: true,
    pauseOnHover: true,
    draggable: true,
  });
};

