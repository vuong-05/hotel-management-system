package com.hotelmanagement.backend.mapper;

import com.hotelmanagement.backend.dto.response.BookingResponse;
import com.hotelmanagement.backend.entity.Booking;
import org.mapstruct.*;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(target = "id", source = "publicId")
    @Mapping(target = "roomNumbers", expression = "java(mapRoomNumbers(entity))")
    BookingResponse toResponse(Booking entity);

    default java.util.List<String> mapRoomNumbers(Booking entity) {
        return entity.getBookingDetails().stream()
                .map(bd -> bd.getRoom().getRoomNumber())
                .collect(Collectors.toList());
    }
}