package com.hotelmanagement.backend.service.impl;

import com.hotelmanagement.backend.dto.request.BookingRequest;
import com.hotelmanagement.backend.dto.response.*;
import com.hotelmanagement.backend.entity.*;
import com.hotelmanagement.backend.exception.BusinessException;
import com.hotelmanagement.backend.mapper.BookingMapper;
import com.hotelmanagement.backend.repository.*;
import com.hotelmanagement.backend.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final CustomerRepository customerRepository;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public BookingResponse create(BookingRequest request, Authentication authentication) {

        if (!request.getCheckOutDate().isAfter(request.getCheckInDate())) {
            throw new BusinessException("Ngày check-out phải sau ngày check-in", HttpStatus.BAD_REQUEST);
        }

        Customer customer = customerRepository.findByUserEmail(authentication.getName())
                .orElseThrow(() -> new BusinessException("Không tìm thấy thông tin khách hàng", HttpStatus.NOT_FOUND));

        // Khoá các phòng được chọn (Pessimistic Lock) để tránh 2 request đặt trùng cùng lúc
        List<Room> rooms = roomRepository.findByIdsForUpdate(request.getRoomIds());

        if (rooms.size() != request.getRoomIds().size()) {
            throw new BusinessException("Một số phòng không tồn tại", HttpStatus.NOT_FOUND);
        }

        // Kiểm tra lại phòng trống NGAY TRONG transaction (không tin kết quả đã xem trước đó)
        List<Room> availableRooms = roomRepository.findAvailableRooms(
                request.getCheckInDate(), request.getCheckOutDate(), null);
        List<Long> availableIds = availableRooms.stream().map(Room::getId).toList();

        for (Long roomId : request.getRoomIds()) {
            if (!availableIds.contains(roomId)) {
                throw new BusinessException(
                        "Phòng ID " + roomId + " đã có người đặt trong khoảng thời gian này",
                        HttpStatus.CONFLICT);
            }
        }

        long nights = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());

        Booking booking = Booking.builder()
                .publicId(UUID.randomUUID().toString())
                .customer(customer)
                .checkInDate(request.getCheckInDate())
                .checkOutDate(request.getCheckOutDate())
                .totalGuests(request.getTotalGuests())
                .status("PENDING")
                .totalAmount(BigDecimal.ZERO)
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (Room room : rooms) {
            BigDecimal unitPrice = room.getRoomType().getBasePrice();
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(nights));

            BookingDetail detail = BookingDetail.builder()
                    .booking(booking)
                    .room(room)
                    .unitPrice(unitPrice)
                    .nights((int) nights)
                    .subtotal(subtotal)
                    .build();

            booking.getBookingDetails().add(detail);
            totalAmount = totalAmount.add(subtotal);
        }

        booking.setTotalAmount(totalAmount);
        bookingRepository.save(booking);

        return bookingMapper.toResponse(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BookingResponse> getMyBookings(Authentication authentication, Pageable pageable) {
        Page<Booking> page = bookingRepository.findByCustomerUserEmail(authentication.getName(), pageable);
        Page<BookingResponse> responsePage = page.map(bookingMapper::toResponse);
        return PageResponse.from(responsePage);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getById(String publicId) {
        Booking booking = findByPublicId(publicId);
        return bookingMapper.toResponse(booking);
    }

    @Override
    @Transactional
    public void cancel(String publicId, Authentication authentication) {
        Booking booking = findByPublicId(publicId);

        if (!booking.getCustomer().getUser().getEmail().equals(authentication.getName())) {
            throw new BusinessException("Bạn không có quyền huỷ booking này", HttpStatus.FORBIDDEN);
        }

        if (List.of("CHECKED_OUT", "CANCELLED").contains(booking.getStatus())) {
            throw new BusinessException("Không thể huỷ booking ở trạng thái này", HttpStatus.BAD_REQUEST);
        }

        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public BookingResponse updateStatus(String publicId, String status) {
        Booking booking = findByPublicId(publicId);
        booking.setStatus(status);
        bookingRepository.save(booking);
        return bookingMapper.toResponse(booking);
    }

    private Booking findByPublicId(String publicId) {
        return bookingRepository.findByPublicId(publicId)
                .orElseThrow(() -> new BusinessException("Không tìm thấy booking", HttpStatus.NOT_FOUND));
    }
}