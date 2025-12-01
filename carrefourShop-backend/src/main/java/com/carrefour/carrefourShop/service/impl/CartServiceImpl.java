package com.carrefour.carrefourShop.service.impl;

import com.carrefour.carrefourShop.dto.AddToCartRequest;
import com.carrefour.carrefourShop.dto.CartDto;
import com.carrefour.carrefourShop.entity.Cart;
import com.carrefour.carrefourShop.entity.CartItem;
import com.carrefour.carrefourShop.entity.Product;
import com.carrefour.carrefourShop.exception.BusinessException;
import com.carrefour.carrefourShop.exception.ExceptionConstants;
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
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionConstants.PRODUCT_NOT_FOUND, ExceptionConstants.getMessage(ExceptionConstants.PRODUCT_NOT_FOUND)));

        if (!product.getActive()) {
            throw new BusinessException(ExceptionConstants.PRODUCT_NOT_AVAILABLE, ExceptionConstants.getMessage(ExceptionConstants.PRODUCT_NOT_AVAILABLE));
        }

        if (product.getStock() != null && product.getStock() < request.getQuantity()) {
            throw new BusinessException(ExceptionConstants.INSUFFICIENT_STOCK, ExceptionConstants.getMessage(ExceptionConstants.INSUFFICIENT_STOCK));
        }

        CartItem existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), request.getProductId())
                .orElse(null);

        if (existingItem != null) {
            int newQuantity = existingItem.getQuantity() + request.getQuantity();
            if (product.getStock() != null && product.getStock() < newQuantity) {
                throw new BusinessException(ExceptionConstants.INSUFFICIENT_STOCK, ExceptionConstants.getMessage(ExceptionConstants.INSUFFICIENT_STOCK));
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
            cart.getItems().add(newItem);
        }

        cartRepository.flush();
        cart = cartRepository.findByUserId(userId).orElse(cart);
        return cartMapper.toDto(cart);
    }

    @Override
    @Transactional
    public CartDto updateCartItem(Long userId, Long itemId, Integer quantity) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionConstants.CART_NOT_FOUND, ExceptionConstants.getMessage(ExceptionConstants.CART_NOT_FOUND)));

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionConstants.CART_ITEM_NOT_FOUND, ExceptionConstants.getMessage(ExceptionConstants.CART_ITEM_NOT_FOUND)));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new BusinessException(ExceptionConstants.CART_ITEM_DOES_NOT_BELONG, ExceptionConstants.getMessage(ExceptionConstants.CART_ITEM_DOES_NOT_BELONG));
        }

        if (quantity <= 0) {
            cartItemRepository.delete(item);
        } else {
            Product product = item.getProduct();
            if (product.getStock() != null && product.getStock() < quantity) {
                throw new BusinessException(ExceptionConstants.INSUFFICIENT_STOCK, ExceptionConstants.getMessage(ExceptionConstants.INSUFFICIENT_STOCK));
            }
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }

        cartRepository.flush();
        cart = cartRepository.findByUserId(userId).orElse(cart);
        return cartMapper.toDto(cart);
    }

    @Override
    @Transactional
    public CartDto removeItemFromCart(Long userId, Long itemId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionConstants.CART_NOT_FOUND, ExceptionConstants.getMessage(ExceptionConstants.CART_NOT_FOUND)));

        CartItem item = cart.getItems().stream()
                .filter(cartItem -> cartItem.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionConstants.CART_ITEM_NOT_FOUND, ExceptionConstants.getMessage(ExceptionConstants.CART_ITEM_NOT_FOUND)));

        cart.getItems().remove(item);
        cartRepository.save(cart);
        cart = cartRepository.findByUserId(userId).orElse(cart);
        return cartMapper.toDto(cart);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionConstants.CART_NOT_FOUND, ExceptionConstants.getMessage(ExceptionConstants.CART_NOT_FOUND)));
        cartItemRepository.deleteByCartId(cart.getId());
    }

    private Cart createCartForUser(Long userId) {
        com.carrefour.carrefourShop.entity.User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionConstants.USER_NOT_FOUND, ExceptionConstants.getMessage(ExceptionConstants.USER_NOT_FOUND)));
        return cartRepository.save(Cart.builder()
                .user(user)
                .items(new ArrayList<>())
                .build());
    }
}

