package com.hotelmanagement.backend.repository;

import com.hotelmanagement.backend.entity.RoomImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomImageRepository extends JpaRepository<RoomImage, Long> {
}