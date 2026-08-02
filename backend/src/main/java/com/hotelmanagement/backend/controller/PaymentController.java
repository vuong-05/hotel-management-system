package com.hotelmanagement.backend.controller;

import com.hotelmanagement.backend.dto.request.PaymentRequest;
import com.hotelmanagement.backend.dto.response.*;
import com.hotelmanagement.backend.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> pay(@Valid @RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.pay(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Thanh toán thành công", response));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getByBookingId(@PathVariable String bookingId) {
        return ResponseEntity.ok(ApiResponse.success("Success", paymentService.getByBookingId(bookingId)));
    }
}