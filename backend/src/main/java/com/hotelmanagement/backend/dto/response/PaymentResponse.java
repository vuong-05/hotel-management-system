package com.hotelmanagement.backend.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter
@Builder
public class PaymentResponse {
    private Long id;
    private String bookingId;
    private String method;
    private String status;
    private BigDecimal amount;
    private String transactionRef;
    private LocalDateTime paidAt;
}