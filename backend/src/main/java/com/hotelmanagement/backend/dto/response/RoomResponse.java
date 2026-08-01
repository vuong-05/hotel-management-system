package com.hotelmanagement.backend.dto.response;

import lombok.*;

@Getter @Setter
@Builder
public class RoomResponse {
    private Long id;
    private String roomNumber;
    private String roomTypeName;
    private Integer floor;
    private String status;
}