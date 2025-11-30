package com.carrefour.carrefourShop.service;

import com.carrefour.carrefourShop.dto.AuthRequest;
import com.carrefour.carrefourShop.dto.AuthResponse;
import com.carrefour.carrefourShop.dto.RegisterRequest;
import com.carrefour.carrefourShop.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    UserDto register(RegisterRequest request);
    AuthResponse authenticate(AuthRequest request);
    UserDto getCurrentUser(String email);
    com.carrefour.carrefourShop.entity.User getUserById(Long id);
}

