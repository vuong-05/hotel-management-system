package com.hotelmanagement.backend.mapper;

import com.hotelmanagement.backend.dto.response.RoomResponse;
import com.hotelmanagement.backend.entity.Room;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface RoomMapper {

    @Mapping(target = "roomTypeName", source = "roomType.name")
    RoomResponse toResponse(Room entity);
}