package com.hotelmanagement.backend.repository;

import com.hotelmanagement.backend.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    boolean existsByRoomNumber(String roomNumber);

    @Query("""
        SELECT r FROM Room r
        WHERE r.isDeleted = false
          AND r.status = 'AVAILABLE'
          AND (:roomTypeId IS NULL OR r.roomType.id = :roomTypeId)
          AND r.id NOT IN (
              SELECT bd.room.id FROM BookingDetail bd
              JOIN bd.booking b
              WHERE b.status IN ('PENDING', 'CONFIRMED', 'CHECKED_IN')
                AND b.checkInDate < :checkOut
                AND b.checkOutDate > :checkIn
          )
        """)
    List<Room> findAvailableRooms(
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut,
            @Param("roomTypeId") Long roomTypeId
    );

    // Dùng Pessimistic Lock để chống race condition khi tạo booking
    @Query("SELECT r FROM Room r WHERE r.id IN :roomIds")
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    List<Room> findByIdsForUpdate(@Param("roomIds") List<Long> roomIds);
}