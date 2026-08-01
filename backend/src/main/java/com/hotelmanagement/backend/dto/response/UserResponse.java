package com.hotelmanagement.backend.dto.response;

import lombok.*;

@Getter @Setter
@Builder
public class UserResponse {
    private String id;
    private String fullName;
    private String email;
    private String role;
}