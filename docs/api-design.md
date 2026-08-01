# API Design — Hotel Management System (Release 1 MVP)

## 1. Quy ước chung

- **Base URL**: `/api/v1`
- **Format**: JSON
- **Authentication**: JWT Bearer Token (header `Authorization: Bearer <token>`)
- **Versioning**: prefix `/api/v1` cho toàn bộ endpoint, đảm bảo có thể phát hành `/api/v2` sau này mà không phá vỡ client cũ.

### Response thành công (chuẩn)

```json
{
  "success": true,
  "message": "Success",
  "data": { },
  "timestamp": "2026-08-01T10:00:00Z"
}
```

### Response lỗi (chuẩn)

```json
{
  "success": false,
  "message": "Room not found",
  "errors": null,
  "timestamp": "2026-08-01T10:00:00Z"
}
```

### Response phân trang (chuẩn)

```json
{
  "success": true,
  "message": "Success",
  "data": {
    "content": [ ],
    "page": 0,
    "size": 10,
    "totalElements": 42,
    "totalPages": 5
  },
  "timestamp": "2026-08-01T10:00:00Z"
}
```

### HTTP Status Code sử dụng

| Code | Ý nghĩa |
|---|---|
| 200 | OK — thành công (GET, PUT, PATCH) |
| 201 | Created — tạo mới thành công (POST) |
| 204 | No Content — xoá thành công |
| 400 | Bad Request — dữ liệu không hợp lệ |
| 401 | Unauthorized — chưa đăng nhập / token sai |
| 403 | Forbidden — không đủ quyền |
| 404 | Not Found — không tìm thấy tài nguyên |
| 409 | Conflict — xung đột dữ liệu (VD: phòng đã được đặt) |
| 500 | Internal Server Error |

---

## 2. Authentication — `/api/v1/auth` (Public)

| Method | Endpoint | Mô tả | Request Body |
|---|---|---|---|
| POST | `/api/v1/auth/register` | Đăng ký tài khoản Customer | `email, password, fullName, phone` |
| POST | `/api/v1/auth/login` | Đăng nhập | `email, password` |
| POST | `/api/v1/auth/refresh-token` | Cấp lại Access Token | `refreshToken` |
| POST | `/api/v1/auth/logout` | Đăng xuất, thu hồi Refresh Token | `refreshToken` |

**Response mẫu — POST `/auth/login`**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "accessToken": "eyJhbGciOi...",
    "refreshToken": "eyJhbGciOi...",
    "tokenType": "Bearer",
    "expiresIn": 3600,
    "user": {
      "id": "a1b2c3d4-...",
      "fullName": "Nguyen Van A",
      "email": "a@example.com",
      "role": "CUSTOMER"
    }
  }
}
```

---

## 3. User / Customer — `/api/v1/users` (Protected)

| Method | Endpoint | Mô tả | Quyền |
|---|---|---|---|
| GET | `/api/v1/users/me` | Lấy thông tin cá nhân | Đã đăng nhập |
| PUT | `/api/v1/users/me` | Cập nhật thông tin cá nhân | Đã đăng nhập |
| PATCH | `/api/v1/users/me/avatar` | Cập nhật avatar | Đã đăng nhập |
| PATCH | `/api/v1/users/me/change-password` | Đổi mật khẩu | Đã đăng nhập |
| GET | `/api/v1/users` | Danh sách user (phân trang, filter theo role) | ADMIN |
| GET | `/api/v1/users/{id}` | Chi tiết 1 user | ADMIN |
| PATCH | `/api/v1/users/{id}/status` | Khoá / mở khoá tài khoản | ADMIN |

**Query params cho GET `/users`**: `page, size, sort, role, keyword`

---

## 4. Room Type — `/api/v1/room-types`

| Method | Endpoint | Mô tả | Quyền |
|---|---|---|---|
| GET | `/api/v1/room-types` | Danh sách loại phòng | Public |
| GET | `/api/v1/room-types/{id}` | Chi tiết loại phòng (kèm ảnh, tiện nghi) | Public |
| POST | `/api/v1/room-types` | Tạo loại phòng mới | ADMIN |
| PUT | `/api/v1/room-types/{id}` | Cập nhật loại phòng | ADMIN |
| DELETE | `/api/v1/room-types/{id}` | Xoá mềm loại phòng | ADMIN |
| POST | `/api/v1/room-types/{id}/images` | Upload ảnh (multipart, qua Cloudinary) | ADMIN |
| DELETE | `/api/v1/room-types/{id}/images/{imageId}` | Xoá ảnh | ADMIN |
| POST | `/api/v1/room-types/{id}/amenities` | Gán tiện nghi cho loại phòng | ADMIN |

**Query params cho GET `/room-types`**: `page, size, sort, minPrice, maxPrice, occupancy, keyword`

**Response mẫu — GET `/room-types/{id}`**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "name": "Deluxe",
    "description": "Phòng cao cấp view thành phố",
    "basePrice": 800000,
    "maxOccupancy": 3,
    "images": ["https://cloudinary.../img1.jpg"],
    "amenities": ["WiFi", "Minibar", "TV"]
  }
}
```

