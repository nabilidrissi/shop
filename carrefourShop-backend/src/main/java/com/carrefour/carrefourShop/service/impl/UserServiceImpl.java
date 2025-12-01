package com.carrefour.carrefourShop.service.impl;

import com.carrefour.carrefourShop.dto.AuthRequest;
import com.carrefour.carrefourShop.dto.AuthResponse;
import com.carrefour.carrefourShop.dto.RegisterRequest;
import com.carrefour.carrefourShop.dto.UserDto;
import com.carrefour.carrefourShop.entity.User;
import com.carrefour.carrefourShop.exception.BusinessException;
import com.carrefour.carrefourShop.exception.ExceptionConstants;
import com.carrefour.carrefourShop.exception.ResourceNotFoundException;
import com.carrefour.carrefourShop.exception.UnauthorizedException;
import com.carrefour.carrefourShop.mapper.UserMapper;
import com.carrefour.carrefourShop.repository.UserRepository;
import com.carrefour.carrefourShop.service.UserService;
import com.carrefour.carrefourShop.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ExceptionConstants.EMAIL_ALREADY_EXISTS, ExceptionConstants.getMessage(ExceptionConstants.EMAIL_ALREADY_EXISTS));
        }

        User user = User.builder()
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .role(User.Role.USER)
                .build();

        user = userRepository.save(user);

        UserDto userDto = userMapper.toDto(user);

        return userDto;
    }

    @Override
    public AuthResponse authenticate(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException(ExceptionConstants.INVALID_EMAIL_OR_PASSWORD, ExceptionConstants.getMessage(ExceptionConstants.INVALID_EMAIL_OR_PASSWORD)));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException(ExceptionConstants.INVALID_EMAIL_OR_PASSWORD, ExceptionConstants.getMessage(ExceptionConstants.INVALID_EMAIL_OR_PASSWORD));
        }

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
        String token = jwtUtil.generateToken(userDetails, user.getRole().name());

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().name())
                .build();
    }


    @Override
    public UserDto getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionConstants.USER_NOT_FOUND, ExceptionConstants.getMessage(ExceptionConstants.USER_NOT_FOUND)));
        return userMapper.toDto(user);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionConstants.USER_NOT_FOUND, ExceptionConstants.getMessage(ExceptionConstants.USER_NOT_FOUND)));
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
    }
}

