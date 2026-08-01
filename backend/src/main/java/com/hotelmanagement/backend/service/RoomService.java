package com.hotelmanagement.backend.service;

import com.hotelmanagement.backend.dto.request.RoomRequest;
import com.hotelmanagement.backend.dto.response.RoomResponse;
import java.util.List;

public interface RoomService {
    List<RoomResponse> getAll();
    RoomResponse create(RoomRequest request);
    RoomResponse updateStatus(Long id, String status);
    void delete(Long id);
}