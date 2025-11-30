package com.carrefour.carrefourShop.service;

import com.carrefour.carrefourShop.dto.CreateOrderRequest;
import com.carrefour.carrefourShop.dto.OrderDto;

import java.util.List;

public interface OrderService {
    OrderDto createOrder(Long userId, CreateOrderRequest request);
    List<OrderDto> getUserOrders(Long userId);
    OrderDto getOrderById(Long orderId, Long userId);
    OrderDto updateOrderStatus(Long orderId, String status);
}

