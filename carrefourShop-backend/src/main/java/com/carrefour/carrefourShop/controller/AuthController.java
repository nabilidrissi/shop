package com.carrefour.carrefourShop.controller;

import com.carrefour.carrefourShop.dto.AuthRequest;
import com.carrefour.carrefourShop.dto.AuthResponse;
import com.carrefour.carrefourShop.dto.RegisterRequest;
import com.carrefour.carrefourShop.dto.UserDto;
import com.carrefour.carrefourShop.mapper.UserMapper;
import com.carrefour.carrefourShop.service.UserService;
import com.carrefour.carrefourShop.util.TokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and user registration APIs")
public class AuthController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account")
    public ResponseEntity<UserDto> register(@Valid @RequestBody RegisterRequest request) {
        UserDto user = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticates user and returns JWT token")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        AuthResponse response = userService.authenticate(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Returns information about the currently authenticated user")
    public ResponseEntity<UserDto> getCurrentUser(org.springframework.security.core.Authentication authentication) {
        Long userId = TokenUtil.getUserId(authentication);
        com.carrefour.carrefourShop.entity.User user = userService.getUserById(userId);
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Logs out the current user")
    public ResponseEntity<Map<String, String>> logout() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logout successful");
        return ResponseEntity.ok(response);
    }
}

