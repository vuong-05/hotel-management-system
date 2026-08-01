# Hotel Management System — Requirements & Roadmap

## 1. Giới thiệu dự án

Hotel Management System là hệ thống quản lý khách sạn Full Stack, được xây dựng theo
tiêu chuẩn doanh nghiệp nhằm mục đích portfolio ứng tuyển các vị trí:

- Java Backend Developer
- Java Full Stack Developer
- Spring Boot Developer

Dự án áp dụng kiến trúc Layered Architecture, RESTful API, DTO Pattern, JWT
Authentication, và được phát triển theo mô hình Release tăng dần (không code
toàn bộ tính năng cùng lúc).

## 2. Tech Stack

### Backend
Java 21, Spring Boot 3, Spring Security, JWT, Spring Data JPA, Hibernate, MySQL,
Maven, Lombok, MapStruct, Validation, Global Exception Handling, Swagger/OpenAPI,
SLF4J, JUnit + Mockito.

### Frontend
React, TypeScript, Vite, Tailwind CSS, React Router, Axios, TanStack Query,
React Hook Form, Zod, Shadcn UI / Material UI, Recharts.

### DevOps
Docker, Docker Compose, GitHub Actions, Render/Railway (backend deploy),
Vercel (frontend deploy), Cloudinary (upload ảnh).

## 3. Actor / Role trong hệ thống

| Role | Mô tả |
|---|---|
| Customer | Khách hàng đặt phòng |
| Receptionist | Lễ tân, xử lý check-in/out |
| Manager | Quản lý vận hành, xem báo cáo |
| Admin | Toàn quyền quản trị hệ thống |

## 4. Roadmap theo Release

| Release | Nội dung chính | Actor liên quan |
|---|---|---|
| **R1 — Core MVP** | Auth (JWT + Refresh Token), Room CRUD, Search/Filter/Sort phòng, Booking flow, Mock Payment, Invoice cơ bản | Customer, Admin |
| **R2 — Vận hành khách sạn** | Receptionist, Manager, Booking Status Workflow (Pending → Confirmed → Checked In → Checked Out → Cancelled → No Show), Service (Spa, Breakfast, Laundry...), Voucher | + Receptionist, Manager |
| **R3 — Trải nghiệm khách hàng** | Review, Wishlist, Notification, Email tự động (Booking Confirmation, Payment Success, Booking Cancelled, Forgot Password) | Customer |
| **R4 — Doanh nghiệp hoá** | Dashboard + Chart (Revenue, Booking, Occupancy Rate), Report Excel/PDF, Audit Log, Rate Limiting | Admin, Manager |
| **R5 — DevOps & Triển khai** | Docker, Docker Compose, GitHub Actions CI/CD, Deploy Render/Vercel, Cloudinary | Toàn hệ thống |

## 5. User Story — Release 1 (MVP)

### Customer
- Là khách, tôi muốn tìm phòng theo ngày check-in/out và số người, để biết phòng
  nào còn trống.
- Là khách, tôi muốn xem chi tiết phòng (hình ảnh, mô tả, tiện nghi, chính sách),
  để quyết định đặt phòng.
- Là khách, tôi muốn đặt phòng và nhận xác nhận, để yên tâm về chuyến đi của mình.
- Là khách, tôi muốn thanh toán (giả lập) cho booking, để hoàn tất đặt phòng.
- Là khách, tôi muốn đăng ký/đăng nhập bằng email, để quản lý tài khoản cá nhân.
- Là khách, tôi muốn xem lại lịch sử đặt phòng, để theo dõi các booking đã thực hiện.

### Admin
- Là admin, tôi muốn CRUD phòng và loại phòng, để quản lý inventory khách sạn.
- Là admin, tôi muốn CRUD tiện nghi (amenities) và gán cho từng loại phòng.
- Là admin, tôi muốn xem danh sách booking và cập nhật trạng thái, để vận hành
  lễ tân.
- Là admin, tôi muốn xem dashboard doanh thu cơ bản, để nắm tình hình kinh doanh.

## 6. Non-Functional Requirements

| Hạng mục | Yêu cầu |
|---|---|
| Security | JWT stateless, mã hoá mật khẩu bằng BCrypt, phân quyền theo Role + Permission, cấu hình CORS rõ ràng theo origin frontend |
| Scalability | Layered Architecture (Controller → Service → Repository), dễ tách microservice khi cần |
| Performance | Mọi API trả danh sách bắt buộc có Pagination, không trả nguyên bảng dữ liệu |
| Data Integrity | Nghiệp vụ đa bảng (tạo booking, trừ phòng trống, tạo invoice) chạy trong 1 transaction (`@Transactional`) |
| Maintainability | DTO tách biệt hoàn toàn khỏi Entity (dùng MapStruct), không trả Entity trực tiếp ra API |

## 7. Kiến trúc áp dụng

### Backend
Layered Architecture, RESTful API, DTO Pattern, Repository Pattern, Service
Pattern, Controller Pattern, Mapper Pattern, Global Exception Handling,
Validation, Pagination, Sorting, Filtering.

### Frontend
Feature-based Folder Structure, Reusable Components, Protected Routes,
Responsive Design, Dark Mode (optional).

## 8. Design Pattern đáng chú ý

- **Strategy Pattern cho Payment**: interface `PaymentGateway` dùng chung,
  implement `MockPaymentGateway` trước ở R1, sau đó thêm `VNPayGateway` /
  `MoMoGateway` mà không sửa logic nghiệp vụ.
- **RBAC (Role-Based Access Control)**: thiết kế bảng `roles` + `permissions`
  + `role_permissions` ngay từ R1, dù chỉ có 2 role, để R2 thêm Receptionist/
  Manager không cần sửa cấu trúc.

## 9. Định nghĩa "Hoàn thành" (Definition of Done) cho mỗi Release

- Toàn bộ API của Release có Swagger doc đầy đủ.
- Có Unit Test cho Service layer (JUnit + Mockito).
- README cập nhật hướng dẫn chạy tính năng mới.
- Code đã qua Pull Request review (tự review nếu làm một mình).
- Đã merge vào `main` và gắn Git tag nếu là mốc Release.

## 10. Ghi chú

Tài liệu này là nguồn tham chiếu xuyên suốt dự án, sẽ được cập nhật sau mỗi Phase.
Các tài liệu liên quan khác:
- `docs/database-design.md` — Thiết kế Database (Phase 2)
- `docs/erd.md` — ERD (Phase 3)
- `docs/api-design.md` — Thiết kế API (Phase 4)