package com.carrefour.carrefourShop.service.impl;

import com.carrefour.carrefourShop.dto.AddToCartRequest;
import com.carrefour.carrefourShop.dto.CartDto;
import com.carrefour.carrefourShop.dto.CartItemDto;
import com.carrefour.carrefourShop.dto.ProductDto;
import com.carrefour.carrefourShop.entity.Cart;
import com.carrefour.carrefourShop.entity.CartItem;
import com.carrefour.carrefourShop.entity.Product;
import com.carrefour.carrefourShop.entity.User;
import com.carrefour.carrefourShop.exception.BusinessException;
import com.carrefour.carrefourShop.exception.ExceptionConstants;
import com.carrefour.carrefourShop.exception.ResourceNotFoundException;
import com.carrefour.carrefourShop.mapper.CartMapper;
import com.carrefour.carrefourShop.repository.CartItemRepository;
import com.carrefour.carrefourShop.repository.CartRepository;
import com.carrefour.carrefourShop.repository.ProductRepository;
import com.carrefour.carrefourShop.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartMapper cartMapper;

    @InjectMocks
    private CartServiceImpl cartService;

    private User user;
    private Cart cart;
    private Product product;
    private CartItem cartItem;
    private AddToCartRequest addToCartRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .role(User.Role.USER)
                .build();

        product = Product.builder()
                .id(1L)
                .name("Test Product")
                .description("Test Description")
                .price(new BigDecimal("10.00"))
                .stock(100)
                .active(true)
                .build();

        cart = Cart.builder()
                .id(1L)
                .user(user)
                .items(new ArrayList<>())
                .build();

        cartItem = CartItem.builder()
                .id(1L)
                .cart(cart)
                .product(product)
                .quantity(2)
                .build();

        addToCartRequest = new AddToCartRequest(1L, 3);
    }

    @Test
    void getCart_WhenCartExists_ShouldReturnCartDto() {
        CartDto expectedDto = CartDto.builder()
                .id(1L)
                .items(Collections.emptyList())
                .totalPrice(BigDecimal.ZERO)
                .build();

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(cartMapper.toDto(cart)).thenReturn(expectedDto);

        CartDto result = cartService.getCart(1L);

        assertNotNull(result);
        assertEquals(expectedDto.getId(), result.getId());
        verify(cartRepository).findByUserId(1L);
        verify(cartMapper).toDto(cart);
    }

    @Test
    void getCart_WhenCartDoesNotExist_ShouldReturnEmptyCartDto() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());

        CartDto result = cartService.getCart(1L);

        assertNotNull(result);
        assertNull(result.getId());
        assertEquals(Collections.emptyList(), result.getItems());
        assertEquals(BigDecimal.ZERO, result.getTotalPrice());
        verify(cartRepository).findByUserId(1L);
        verify(cartMapper, never()).toDto(any());
    }

    @Test
    void addItemToCart_WhenCartDoesNotExist_ShouldCreateCartAndAddItem() {
        when(cartRepository.findByUserId(1L))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(cart));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByCartIdAndProductId(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(cartItem);

        CartDto expectedDto = CartDto.builder()
                .id(1L)
                .items(Collections.emptyList())
                .totalPrice(BigDecimal.ZERO)
                .build();
        when(cartMapper.toDto(cart)).thenReturn(expectedDto);

        CartDto result = cartService.addItemToCart(1L, addToCartRequest);

        assertNotNull(result);
        verify(cartRepository, times(2)).findByUserId(1L);
        verify(userRepository).findById(1L);
        verify(cartRepository).save(any(Cart.class));
        verify(productRepository).findById(1L);
        verify(cartItemRepository).save(any(CartItem.class));
        verify(cartRepository).flush();
    }

    @Test
    void addItemToCart_WhenProductNotFound_ShouldThrowResourceNotFoundException() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> cartService.addItemToCart(1L, addToCartRequest));

        assertEquals(ExceptionConstants.PRODUCT_NOT_FOUND, exception.getCode());
        verify(productRepository).findById(1L);
        verify(cartItemRepository, never()).save(any());
    }

    @Test
    void addItemToCart_WhenProductNotActive_ShouldThrowBusinessException() {
        product.setActive(false);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> cartService.addItemToCart(1L, addToCartRequest));

        assertEquals(ExceptionConstants.PRODUCT_NOT_AVAILABLE, exception.getCode());
        verify(productRepository).findById(1L);
        verify(cartItemRepository, never()).save(any());
    }

    @Test
    void addItemToCart_WhenInsufficientStock_ShouldThrowBusinessException() {
        product.setStock(2);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> cartService.addItemToCart(1L, addToCartRequest));

        assertEquals(ExceptionConstants.INSUFFICIENT_STOCK, exception.getCode());
        verify(productRepository).findById(1L);
        verify(cartItemRepository, never()).save(any());
    }

    @Test
    void addItemToCart_WhenItemExists_ShouldUpdateQuantity() {
        when(cartRepository.findByUserId(1L))
                .thenReturn(Optional.of(cart))
                .thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByCartIdAndProductId(1L, 1L)).thenReturn(Optional.of(cartItem));
        when(cartItemRepository.save(cartItem)).thenReturn(cartItem);

        CartDto expectedDto = CartDto.builder()
                .id(1L)
                .items(Collections.emptyList())
                .totalPrice(BigDecimal.ZERO)
                .build();
        when(cartMapper.toDto(cart)).thenReturn(expectedDto);

        CartDto result = cartService.addItemToCart(1L, addToCartRequest);

        assertNotNull(result);
        assertEquals(5, cartItem.getQuantity());
        verify(cartItemRepository).save(cartItem);
        verify(cartRepository, times(2)).findByUserId(1L);
        verify(cartRepository).flush();
    }

    @Test
    void addItemToCart_WhenItemExistsAndInsufficientStock_ShouldThrowBusinessException() {
        product.setStock(3);
        cartItem.setQuantity(2);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByCartIdAndProductId(1L, 1L)).thenReturn(Optional.of(cartItem));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> cartService.addItemToCart(1L, addToCartRequest));

        assertEquals(ExceptionConstants.INSUFFICIENT_STOCK, exception.getCode());
        verify(cartItemRepository, never()).save(any());
    }

    @Test
    void addItemToCart_WhenStockIsNull_ShouldAllowAddingItem() {
        product.setStock(null);
        when(cartRepository.findByUserId(1L))
                .thenReturn(Optional.of(cart))
                .thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByCartIdAndProductId(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(cartItem);

        CartDto expectedDto = CartDto.builder()
                .id(1L)
                .items(Collections.emptyList())
                .totalPrice(BigDecimal.ZERO)
                .build();
        when(cartMapper.toDto(cart)).thenReturn(expectedDto);

        CartDto result = cartService.addItemToCart(1L, addToCartRequest);

        assertNotNull(result);
        verify(cartItemRepository).save(any(CartItem.class));
        verify(cartRepository, times(2)).findByUserId(1L);
        verify(cartRepository).flush();
    }

    @Test
    void updateCartItem_WhenCartNotFound_ShouldThrowResourceNotFoundException() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> cartService.updateCartItem(1L, 1L, 5));

        assertEquals(ExceptionConstants.CART_NOT_FOUND, exception.getCode());
        verify(cartRepository).findByUserId(1L);
        verify(cartItemRepository, never()).findById(anyLong());
    }

    @Test
    void updateCartItem_WhenItemNotFound_ShouldThrowResourceNotFoundException() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> cartService.updateCartItem(1L, 1L, 5));

        assertEquals(ExceptionConstants.CART_ITEM_NOT_FOUND, exception.getCode());
        verify(cartItemRepository).findById(1L);
    }

    @Test
    void updateCartItem_WhenItemDoesNotBelongToCart_ShouldThrowBusinessException() {
        Cart otherCart = Cart.builder().id(2L).user(user).items(new ArrayList<>()).build();
        CartItem otherItem = CartItem.builder().id(1L).cart(otherCart).product(product).quantity(2).build();

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(otherItem));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> cartService.updateCartItem(1L, 1L, 5));

        assertEquals(ExceptionConstants.CART_ITEM_DOES_NOT_BELONG, exception.getCode());
        verify(cartItemRepository, never()).save(any());
    }

    @Test
    void updateCartItem_WhenQuantityIsZero_ShouldDeleteItem() {
        when(cartRepository.findByUserId(1L))
                .thenReturn(Optional.of(cart))
                .thenReturn(Optional.of(cart));
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(cartItem));

        CartDto expectedDto = CartDto.builder()
                .id(1L)
                .items(Collections.emptyList())
                .totalPrice(BigDecimal.ZERO)
                .build();
        when(cartMapper.toDto(cart)).thenReturn(expectedDto);

        CartDto result = cartService.updateCartItem(1L, 1L, 0);

        assertNotNull(result);
        verify(cartItemRepository).delete(cartItem);
        verify(cartItemRepository, never()).save(any());
        verify(cartRepository, times(2)).findByUserId(1L);
        verify(cartRepository).flush();
    }

    @Test
    void updateCartItem_WhenQuantityIsNegative_ShouldDeleteItem() {
        when(cartRepository.findByUserId(1L))
                .thenReturn(Optional.of(cart))
                .thenReturn(Optional.of(cart));
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(cartItem));

        CartDto expectedDto = CartDto.builder()
                .id(1L)
                .items(Collections.emptyList())
                .totalPrice(BigDecimal.ZERO)
                .build();
        when(cartMapper.toDto(cart)).thenReturn(expectedDto);

        CartDto result = cartService.updateCartItem(1L, 1L, -1);

        assertNotNull(result);
        verify(cartItemRepository).delete(cartItem);
        verify(cartItemRepository, never()).save(any());
        verify(cartRepository, times(2)).findByUserId(1L);
        verify(cartRepository).flush();
    }

    @Test
    void updateCartItem_WhenValidQuantity_ShouldUpdateItem() {
        when(cartRepository.findByUserId(1L))
                .thenReturn(Optional.of(cart))
                .thenReturn(Optional.of(cart));
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(cartItem));
        when(cartItemRepository.save(cartItem)).thenReturn(cartItem);

        CartDto expectedDto = CartDto.builder()
                .id(1L)
                .items(Collections.emptyList())
                .totalPrice(BigDecimal.ZERO)
                .build();
        when(cartMapper.toDto(cart)).thenReturn(expectedDto);

        CartDto result = cartService.updateCartItem(1L, 1L, 5);

        assertNotNull(result);
        assertEquals(5, cartItem.getQuantity());
        verify(cartItemRepository).save(cartItem);
        verify(cartItemRepository, never()).delete(any());
        verify(cartRepository, times(2)).findByUserId(1L);
        verify(cartRepository).flush();
    }

    @Test
    void updateCartItem_WhenInsufficientStock_ShouldThrowBusinessException() {
        product.setStock(3);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(cartItem));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> cartService.updateCartItem(1L, 1L, 5));

        assertEquals(ExceptionConstants.INSUFFICIENT_STOCK, exception.getCode());
        verify(cartItemRepository, never()).save(any());
    }

    @Test
    void removeItemFromCart_WhenCartNotFound_ShouldThrowResourceNotFoundException() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> cartService.removeItemFromCart(1L, 1L));

        assertEquals(ExceptionConstants.CART_NOT_FOUND, exception.getCode());
        verify(cartRepository).findByUserId(1L);
        verify(cartItemRepository, never()).findById(anyLong());
    }

    @Test
    void removeItemFromCart_WhenItemNotFound_ShouldThrowResourceNotFoundException() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> cartService.removeItemFromCart(1L, 1L));

        assertEquals(ExceptionConstants.CART_ITEM_NOT_FOUND, exception.getCode());
        verify(cartItemRepository, never()).findById(anyLong());
    }

    @Test
    void removeItemFromCart_WhenItemDoesNotBelongToCart_ShouldThrowResourceNotFoundException() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> cartService.removeItemFromCart(1L, 1L));

        assertEquals(ExceptionConstants.CART_ITEM_NOT_FOUND, exception.getCode());
        verify(cartItemRepository, never()).delete(any());
    }

    @Test
    void removeItemFromCart_WhenValid_ShouldDeleteItem() {
        cart.getItems().add(cartItem);
        Cart cartAfterRemoval = Cart.builder()
                .id(1L)
                .user(user)
                .items(new ArrayList<>())
                .build();

        when(cartRepository.findByUserId(1L))
                .thenReturn(Optional.of(cart))
                .thenReturn(Optional.of(cartAfterRemoval));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        CartDto expectedDto = CartDto.builder()
                .id(1L)
                .items(Collections.emptyList())
                .totalPrice(BigDecimal.ZERO)
                .build();
        when(cartMapper.toDto(cartAfterRemoval)).thenReturn(expectedDto);

        CartDto result = cartService.removeItemFromCart(1L, 1L);

        assertNotNull(result);
        verify(cartRepository).save(cart);
        verify(cartItemRepository, never()).delete(any());
        verify(cartRepository, times(2)).findByUserId(1L);
    }

    @Test
    void clearCart_WhenCartNotFound_ShouldThrowResourceNotFoundException() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> cartService.clearCart(1L));

        assertEquals(ExceptionConstants.CART_NOT_FOUND, exception.getCode());
        verify(cartRepository).findByUserId(1L);
        verify(cartItemRepository, never()).deleteByCartId(anyLong());
    }

    @Test
    void clearCart_WhenValid_ShouldDeleteAllItems() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        cartService.clearCart(1L);

        verify(cartRepository).findByUserId(1L);
        verify(cartItemRepository).deleteByCartId(1L);
    }

    @Test
    void addItemToCart_WhenUserNotFound_ShouldThrowResourceNotFoundException() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> cartService.addItemToCart(1L, addToCartRequest));

        assertEquals(ExceptionConstants.USER_NOT_FOUND, exception.getCode());
        verify(userRepository).findById(1L);
        verify(cartRepository, never()).save(any());
    }
}
