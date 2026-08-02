package com.hotelmanagement.backend.service.gateway;

import java.math.BigDecimal;

public interface PaymentGateway {
    PaymentResult process(String bookingPublicId, BigDecimal amount);
    String getMethodName();
}