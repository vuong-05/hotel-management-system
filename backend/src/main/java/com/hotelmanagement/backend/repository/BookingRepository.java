package com.hotelmanagement.backend.repository;

import com.hotelmanagement.backend.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findByCustomerUserEmail(String email, Pageable pageable);
    List<Booking> findByStatus(String status);
    Optional<Booking> findByPublicId(String publicId);
}