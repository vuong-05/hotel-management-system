package com.hotelmanagement.backend.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter @Setter
@Builder
public class BookingResponse {
    private String id;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer totalGuests;
    private String status;
    private BigDecimal totalAmount;
    private List<String> roomNumbers;
}