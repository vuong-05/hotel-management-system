package com.hotelmanagement.backend.service;

import com.hotelmanagement.backend.dto.request.RegisterRequest;
import com.hotelmanagement.backend.entity.Role;
import com.hotelmanagement.backend.exception.BusinessException;
import com.hotelmanagement.backend.repository.*;
import com.hotelmanagement.backend.security.JwtUtil;
import com.hotelmanagement.backend.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtUtil jwtUtil;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void register_ShouldThrowException_WhenEmailAlreadyExists() {
        ReflectionTestUtils.setField(authService, "accessTokenExpiration", 3600000L);
        ReflectionTestUtils.setField(authService, "refreshTokenExpiration", 604800000L);

        RegisterRequest request = new RegisterRequest();
        request.setEmail("existing@example.com");
        request.setPassword("123456");
        request.setFullName("Test User");

        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> authService.register(request));

        assertEquals("Email đã được sử dụng", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_ShouldThrowException_WhenCustomerRoleNotFound() {
        ReflectionTestUtils.setField(authService, "accessTokenExpiration", 3600000L);
        ReflectionTestUtils.setField(authService, "refreshTokenExpiration", 604800000L);

        RegisterRequest request = new RegisterRequest();
        request.setEmail("new@example.com");
        request.setPassword("123456");
        request.setFullName("Test User");

        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(roleRepository.findByName("CUSTOMER")).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> authService.register(request));
    }
}