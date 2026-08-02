package com.hotelmanagement.backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Getter @Setter
public class BookingRequest {

    @NotNull(message = "Ngày check-in không được để trống")
    @FutureOrPresent(message = "Ngày check-in phải từ hôm nay trở đi")
    private LocalDate checkInDate;

    @NotNull(message = "Ngày check-out không được để trống")
    private LocalDate checkOutDate;

    @NotNull(message = "Số lượng khách không được để trống")
    @Min(value = 1, message = "Phải có ít nhất 1 khách")
    private Integer totalGuests;

    @NotEmpty(message = "Phải chọn ít nhất 1 phòng")
    private List<Long> roomIds;
}