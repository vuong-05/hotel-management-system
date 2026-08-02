package com.hotelmanagement.backend.controller;

import com.hotelmanagement.backend.dto.request.BookingRequest;
import com.hotelmanagement.backend.dto.response.*;
import com.hotelmanagement.backend.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponse>> create(
            @Valid @RequestBody BookingRequest request, Authentication authentication) {
        BookingResponse response = bookingService.create(request, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Đặt phòng thành công", response));
    }

    @GetMapping("/my-bookings")
    public ResponseEntity<ApiResponse<PageResponse<BookingResponse>>> getMyBookings(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        return ResponseEntity.ok(ApiResponse.success("Success", bookingService.getMyBookings(authentication, pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingResponse>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success("Success", bookingService.getById(id)));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancel(@PathVariable String id, Authentication authentication) {
        bookingService.cancel(id, authentication);
        return ResponseEntity.ok(ApiResponse.success("Huỷ booking thành công", null));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<BookingResponse>> updateStatus(
            @PathVariable String id, @RequestParam String status) {
        return ResponseEntity.ok(ApiResponse.success("Cập nhật trạng thái thành công", bookingService.updateStatus(id, status)));
    }
}