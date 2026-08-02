package com.hotelmanagement.backend.repository;

import com.hotelmanagement.backend.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByBookingPublicId(String bookingPublicId);
}