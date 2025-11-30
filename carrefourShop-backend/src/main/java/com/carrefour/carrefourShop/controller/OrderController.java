package com.carrefour.carrefourShop.controller;

import com.carrefour.carrefourShop.dto.CreateOrderRequest;
import com.carrefour.carrefourShop.dto.OrderDto;
import com.carrefour.carrefourShop.service.OrderService;
import com.carrefour.carrefourShop.util.TokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order management APIs")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Create order", description = "Creates a new order from the user's cart")
    public ResponseEntity<OrderDto> createOrder(
            Authentication authentication,
            @Valid @RequestBody CreateOrderRequest request) {
        Long userId = TokenUtil.getUserId(authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(userId, request));
    }

    @GetMapping
    @Operation(summary = "Get user orders", description = "Returns all orders for the current user")
    public ResponseEntity<List<OrderDto>> getUserOrders(Authentication authentication) {
        Long userId = TokenUtil.getUserId(authentication);
        return ResponseEntity.ok(orderService.getUserOrders(userId));
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get order by ID", description = "Returns a specific order by its ID")
    public ResponseEntity<OrderDto> getOrderById(
            Authentication authentication,
            @PathVariable Long orderId) {
        Long userId = TokenUtil.getUserId(authentication);
        return ResponseEntity.ok(orderService.getOrderById(orderId, userId));
    }

    @PutMapping("/{orderId}/status")
    @Operation(summary = "Update order status", description = "Updates the status of an order (ADMIN only)")
    public ResponseEntity<OrderDto> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, status));
    }
}

