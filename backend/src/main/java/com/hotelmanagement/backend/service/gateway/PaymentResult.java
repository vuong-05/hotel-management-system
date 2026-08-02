package com.hotelmanagement.backend.service.gateway;

import lombok.*;

@Getter @Setter
@Builder
public class PaymentResult {
    private boolean success;
    private String transactionRef;
    private String message;
}