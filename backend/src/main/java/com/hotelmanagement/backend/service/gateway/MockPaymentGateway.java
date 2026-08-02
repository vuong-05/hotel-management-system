package com.hotelmanagement.backend.service.gateway;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.UUID;

@Component
public class MockPaymentGateway implements PaymentGateway {

    @Override
    public PaymentResult process(String bookingPublicId, BigDecimal amount) {
        // Giả lập luôn thành công — sau này thay bằng gọi API VNPay/MoMo thật
        return PaymentResult.builder()
                .success(true)
                .transactionRef("MOCK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .message("Thanh toán giả lập thành công")
                .build();
    }

    @Override
    public String getMethodName() {
        return "MOCK";
    }
}