-- =====================================================
-- HOTEL MANAGEMENT SYSTEM - SCHEMA R1 (MVP)
-- =====================================================

CREATE DATABASE IF NOT EXISTS hotel_management
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE hotel_management;

-- ============ AUTHORIZATION ============

CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE permissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE role_permissions (
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_rp_role FOREIGN KEY (role_id) REFERENCES roles(id),
    CONSTRAINT fk_rp_permission FOREIGN KEY (permission_id) REFERENCES permissions(id)
);

-- ============ USER ============

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    public_id VARCHAR(36) NOT NULL UNIQUE,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(150) NOT NULL,
    phone VARCHAR(20),
    avatar_url VARCHAR(500),
    role_id BIGINT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    address VARCHAR(255),
    date_of_birth DATE,
    CONSTRAINT fk_customers_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- ============ ROOM MANAGEMENT ============

CREATE TABLE room_types (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    base_price DECIMAL(12,2) NOT NULL,
    max_occupancy INT NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE rooms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_number VARCHAR(20) NOT NULL UNIQUE,
    room_type_id BIGINT NOT NULL,
    floor INT,
    status VARCHAR(30) NOT NULL DEFAULT 'AVAILABLE',
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_rooms_type FOREIGN KEY (room_type_id) REFERENCES room_types(id)
);

CREATE TABLE room_images (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_type_id BIGINT NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    display_order INT DEFAULT 0,
    CONSTRAINT fk_images_type FOREIGN KEY (room_type_id) REFERENCES room_types(id)
);

CREATE TABLE amenities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    icon VARCHAR(100)
);

CREATE TABLE room_amenities (
    room_type_id BIGINT NOT NULL,
    amenity_id BIGINT NOT NULL,
    PRIMARY KEY (room_type_id, amenity_id),
    CONSTRAINT fk_ra_type FOREIGN KEY (room_type_id) REFERENCES room_types(id),
    CONSTRAINT fk_ra_amenity FOREIGN KEY (amenity_id) REFERENCES amenities(id)
);

-- ============ BOOKING ============

CREATE TABLE bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    public_id VARCHAR(36) NOT NULL UNIQUE,
    customer_id BIGINT NOT NULL,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    total_guests INT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    total_amount DECIMAL(12,2) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_bookings_customer FOREIGN KEY (customer_id) REFERENCES customers(id)
);

CREATE TABLE booking_details (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    room_id BIGINT NOT NULL,
    unit_price DECIMAL(12,2) NOT NULL,
    nights INT NOT NULL,
    subtotal DECIMAL(12,2) NOT NULL,
    CONSTRAINT fk_bd_booking FOREIGN KEY (booking_id) REFERENCES bookings(id),
    CONSTRAINT fk_bd_room FOREIGN KEY (room_id) REFERENCES rooms(id)
);

-- ============ PAYMENT ============

CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    method VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    amount DECIMAL(12,2) NOT NULL,
    transaction_ref VARCHAR(100),
    paid_at DATETIME,
    CONSTRAINT fk_payments_booking FOREIGN KEY (booking_id) REFERENCES bookings(id)
);

-- ============ INDEXES ============

CREATE INDEX idx_bookings_daterange ON bookings(check_in_date, check_out_date);
CREATE INDEX idx_booking_details_room ON booking_details(room_id);
CREATE INDEX idx_rooms_type ON rooms(room_type_id);
CREATE INDEX idx_users_email ON users(email);

-- ============ SEED DATA (tối thiểu để test) ============

INSERT INTO roles (name) VALUES ('CUSTOMER'), ('ADMIN');

INSERT INTO permissions (code) VALUES
  ('ROOM_CREATE'), ('ROOM_UPDATE'), ('ROOM_DELETE'), ('ROOM_VIEW'),
  ('BOOKING_CREATE'), ('BOOKING_UPDATE'), ('BOOKING_VIEW'),
  ('USER_MANAGE');

-- Gán toàn bộ permission cho ADMIN (id=2), CUSTOMER (id=1) chỉ có quyền cơ bản
INSERT INTO role_permissions (role_id, permission_id)
SELECT 2, id FROM permissions;

INSERT INTO role_permissions (role_id, permission_id)
SELECT 1, id FROM permissions WHERE code IN ('ROOM_VIEW', 'BOOKING_CREATE', 'BOOKING_VIEW');