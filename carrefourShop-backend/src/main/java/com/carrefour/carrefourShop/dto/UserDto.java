package com.carrefour.carrefourShop.dto;

import com.carrefour.carrefourShop.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private User.Role role;
    private CartDto cart;
    private LocalDateTime createdAt;
}

