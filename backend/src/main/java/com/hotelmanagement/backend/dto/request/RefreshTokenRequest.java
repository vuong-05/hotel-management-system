package com.hotelmanagement.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter
public class RefreshTokenRequest {
    @NotBlank
    private String refreshToken;
}