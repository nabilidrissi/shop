package com.carrefour.carrefourShop.service.impl;

import com.carrefour.carrefourShop.dto.CreateOrderRequest;
import com.carrefour.carrefourShop.dto.OrderDto;
import com.carrefour.carrefourShop.entity.*;
import com.carrefour.carrefourShop.exception.BusinessException;
import com.carrefour.carrefourShop.exception.ExceptionConstants;
import com.carrefour.carrefourShop.exception.ResourceNotFoundException;
import com.carrefour.carrefourShop.mapper.OrderMapper;
import com.carrefour.carrefourShop.repository.*;
import com.carrefour.carrefourShop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderDto createOrder(Long userId, CreateOrderRequest request) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionConstants.CART_IS_EMPTY, ExceptionConstants.getMessage(ExceptionConstants.CART_IS_EMPTY)));

        if (cart.getItems().isEmpty()) {
            throw new BusinessException(ExceptionConstants.CANNOT_CREATE_ORDER_WITH_EMPTY_CART, ExceptionConstants.getMessage(ExceptionConstants.CANNOT_CREATE_ORDER_WITH_EMPTY_CART));
        }

        User user = User.builder().id(userId).build();
        Order order = Order.builder()
                .user(user)
                .status(Order.OrderStatus.PENDING)
                .shippingAddress(request.getShippingAddress())
                .billingAddress(request.getBillingAddress() != null ? request.getBillingAddress() : request.getShippingAddress())
                .phone(request.getPhone())
                .email(request.getEmail())
                .items(new ArrayList<>())
                .build();

        BigDecimal totalPrice = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();

            if (!product.getActive()) {
                throw new BusinessException(ExceptionConstants.PRODUCT_NO_LONGER_AVAILABLE, ExceptionConstants.getMessage(ExceptionConstants.PRODUCT_NO_LONGER_AVAILABLE, product.getName()));
            }

            if (product.getStock() != null && product.getStock() < cartItem.getQuantity()) {
                throw new BusinessException(ExceptionConstants.INSUFFICIENT_STOCK_FOR_PRODUCT, ExceptionConstants.getMessage(ExceptionConstants.INSUFFICIENT_STOCK_FOR_PRODUCT, product.getName()));
            }

            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .unitPrice(product.getPrice())
                    .totalPrice(itemTotal)
                    .build();

            order.getItems().add(orderItem);
            totalPrice = totalPrice.add(itemTotal);

            if (product.getStock() != null) {
                product.setStock(product.getStock() - cartItem.getQuantity());
                productRepository.save(product);
            }
        }

        order.setTotalPrice(totalPrice);
        order = orderRepository.save(order);

        cartItemRepository.deleteByCartId(cart.getId());
        
        cart = cartRepository.findById(cart.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionConstants.CART_NOT_FOUND, ExceptionConstants.getMessage(ExceptionConstants.CART_NOT_FOUND)));
        
        cart.getItems().clear();
        
        User userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionConstants.USER_NOT_FOUND, ExceptionConstants.getMessage(ExceptionConstants.USER_NOT_FOUND)));
        userEntity.setCart(null);
        userRepository.save(userEntity);
        
        cartRepository.delete(cart);

        return orderMapper.toDto(order);
    }

    @Override
    public List<OrderDto> getUserOrders(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrderDto getOrderById(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionConstants.ORDER_NOT_FOUND, ExceptionConstants.getMessage(ExceptionConstants.ORDER_NOT_FOUND)));

        if (!order.getUser().getId().equals(userId)) {
            throw new BusinessException(ExceptionConstants.ORDER_DOES_NOT_BELONG_TO_USER, ExceptionConstants.getMessage(ExceptionConstants.ORDER_DOES_NOT_BELONG_TO_USER));
        }

        return orderMapper.toDto(order);
    }

    @Override
    @Transactional
    public OrderDto updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionConstants.ORDER_NOT_FOUND, ExceptionConstants.getMessage(ExceptionConstants.ORDER_NOT_FOUND)));

        try {
            order.setStatus(Order.OrderStatus.valueOf(status.toUpperCase()));
            order = orderRepository.save(order);
            return orderMapper.toDto(order);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ExceptionConstants.INVALID_ORDER_STATUS, ExceptionConstants.getMessage(ExceptionConstants.INVALID_ORDER_STATUS, status));
        }
    }
}

