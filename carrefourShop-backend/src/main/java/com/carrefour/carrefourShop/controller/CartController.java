package com.carrefour.carrefourShop.controller;

import com.carrefour.carrefourShop.dto.AddToCartRequest;
import com.carrefour.carrefourShop.dto.CartDto;
import com.carrefour.carrefourShop.service.CartService;
import com.carrefour.carrefourShop.util.TokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "Shopping cart management APIs")
public class CartController {

    private final CartService cartService;

    @GetMapping
    @Operation(summary = "Get user's cart", description = "Returns the current user's shopping cart")
    public ResponseEntity<CartDto> getCart(Authentication authentication) {
        Long userId = TokenUtil.getUserId(authentication);
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @PostMapping("/items")
    @Operation(summary = "Add item to cart", description = "Adds a product to the user's shopping cart")
    public ResponseEntity<CartDto> addItemToCart(
            Authentication authentication,
            @Valid @RequestBody AddToCartRequest request) {
        Long userId = TokenUtil.getUserId(authentication);
        return ResponseEntity.ok(cartService.addItemToCart(userId, request));
    }

    @PutMapping("/items/{itemId}")
    @Operation(summary = "Update cart item quantity", description = "Updates the quantity of an item in the cart")
    public ResponseEntity<CartDto> updateCartItem(
            Authentication authentication,
            @PathVariable Long itemId,
            @RequestParam Integer quantity) {
        Long userId = TokenUtil.getUserId(authentication);
        return ResponseEntity.ok(cartService.updateCartItem(userId, itemId, quantity));
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Remove item from cart", description = "Removes an item from the shopping cart")
    public ResponseEntity<CartDto> removeItemFromCart(
            Authentication authentication,
            @PathVariable Long itemId) {
        Long userId = TokenUtil.getUserId(authentication);
        return ResponseEntity.ok(cartService.removeItemFromCart(userId, itemId));
    }

    @DeleteMapping
    @Operation(summary = "Clear cart", description = "Removes all items from the shopping cart")
    public ResponseEntity<Void> clearCart(Authentication authentication) {
        Long userId = TokenUtil.getUserId(authentication);
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}

