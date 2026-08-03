package com.hotelmanagement.backend.service;

import com.hotelmanagement.backend.dto.request.RoomRequest;
import com.hotelmanagement.backend.dto.response.RoomResponse;
import java.time.LocalDate;
import java.util.List;

public interface RoomService {
    List<RoomResponse> getAll();
    List<RoomResponse> getAvailableRooms(LocalDate checkIn, LocalDate checkOut, Long roomTypeId);
    RoomResponse create(RoomRequest request);
    RoomResponse updateStatus(Long id, String status);
    void delete(Long id);
}