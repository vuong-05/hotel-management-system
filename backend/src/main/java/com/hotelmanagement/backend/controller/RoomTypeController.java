package com.hotelmanagement.backend.controller;

import com.hotelmanagement.backend.dto.request.RoomTypeRequest;
import com.hotelmanagement.backend.dto.response.*;
import com.hotelmanagement.backend.service.RoomTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/room-types")
@RequiredArgsConstructor
public class RoomTypeController {

    private final RoomTypeService roomTypeService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<RoomTypeResponse>>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer occupancy,
            Pageable pageable) {

        PageResponse<RoomTypeResponse> result = roomTypeService.search(keyword, minPrice, maxPrice, occupancy, pageable);
        return ResponseEntity.ok(ApiResponse.success("Success", result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoomTypeResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Success", roomTypeService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RoomTypeResponse>> create(@Valid @RequestBody RoomTypeRequest request) {
        RoomTypeResponse response = roomTypeService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Tạo loại phòng thành công", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RoomTypeResponse>> update(@PathVariable Long id, @Valid @RequestBody RoomTypeRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Cập nhật thành công", roomTypeService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        roomTypeService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Xoá thành công", null));
    }
}