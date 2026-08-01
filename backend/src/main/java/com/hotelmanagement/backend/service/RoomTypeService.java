package com.hotelmanagement.backend.service;

import com.hotelmanagement.backend.dto.request.RoomTypeRequest;
import com.hotelmanagement.backend.dto.response.PageResponse;
import com.hotelmanagement.backend.dto.response.RoomTypeResponse;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;

public interface RoomTypeService {
    PageResponse<RoomTypeResponse> search(String keyword, BigDecimal minPrice, BigDecimal maxPrice,
                                           Integer occupancy, Pageable pageable);
    RoomTypeResponse getById(Long id);
    RoomTypeResponse create(RoomTypeRequest request);
    RoomTypeResponse update(Long id, RoomTypeRequest request);
    void delete(Long id);
}