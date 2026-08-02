package com.hotelmanagement.backend.service;

import com.hotelmanagement.backend.dto.request.PaymentRequest;
import com.hotelmanagement.backend.dto.response.PaymentResponse;

public interface PaymentService {
    PaymentResponse pay(PaymentRequest request);
    PaymentResponse getByBookingId(String bookingId);
}