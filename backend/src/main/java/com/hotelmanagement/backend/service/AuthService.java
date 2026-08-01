package com.hotelmanagement.backend.service;

import com.hotelmanagement.backend.dto.request.*;
import com.hotelmanagement.backend.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(RefreshTokenRequest request);
    void logout(String refreshToken);
}