package com.carrefour.carrefourShop.exception;

public class UnauthorizedException extends RuntimeException {
    private final String code;
    private final String message;

    public UnauthorizedException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

