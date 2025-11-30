package com.carrefour.carrefourShop.dto;

import com.carrefour.carrefourShop.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private Long id;
    private List<OrderItemDto> items;
    private Order.OrderStatus status;
    private BigDecimal totalPrice;
    private String shippingAddress;
    private String billingAddress;
    private String phone;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

