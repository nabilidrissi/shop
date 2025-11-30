package com.carrefour.carrefourShop.service;

import com.carrefour.carrefourShop.dto.AddToCartRequest;
import com.carrefour.carrefourShop.dto.CartDto;

public interface CartService {
    CartDto getCart(Long userId);
    CartDto addItemToCart(Long userId, AddToCartRequest request);
    CartDto updateCartItem(Long userId, Long itemId, Integer quantity);
    CartDto removeItemFromCart(Long userId, Long itemId);
    void clearCart(Long userId);
}

