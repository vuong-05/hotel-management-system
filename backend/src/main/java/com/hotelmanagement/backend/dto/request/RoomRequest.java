package com.hotelmanagement.backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter
public class RoomRequest {

    @NotBlank(message = "Số phòng không được để trống")
    private String roomNumber;

    @NotNull(message = "Loại phòng không được để trống")
    private Long roomTypeId;

    private Integer floor;
}