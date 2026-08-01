package com.hotelmanagement.backend.mapper;

import com.hotelmanagement.backend.dto.request.RoomTypeRequest;
import com.hotelmanagement.backend.dto.response.RoomTypeResponse;
import com.hotelmanagement.backend.entity.RoomType;
import org.mapstruct.*;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface RoomTypeMapper {

    @Mapping(target = "images", expression = "java(mapImages(entity))")
    RoomTypeResponse toResponse(RoomType entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "images", ignore = true)
    RoomType toEntity(RoomTypeRequest request);

    default java.util.List<String> mapImages(RoomType entity) {
        if (entity.getImages() == null) return java.util.List.of();
        return entity.getImages().stream()
                .map(img -> img.getImageUrl())
                .collect(Collectors.toList());
    }
}