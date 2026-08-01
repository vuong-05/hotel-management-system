package com.hotelmanagement.backend.repository;

import com.hotelmanagement.backend.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {

    boolean existsByRoomNumber(String roomNumber);

    // TODO: Sẽ bật lại ở Phase 8 sau khi có Entity Booking, BookingDetail
    // @Query("""
    //     SELECT r FROM Room r
    //     WHERE r.isDeleted = false
    //       AND r.status = 'AVAILABLE'
    //       AND (:roomTypeId IS NULL OR r.roomType.id = :roomTypeId)
    //       AND r.id NOT IN (
    //           SELECT bd.room.id FROM BookingDetail bd
    //           JOIN bd.booking b
    //           WHERE b.status IN ('PENDING', 'CONFIRMED', 'CHECKED_IN')
    //             AND b.checkInDate < :checkOut
    //             AND b.checkOutDate > :checkIn
    //       )
    //     """)
    // List<Room> findAvailableRooms(
    //         @Param("checkIn") LocalDate checkIn,
    //         @Param("checkOut") LocalDate checkOut,
    //         @Param("roomTypeId") Long roomTypeId
    // );
}