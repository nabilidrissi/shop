package com.carrefour.carrefourShop.service.impl;

import com.carrefour.carrefourShop.dto.AddToCartRequest;
import com.carrefour.carrefourShop.dto.CartDto;
import com.carrefour.carrefourShop.entity.Cart;
import com.carrefour.carrefourShop.entity.CartItem;
import com.carrefour.carrefourShop.entity.Product;
import com.carrefour.carrefourShop.exception.ResourceNotFoundException;
import com.carrefour.carrefourShop.mapper.CartMapper;
import com.carrefour.carrefourShop.repository.CartItemRepository;
import com.carrefour.carrefourShop.repository.CartRepository;
import com.carrefour.carrefourShop.repository.ProductRepository;
import com.carrefour.carrefourShop.repository.UserRepository;
import com.carrefour.carrefourShop.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;

    @Override
    @Transactional
    public CartDto getCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .map(cartMapper::toDto)
                .orElse(CartDto.builder()
                        .items(Collections.emptyList())
                        .totalPrice(BigDecimal.ZERO)
                        .build());
    }

    @Override
    @Transactional
    public CartDto addItemToCart(Long userId, AddToCartRequest request) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> createCartForUser(userId));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (!product.getActive()) {
            throw new IllegalArgumentException("Product is not available");
        }

        if (product.getStock() != null && product.getStock() < request.getQuantity()) {
            throw new IllegalArgumentException("Insufficient stock");
        }

        CartItem existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), request.getProductId())
                .orElse(null);

        if (existingItem != null) {
            int newQuantity = existingItem.getQuantity() + request.getQuantity();
            if (product.getStock() != null && product.getStock() < newQuantity) {
                throw new IllegalArgumentException("Insufficient stock");
            }
            existingItem.setQuantity(newQuantity);
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();
            cartItemRepository.save(newItem);
        }

        cart = cartRepository.findById(cart.getId()).orElse(cart);
        return cartMapper.toDto(cart);
    }

    @Override
    @Transactional
    public CartDto updateCartItem(Long userId, Long itemId, Integer quantity) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new IllegalArgumentException("Cart item does not belong to user's cart");
        }

        if (quantity <= 0) {
            cartItemRepository.delete(item);
        } else {
            Product product = item.getProduct();
            if (product.getStock() != null && product.getStock() < quantity) {
                throw new IllegalArgumentException("Insufficient stock");
            }
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }

        cart = cartRepository.findById(cart.getId()).orElse(cart);
        return cartMapper.toDto(cart);
    }

    @Override
    @Transactional
    public CartDto removeItemFromCart(Long userId, Long itemId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new IllegalArgumentException("Cart item does not belong to user's cart");
        }

        cartItemRepository.delete(item);
        cart = cartRepository.findById(cart.getId()).orElse(cart);
        return cartMapper.toDto(cart);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        cartItemRepository.deleteByCartId(cart.getId());
    }

    private Cart createCartForUser(Long userId) {
        com.carrefour.carrefourShop.entity.User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return cartRepository.save(Cart.builder()
                .user(user)
                .items(new ArrayList<>())
                .build());
    }
}

