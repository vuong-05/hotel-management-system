package com.hotelmanagement.backend.service;

import com.hotelmanagement.backend.dto.request.BookingRequest;
import com.hotelmanagement.backend.dto.response.BookingResponse;
import com.hotelmanagement.backend.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface BookingService {
    BookingResponse create(BookingRequest request, Authentication authentication);
    PageResponse<BookingResponse> getMyBookings(Authentication authentication, Pageable pageable);
    BookingResponse getById(String publicId);
    void cancel(String publicId, Authentication authentication);
    BookingResponse updateStatus(String publicId, String status);
}