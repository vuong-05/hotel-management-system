package com.hotelmanagement.backend.dto.response;

import lombok.*;

@Getter @Setter
@Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long expiresIn;
    private UserResponse user;
}