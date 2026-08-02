package com.hotelmanagement.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter
public class PaymentRequest {
    @NotBlank(message = "Booking ID không được để trống")
    private String bookingId;

    @NotBlank(message = "Phương thức thanh toán không được để trống")
    private String method;
}