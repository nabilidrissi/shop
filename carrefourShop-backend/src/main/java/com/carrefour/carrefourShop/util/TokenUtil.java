package com.carrefour.carrefourShop.util;

import org.springframework.security.core.Authentication;

public class TokenUtil {

    public static Long getUserId(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new IllegalArgumentException("Authentication is null or user ID is missing");
        }
        try {
            return Long.parseLong(authentication.getName());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid user ID format in authentication", e);
        }
    }

    public static String getUsername(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new IllegalArgumentException("Authentication is null or username is missing");
        }
        return authentication.getName();
    }
}
