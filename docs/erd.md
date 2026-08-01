# ERD — Hotel Management System (Release 1 MVP)

```mermaid
erDiagram
    ROLES ||--o{ USERS : "has"
    ROLES ||--o{ ROLE_PERMISSIONS : "has"
    PERMISSIONS ||--o{ ROLE_PERMISSIONS : "has"

    USERS ||--o| CUSTOMERS : "extends"
    CUSTOMERS ||--o{ BOOKINGS : "makes"

    ROOM_TYPES ||--o{ ROOMS : "has"
    ROOM_TYPES ||--o{ ROOM_IMAGES : "has"
    ROOM_TYPES ||--o{ ROOM_AMENITIES : "has"
    AMENITIES ||--o{ ROOM_AMENITIES : "has"

    BOOKINGS ||--o{ BOOKING_DETAILS : "contains"
    ROOMS ||--o{ BOOKING_DETAILS : "booked in"

    BOOKINGS ||--o{ PAYMENTS : "paid by"

    ROLES {
        bigint id PK
        varchar name
    }
    PERMISSIONS {
        bigint id PK
        varchar code
    }
    ROLE_PERMISSIONS {
        bigint role_id FK
        bigint permission_id FK
    }
    USERS {
        bigint id PK
        varchar public_id
        varchar email
        varchar password_hash
        bigint role_id FK
    }
    CUSTOMERS {
        bigint id PK
        bigint user_id FK
        varchar address
    }
    ROOM_TYPES {
        bigint id PK
        varchar name
        decimal base_price
        int max_occupancy
    }
    ROOMS {
        bigint id PK
        varchar room_number
        bigint room_type_id FK
        varchar status
    }
    ROOM_IMAGES {
        bigint id PK
        bigint room_type_id FK
        varchar image_url
    }
    AMENITIES {
        bigint id PK
        varchar name
    }
    ROOM_AMENITIES {
        bigint room_type_id FK
        bigint amenity_id FK
    }
    BOOKINGS {
        bigint id PK
        varchar public_id
        bigint customer_id FK
        date check_in_date
        date check_out_date
        varchar status
        decimal total_amount
    }
    BOOKING_DETAILS {
        bigint id PK
        bigint booking_id FK
        bigint room_id FK
        decimal unit_price
        int nights
    }
    PAYMENTS {
        bigint id PK
        bigint booking_id FK
        varchar method
        varchar status
        decimal amount
    }
```

## Giải thích quan hệ chính

- **ROLES 1—n USERS**: 1 role có nhiều user, mỗi user chỉ thuộc 1 role.
- **ROLES n—n PERMISSIONS** (qua `ROLE_PERMISSIONS`): 1 role có nhiều quyền, 1 quyền có thể thuộc nhiều role.
- **USERS 1—1 CUSTOMERS**: chỉ user có role Customer mới có bản ghi mở rộng trong `customers`.
- **CUSTOMERS 1—n BOOKINGS**: 1 khách có thể có nhiều đơn đặt phòng.
- **ROOM_TYPES 1—n ROOMS**: 1 loại phòng áp dụng cho nhiều phòng vật lý cụ thể.
- **ROOM_TYPES n—n AMENITIES** (qua `ROOM_AMENITIES`): 1 loại phòng có nhiều tiện nghi, 1 tiện nghi dùng cho nhiều loại phòng.
- **BOOKINGS 1—n BOOKING_DETAILS**: 1 booking có thể đặt nhiều phòng cùng lúc.
- **BOOKINGS 1—n PAYMENTS**: 1 booking có thể có nhiều lần thanh toán (ví dụ đặt cọc + thanh toán còn lại).