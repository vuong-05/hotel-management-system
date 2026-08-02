package com.hotelmanagement.backend.repository;

import com.hotelmanagement.backend.entity.BookingDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingDetailRepository extends JpaRepository<BookingDetail, Long> {
}