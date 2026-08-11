# 🏨 Hotel Management System

Hệ thống quản lý khách sạn Full Stack — đặt phòng, quản lý phòng, thanh toán, xác thực JWT. Xây dựng theo tiêu chuẩn doanh nghiệp với kiến trúc Layered Architecture, RESTful API, và luồng nghiệp vụ đặt phòng an toàn (atomic transaction, chống race condition).

## 🔗 Live Demo

| | Link |
|---|---|
| 🌐 **Frontend (Web App)** | [hotel-management-frontend-rosy-omega.vercel.app](https://hotel-management-frontend-rosy-omega.vercel.app) |
| ⚙️ **Backend API (Swagger)** | [hotel-management-backend-zs32.onrender.com/swagger-ui.html](https://hotel-management-backend-zs32.onrender.com/swagger-ui.html) |

> ⚠️ Backend host trên gói Free của Render — sẽ "ngủ" sau 15 phút không có traffic. Lần truy cập đầu tiên có thể mất 30–60 giây để khởi động lại, các lần sau sẽ nhanh bình thường.

**Tài khoản demo:**
```
Customer:  customer@hotelms.com / 123456
```
*(hoặc tự đăng ký tài khoản mới để trải nghiệm)*

## 📸 Screenshots

<!-- Chèn ảnh chụp màn hình trang chủ, chi tiết phòng, booking tại đây -->

## ✨ Tính năng chính

- 🔐 **Authentication**: Đăng ký/Đăng nhập bằng JWT (Access Token + Refresh Token), tự động khôi phục phiên đăng nhập, thu hồi token khi đăng xuất
- 🛏️ **Quản lý phòng**: CRUD loại phòng & phòng vật lý, tìm kiếm/lọc/phân trang
- 📅 **Đặt phòng**: Kiểm tra phòng trống theo thời gian thực, transaction atomic chống đặt trùng phòng (Pessimistic Locking)
- 💳 **Thanh toán**: Thanh toán giả lập theo Strategy Pattern — dễ dàng mở rộng sang VNPay/MoMo mà không sửa logic nghiệp vụ
- 📊 **Lịch sử đặt phòng**: Xem và quản lý các booking cá nhân
- 👤 **Phân quyền**: RBAC (Role-Based Access Control) với Customer / Admin

## 🛠️ Tech Stack

**Backend**
![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4-brightgreen)
![Spring Security](https://img.shields.io/badge/Spring%20Security-JWT-green)
![MySQL](https://img.shields.io/badge/MySQL-8-blue)
![Maven](https://img.shields.io/badge/Maven-Build-red)

- Java 21, Spring Boot, Spring Security, Spring Data JPA, Hibernate
- MySQL, MapStruct, Lombok, Validation
- JWT (jjwt), Swagger/OpenAPI, SLF4J
- JUnit + Mockito

**Frontend**
![React](https://img.shields.io/badge/React-18-61DAFB)
![TypeScript](https://img.shields.io/badge/TypeScript-5-blue)
![Vite](https://img.shields.io/badge/Vite-Build-purple)
![TailwindCSS](https://img.shields.io/badge/TailwindCSS-4-38BDF8)

- React, TypeScript, Vite, Tailwind CSS v4
- React Router, Axios, TanStack Query

**DevOps**
- Docker (multi-stage build)
- Deploy: Render (Backend), Vercel (Frontend), Aiven (MySQL Database)
- CI/CD: Auto-deploy khi push lên `main`

## 🏗️ Kiến trúc

Backend áp dụng Layered Architecture chuẩn doanh nghiệp:

```
Controller → Service → Repository → Entity
     ↓           ↓
   DTO      Business Logic
     ↓           ↓
Global Exception Handler   Transaction Management
```

- **DTO Pattern**: Tách biệt hoàn toàn Entity khỏi dữ liệu trả về API (MapStruct)
- **Repository Pattern**: Spring Data JPA + Specification cho query động
- **Strategy Pattern**: Payment Gateway (dễ mở rộng VNPay/MoMo)
- **RBAC**: Roles/Permissions tách bảng riêng, linh hoạt mở rộng vai trò

Chi tiết đầy đủ về Database Design, ERD, và API Contract xem tại [`docs/`](docs/).

## 📂 Cấu trúc dự án

```
hotel-management-system/
├── backend/          # Spring Boot REST API
├── frontend/         # React + TypeScript SPA
├── database/         # SQL schema & migrations
└── docs/             # Requirements, ERD, API design, hướng dẫn triển khai
```

## 🚀 Chạy dự án ở local

### Yêu cầu
- Java 21 (khuyến nghị Eclipse Temurin)
- Node.js 18+
- MySQL 8

### Backend

```bash
cd backend
cp src/main/resources/application-local.yml.example src/main/resources/application-local.yml
# Sửa application-local.yml với thông tin MySQL của bạn
./mvnw spring-boot:run
```
Chạy schema trước khi khởi động: `database/schema-r1.sql` và `database/schema-r1.1-refresh-tokens.sql`.

API chạy tại `http://localhost:8080`, Swagger UI tại `http://localhost:8080/swagger-ui.html`.

### Frontend

```bash
cd frontend
cp .env.example .env
npm install
npm run dev
```
Ứng dụng chạy tại `http://localhost:5173`.

## 📖 Tài liệu

- [`docs/REQUIREMENTS.md`](docs/REQUIREMENTS.md) — Phân tích yêu cầu, roadmap theo Release
- [`docs/erd.md`](docs/erd.md) — Sơ đồ quan hệ dữ liệu (ERD)
- [`docs/api-design.md`](docs/api-design.md) — API Contract đầy đủ
- [`docs/PROJECT-GUIDE.md`](docs/PROJECT-GUIDE.md) — Hướng dẫn triển khai chi tiết từng Phase

## 📄 License

Dự án cá nhân phục vụ mục đích học tập và portfolio.