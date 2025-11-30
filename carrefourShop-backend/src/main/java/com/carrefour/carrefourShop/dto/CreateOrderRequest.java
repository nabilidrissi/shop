package com.carrefour.carrefourShop.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    @NotBlank(message = "Shipping address is required")
    private String shippingAddress;
    
    private String billingAddress;
    
    @NotBlank(message = "Phone is required")
    private String phone;
    
    private String email;
}

