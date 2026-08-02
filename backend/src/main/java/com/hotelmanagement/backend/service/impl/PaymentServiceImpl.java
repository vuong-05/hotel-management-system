package com.hotelmanagement.backend.service.impl;

import com.hotelmanagement.backend.dto.request.PaymentRequest;
import com.hotelmanagement.backend.dto.response.PaymentResponse;
import com.hotelmanagement.backend.entity.Booking;
import com.hotelmanagement.backend.entity.Payment;
import com.hotelmanagement.backend.exception.BusinessException;
import com.hotelmanagement.backend.repository.BookingRepository;
import com.hotelmanagement.backend.repository.PaymentRepository;
import com.hotelmanagement.backend.service.PaymentService;
import com.hotelmanagement.backend.service.gateway.PaymentGateway;
import com.hotelmanagement.backend.service.gateway.PaymentResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final List<PaymentGateway> gateways;

    private Map<String, PaymentGateway> gatewayMap;

    private Map<String, PaymentGateway> getGatewayMap() {
        if (gatewayMap == null) {
            gatewayMap = gateways.stream()
                    .collect(Collectors.toMap(PaymentGateway::getMethodName, g -> g));
        }
        return gatewayMap;
    }

    @Override
    @Transactional
    public PaymentResponse pay(PaymentRequest request) {
        Booking booking = bookingRepository.findByPublicId(request.getBookingId())
                .orElseThrow(() -> new BusinessException("Không tìm thấy booking", HttpStatus.NOT_FOUND));

        if ("CANCELLED".equals(booking.getStatus())) {
            throw new BusinessException("Booking đã bị huỷ, không thể thanh toán", HttpStatus.BAD_REQUEST);
        }

        paymentRepository.findByBookingPublicId(request.getBookingId()).ifPresent(existing -> {
            if ("SUCCESS".equals(existing.getStatus())) {
                throw new BusinessException("Booking này đã được thanh toán", HttpStatus.CONFLICT);
            }
        });

        PaymentGateway gateway = getGatewayMap().get(request.getMethod());
        if (gateway == null) {
            throw new BusinessException("Phương thức thanh toán không hỗ trợ", HttpStatus.BAD_REQUEST);
        }

        PaymentResult result = gateway.process(request.getBookingId(), booking.getTotalAmount());

        Payment payment = Payment.builder()
                .booking(booking)
                .method(request.getMethod())
                .status(result.isSuccess() ? "SUCCESS" : "FAILED")
                .amount(booking.getTotalAmount())
                .transactionRef(result.getTransactionRef())
                .paidAt(result.isSuccess() ? LocalDateTime.now() : null)
                .build();

        paymentRepository.save(payment);

        if (result.isSuccess()) {
            booking.setStatus("CONFIRMED");
            bookingRepository.save(booking);
        }

        return toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getByBookingId(String bookingId) {
        Payment payment = paymentRepository.findByBookingPublicId(bookingId)
                .orElseThrow(() -> new BusinessException("Chưa có thanh toán cho booking này", HttpStatus.NOT_FOUND));
        return toResponse(payment);
    }

    private PaymentResponse toResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .bookingId(payment.getBooking().getPublicId())
                .method(payment.getMethod())
                .status(payment.getStatus())
                .amount(payment.getAmount())
                .transactionRef(payment.getTransactionRef())
                .paidAt(payment.getPaidAt())
                .build();
    }
}