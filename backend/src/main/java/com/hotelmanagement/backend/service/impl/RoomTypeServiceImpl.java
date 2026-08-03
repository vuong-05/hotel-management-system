package com.hotelmanagement.backend.service.impl;

import com.hotelmanagement.backend.dto.request.RoomTypeRequest;
import com.hotelmanagement.backend.dto.response.*;
import com.hotelmanagement.backend.entity.RoomType;
import com.hotelmanagement.backend.exception.BusinessException;
import com.hotelmanagement.backend.mapper.RoomTypeMapper;
import com.hotelmanagement.backend.repository.RoomTypeRepository;
import com.hotelmanagement.backend.service.RoomTypeService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomTypeServiceImpl implements RoomTypeService {

    private final RoomTypeRepository roomTypeRepository;
    private final RoomTypeMapper roomTypeMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<RoomTypeResponse> search(String keyword, BigDecimal minPrice, BigDecimal maxPrice,
                                                  Integer occupancy, Pageable pageable) {

        Specification<RoomType> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.isFalse(root.get("isDeleted")));

            if (keyword != null && !keyword.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + keyword.toLowerCase() + "%"));
            }
            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("basePrice"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("basePrice"), maxPrice));
            }
            if (occupancy != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("maxOccupancy"), occupancy));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<RoomType> page = roomTypeRepository.findAll(spec, pageable);
        Page<RoomTypeResponse> responsePage = page.map(roomTypeMapper::toResponse);
        return PageResponse.from(responsePage);
    }

    @Override
    @Transactional(readOnly = true)
    public RoomTypeResponse getById(Long id) {
        RoomType roomType = findActiveById(id);
        return roomTypeMapper.toResponse(roomType);
    }

    @Override
    @Transactional
    public RoomTypeResponse create(RoomTypeRequest request) {
        RoomType roomType = roomTypeMapper.toEntity(request);
        roomTypeRepository.save(roomType);
        return roomTypeMapper.toResponse(roomType);
    }

    @Override
    @Transactional
    public RoomTypeResponse update(Long id, RoomTypeRequest request) {
        RoomType roomType = findActiveById(id);
        roomType.setName(request.getName());
        roomType.setDescription(request.getDescription());
        roomType.setBasePrice(request.getBasePrice());
        roomType.setMaxOccupancy(request.getMaxOccupancy());
        roomTypeRepository.save(roomType);
        return roomTypeMapper.toResponse(roomType);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        RoomType roomType = findActiveById(id);
        roomType.setIsDeleted(true);
        roomTypeRepository.save(roomType);
    }

    private RoomType findActiveById(Long id) {
        RoomType roomType = roomTypeRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy loại phòng", HttpStatus.NOT_FOUND));
        if (Boolean.TRUE.equals(roomType.getIsDeleted())) {
            throw new BusinessException("Loại phòng đã bị xoá", HttpStatus.NOT_FOUND);
        }
        return roomType;
    }
}