package com.carrefour.carrefourShop.util;

import com.carrefour.carrefourShop.exception.BusinessException;
import com.carrefour.carrefourShop.exception.ExceptionConstants;
import org.springframework.security.core.Authentication;

public class TokenUtil {

    public static Long getUserId(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new BusinessException(ExceptionConstants.AUTHENTICATION_NULL_OR_USER_ID_MISSING, ExceptionConstants.getMessage(ExceptionConstants.AUTHENTICATION_NULL_OR_USER_ID_MISSING));
        }
        try {
            return Long.parseLong(authentication.getName());
        } catch (NumberFormatException e) {
            throw new BusinessException(ExceptionConstants.INVALID_USER_ID_FORMAT, ExceptionConstants.getMessage(ExceptionConstants.INVALID_USER_ID_FORMAT));
        }
    }

    public static String getUsername(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new BusinessException(ExceptionConstants.AUTHENTICATION_NULL_OR_USERNAME_MISSING, ExceptionConstants.getMessage(ExceptionConstants.AUTHENTICATION_NULL_OR_USERNAME_MISSING));
        }
        return authentication.getName();
    }
}