---

## 5. Room — `/api/v1/rooms`

| Method | Endpoint | Mô tả | Quyền |
|---|---|---|---|
| GET | `/api/v1/rooms/available` | Tìm phòng trống theo ngày | Public |
| GET | `/api/v1/rooms` | Danh sách toàn bộ phòng | ADMIN |
| GET | `/api/v1/rooms/{id}` | Chi tiết 1 phòng | ADMIN |
| POST | `/api/v1/rooms` | Tạo phòng mới | ADMIN |
| PUT | `/api/v1/rooms/{id}` | Cập nhật phòng | ADMIN |
| PATCH | `/api/v1/rooms/{id}/status` | Đổi trạng thái (AVAILABLE/MAINTENANCE/INACTIVE) | ADMIN |
| DELETE | `/api/v1/rooms/{id}` | Xoá mềm phòng | ADMIN |

**Query params cho GET `/rooms/available`**: `checkIn, checkOut, guests, roomTypeId, minPrice, maxPrice`

---

## 6. Booking — `/api/v1/bookings`

| Method | Endpoint | Mô tả | Quyền |
|---|---|---|---|
| POST | `/api/v1/bookings` | Tạo booking mới | Customer |
| GET | `/api/v1/bookings/my-bookings` | Lịch sử đặt phòng của tôi | Customer |
| GET | `/api/v1/bookings/{id}` | Chi tiết 1 booking | Customer (chủ sở hữu) / ADMIN |
| PATCH | `/api/v1/bookings/{id}/cancel` | Huỷ booking | Customer (chủ sở hữu) |
| GET | `/api/v1/bookings` | Danh sách toàn bộ booking | ADMIN |
| PATCH | `/api/v1/bookings/{id}/status` | Cập nhật trạng thái (Confirm/CheckIn/CheckOut/NoShow) | ADMIN |

**Query params cho GET `/bookings`**: `page, size, status, fromDate, toDate, keyword`

**Request mẫu — POST `/bookings`**
```json
{
  "checkInDate": "2026-08-05",
  "checkOutDate": "2026-08-08",
  "totalGuests": 2,
  "roomIds": [1, 2]
}
```

**Trạng thái Booking (state machine)**
```
PENDING → CONFIRMED → CHECKED_IN → CHECKED_OUT
   ↓
CANCELLED / NO_SHOW
```

---

## 7. Payment — `/api/v1/payments`

| Method | Endpoint | Mô tả | Quyền |
|---|---|---|---|
| POST | `/api/v1/payments` | Tạo thanh toán (mock) cho 1 booking | Customer |
| GET | `/api/v1/payments/{bookingId}` | Xem trạng thái thanh toán của booking | Customer (chủ sở hữu) / ADMIN |

**Request mẫu — POST `/payments`**
```json
{
  "bookingId": 10,
  "method": "MOCK"
}
```

---

## 8. Dashboard — `/api/v1/dashboard` (ADMIN)

| Method | Endpoint | Mô tả |
|---|---|---|
| GET | `/api/v1/dashboard/summary` | Tổng doanh thu, booking hôm nay/tháng, phòng trống, tỷ lệ lấp đầy |

**Response mẫu**
```json
{
  "success": true,
  "data": {
    "totalRevenue": 125000000,
    "todayBookings": 8,
    "monthlyBookings": 142,
    "availableRooms": 15,
    "occupiedRooms": 25,
    "occupancyRate": 62.5
  }
}
```

---

## 9. Phạm vi ngoài Release 1

Các nhóm API sau sẽ được thiết kế bổ sung khi triển khai Release tương ứng, không định nghĩa trước ở R1:

| Nhóm API | Thuộc Release |
|---|---|
| `/api/v1/services`, `/api/v1/service-orders` | R2 |
| `/api/v1/vouchers` | R2 |
| `/api/v1/employees` | R2 |
| `/api/v1/reviews` | R3 |
| `/api/v1/wishlist` | R3 |
| `/api/v1/notifications` | R3 |
| `/api/v1/reports/export` | R4 |
| `/api/v1/audit-logs` | R4 |

---

## 10. Checklist thiết kế API (đã review)

- [x] Toàn bộ endpoint dùng danh từ số nhiều, không dùng verb trong URL
- [x] Versioning `/api/v1` áp dụng toàn bộ
- [x] Public/Protected/Role được ghi rõ cho từng endpoint
- [x] Response format (success/error/pagination) thống nhất
- [x] HTTP status code dùng đúng ngữ nghĩa
- [x] State machine cho Booking status được định nghĩa rõ