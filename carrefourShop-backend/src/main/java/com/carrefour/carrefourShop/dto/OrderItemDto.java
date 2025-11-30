package com.carrefour.carrefourShop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {
    private Long id;
    private ProductDto product;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
}

