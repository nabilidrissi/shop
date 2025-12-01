package com.carrefour.carrefourShop.exception;

import java.util.HashMap;
import java.util.Map;

public class ExceptionConstants {

    public static final String PRODUCT_NOT_FOUND = "PRODUCT_NOT_FOUND";
    public static final String PRODUCT_NOT_FOUND_BY_ID = "PRODUCT_NOT_FOUND_BY_ID";
    public static final String PRODUCT_NOT_AVAILABLE = "PRODUCT_NOT_AVAILABLE";
    public static final String INSUFFICIENT_STOCK = "INSUFFICIENT_STOCK";
    public static final String INSUFFICIENT_STOCK_FOR_PRODUCT = "INSUFFICIENT_STOCK_FOR_PRODUCT";
    public static final String PRODUCT_NO_LONGER_AVAILABLE = "PRODUCT_NO_LONGER_AVAILABLE";

    public static final String USER_NOT_FOUND = "USER_NOT_FOUND";
    public static final String USER_NOT_FOUND_BY_EMAIL = "USER_NOT_FOUND_BY_EMAIL";
    public static final String EMAIL_ALREADY_EXISTS = "EMAIL_ALREADY_EXISTS";
    public static final String INVALID_EMAIL_OR_PASSWORD = "INVALID_EMAIL_OR_PASSWORD";

    public static final String CART_NOT_FOUND = "CART_NOT_FOUND";
    public static final String CART_IS_EMPTY = "CART_IS_EMPTY";
    public static final String CART_ITEM_NOT_FOUND = "CART_ITEM_NOT_FOUND";
    public static final String CART_ITEM_DOES_NOT_BELONG = "CART_ITEM_DOES_NOT_BELONG";
    public static final String CANNOT_CREATE_ORDER_WITH_EMPTY_CART = "CANNOT_CREATE_ORDER_WITH_EMPTY_CART";

    public static final String ORDER_NOT_FOUND = "ORDER_NOT_FOUND";
    public static final String ORDER_DOES_NOT_BELONG_TO_USER = "ORDER_DOES_NOT_BELONG_TO_USER";
    public static final String INVALID_ORDER_STATUS = "INVALID_ORDER_STATUS";

    public static final String AUTHENTICATION_NULL_OR_USER_ID_MISSING = "AUTHENTICATION_NULL_OR_USER_ID_MISSING";
    public static final String INVALID_USER_ID_FORMAT = "INVALID_USER_ID_FORMAT";
    public static final String AUTHENTICATION_NULL_OR_USERNAME_MISSING = "AUTHENTICATION_NULL_OR_USERNAME_MISSING";

    public static final String UNEXPECTED_ERROR = "UNEXPECTED_ERROR";

    private static final Map<String, String> MESSAGES = new HashMap<>();

    static {
        MESSAGES.put(PRODUCT_NOT_FOUND, "Product not found");
        MESSAGES.put(PRODUCT_NOT_FOUND_BY_ID, "Product not found with id: %s");
        MESSAGES.put(PRODUCT_NOT_AVAILABLE, "Product is not available");
        MESSAGES.put(INSUFFICIENT_STOCK, "Insufficient stock");
        MESSAGES.put(INSUFFICIENT_STOCK_FOR_PRODUCT, "Insufficient stock for product %s");
        MESSAGES.put(PRODUCT_NO_LONGER_AVAILABLE, "Product %s is no longer available");

        MESSAGES.put(USER_NOT_FOUND, "User not found");
        MESSAGES.put(USER_NOT_FOUND_BY_EMAIL, "User not found: %s");
        MESSAGES.put(EMAIL_ALREADY_EXISTS, "Email already exists");
        MESSAGES.put(INVALID_EMAIL_OR_PASSWORD, "Invalid email or password");

        MESSAGES.put(CART_NOT_FOUND, "Cart not found");
        MESSAGES.put(CART_IS_EMPTY, "Cart is empty");
        MESSAGES.put(CART_ITEM_NOT_FOUND, "Cart item not found");
        MESSAGES.put(CART_ITEM_DOES_NOT_BELONG, "Cart item does not belong to user's cart");
        MESSAGES.put(CANNOT_CREATE_ORDER_WITH_EMPTY_CART, "Cannot create order with empty cart");

        MESSAGES.put(ORDER_NOT_FOUND, "Order not found");
        MESSAGES.put(ORDER_DOES_NOT_BELONG_TO_USER, "Order does not belong to user");
        MESSAGES.put(INVALID_ORDER_STATUS, "Invalid order status: %s");

        MESSAGES.put(AUTHENTICATION_NULL_OR_USER_ID_MISSING, "Authentication is null or user ID is missing");
        MESSAGES.put(INVALID_USER_ID_FORMAT, "Invalid user ID format in authentication");
        MESSAGES.put(AUTHENTICATION_NULL_OR_USERNAME_MISSING, "Authentication is null or username is missing");

        MESSAGES.put(UNEXPECTED_ERROR, "An unexpected error occurred");
    }

    public static String getMessage(String code, Object... args) {
        String message = MESSAGES.get(code);
        if (message == null) {
            return "Unknown error";
        }
        if (args.length > 0) {
            return String.format(message, args);
        }
        return message;
    }
}

