# Hotel Management System — Hướng dẫn tổng hợp (Phase 1 → 15)

> File này tổng hợp lại toàn bộ những gì đã làm từ Phase 1 đến Phase 15
> (khép kín Full-stack MVP + Deploy production), đã gộp sẵn các lỗi thực tế
> gặp phải và cách fix, để làm lại (hoặc đối chiếu) một lần là đúng, không
> phải sửa qua sửa lại nhiều lần.
>
> **Tài khoản test đang dùng trong dự án:**
> - Local: `test2@example.com` / `123456` (CUSTOMER), `admin@example.com` /
>   `admin123` (ADMIN)
> - Production: `admin@hotelms.com` / `admin123456` (ADMIN), `customer@hotelms.com` /
>   `123456` (CUSTOMER)
>
> **URL Production:**
> - Frontend: `https://hotel-management-frontend-rosy-omega.vercel.app`
> - Backend: `https://hotel-management-backend-zs32.onrender.com`

---

## PHASE 1 — Khởi tạo Repo & Tài liệu yêu cầu

### Việc cần làm

```bash
git config --global user.name "Tên bạn"
git config --global user.email "email@example.com"

mkdir hotel-management-system
cd hotel-management-system
git init
git branch -M main

mkdir -p docs backend frontend database
touch README.md
```

### Tạo `.gitignore` ở thư mục gốc

⚠️ **Điểm mấu chốt rút ra từ lỗi thực tế**: phải chặn **cả 2 đuôi** `.yml` và
`.yaml`, vì Git so khớp tên file chính xác từng ký tự — chỉ khai 1 đuôi rất dễ
bị lọt file chứa secret (đã xảy ra ở Phase 5).

```gitignore
### Backend - Java / Maven ###
backend/target/
backend/.mvn/
backend/*.iml
*.class
*.log

### IDE ###
.idea/
.vscode/
*.iml

### Frontend - Node / React ###
frontend/node_modules/
frontend/dist/
frontend/.env
frontend/.env.local

### Environment / Secrets — CHẶN CẢ 2 ĐUÔI ###
.env
*.env
application-local.yml
application-local.yaml
application-secrets.yml
application-secrets.yaml

### OS ###
.DS_Store
Thumbs.db
```

### Commit & đẩy lên GitHub

```bash
git add .gitignore README.md docs/
git commit -m "chore: initialize project structure and gitignore"

# Tạo repo rỗng trên GitHub trước (không tick Initialize README), sau đó:
git remote add origin https://github.com/<username>/hotel-management-system.git
git push -u origin main
```

### Tạo file `docs/REQUIREMENTS.md`

Nội dung đầy đủ: roadmap theo Release (R1 Core MVP → R5 DevOps), User Story cho
Customer/Admin, Non-Functional Requirements (Security, Scalability,
Performance, Data Integrity, Maintainability). *(Đã tạo xong ở Phase 1, giữ
nguyên không cần sửa.)*

```bash
git add docs/REQUIREMENTS.md
git commit -m "docs: add project requirements and MVP roadmap (Phase 1)"
git push origin main
```

### ⚠️ Lưu ý quan trọng đã rút ra

- Git **không track thư mục rỗng** — thư mục nào chưa có file bên trong sẽ
  không hiện trên GitHub. Muốn giữ chỗ, dùng file `.gitkeep`:
  ```bash
  touch backend/.gitkeep database/.gitkeep frontend/.gitkeep
  git add backend/.gitkeep database/.gitkeep frontend/.gitkeep
  git commit -m "chore: add placeholder folders for backend, database, frontend"
  ```

### Checklist hoàn thành Phase 1
- [ ] Repo khởi tạo, `.gitignore` chặn cả `.yml`/`.yaml` cho file secret
- [ ] `docs/REQUIREMENTS.md` có roadmap + user story + NFR
- [ ] Push GitHub thành công

---

## PHASE 2 — Thiết kế Database

### Quy trình Git (áp dụng cho mọi Phase từ đây về sau)

```bash
git checkout main
git pull origin main
git checkout -b feature/db-schema-r1
```

### Tạo file `database/schema-r1.sql`

12 bảng cho Release 1 (MVP): `roles`, `permissions`, `role_permissions`,
`users`, `customers`, `room_types`, `rooms`, `room_images`, `amenities`,
`room_amenities`, `bookings`, `booking_details`, `payments`.

**Điểm thiết kế quan trọng cần nhớ:**
- Giá phòng phải **snapshot** vào `booking_details.unit_price` tại thời điểm
  đặt, không tham chiếu trực tiếp `room_types.base_price`.
- "Phòng còn trống" là giá trị **tính động** theo overlap ngày, không lưu tĩnh
  trên bảng `rooms`.
- Không dùng `ENUM` của MySQL cho cột trạng thái — dùng `VARCHAR`.
- Index bắt buộc: `idx_bookings_daterange` trên `(check_in_date, check_out_date)`.

*(Script SQL đầy đủ đã có sẵn trong `database/schema-r1.sql` — giữ nguyên,
không cần viết lại.)*

### Query cốt lõi — kiểm tra phòng trống (logic quan trọng nhất hệ thống)

```sql
SELECT r.id, r.room_number
FROM rooms r
WHERE r.is_deleted = FALSE
  AND r.status = 'AVAILABLE'
  AND r.id NOT IN (
      SELECT bd.room_id
      FROM booking_details bd
      JOIN bookings b ON b.id = bd.booking_id
      WHERE b.status IN ('PENDING', 'CONFIRMED', 'CHECKED_IN')
        AND b.check_in_date < :checkOutMongMuon
        AND b.check_out_date > :checkInMongMuon
  );
```

Công thức overlap ngày kinh điển:
`existing.check_in < new.check_out AND existing.check_out > new.check_in`

### Test schema trên MySQL Workbench

1. Chạy toàn bộ script `schema-r1.sql`.
2. Insert dữ liệu test (`room_types`, `rooms`, `users`, `customers`,
   `bookings`, `booking_details`) — **chỉ để test, không đưa vào file SQL
   chính thức**.
3. Chạy lại query phòng trống, xác nhận phòng đã có booking overlap bị loại
   đúng khỏi kết quả.
4. Sau khi test xong, đảm bảo `schema-r1.sql` commit lên Git **chỉ chứa seed
   data chuẩn** (roles, permissions, role_permissions), không chứa dữ liệu
   test tay.

### Commit & merge

```bash
git add database/schema-r1.sql
git commit -m "feat(db): design R1 MVP schema with roles, rooms, bookings, payments"
git push -u origin feature/db-schema-r1
# Tạo Pull Request trên GitHub → main, review → merge

# Sau khi merge xong:
git checkout main
git pull origin main
git branch -d feature/db-schema-r1
git push origin --delete feature/db-schema-r1
```

### Checklist hoàn thành Phase 2
- [ ] 12 bảng đã tạo thành công, không lỗi
- [ ] Index hiệu năng đã có
- [ ] Query phòng trống test đúng logic overlap
- [ ] Đã merge qua Pull Request, xoá nhánh sau merge

---

## PHASE 3 — ERD (Sơ đồ quan hệ)

```bash
git checkout main && git pull origin main
git checkout -b feature/erd-diagram
```

Tạo file `docs/erd.md` dùng cú pháp **Mermaid** (GitHub tự render thành sơ đồ
trực quan, không cần công cụ ngoài):

````markdown
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
    ...
```
````

*(Nội dung đầy đủ đã có trong `docs/erd.md` — giữ nguyên.)*

Thêm link vào `README.md`:
```markdown
## Database Design
Xem chi tiết ERD tại [`docs/erd.md`](docs/erd.md)
```

```bash
git add docs/erd.md README.md
git commit -m "docs: add ERD diagram for R1 schema (Mermaid)"
git push -u origin feature/erd-diagram
# PR → merge → main

git checkout main && git pull origin main
git branch -d feature/erd-diagram
git push origin --delete feature/erd-diagram
```

### Checklist hoàn thành Phase 3
- [ ] `docs/erd.md` render đúng sơ đồ trên GitHub (không hiện text thô)
- [ ] README có link tới ERD

---

## PHASE 4 — Thiết kế API Contract

```bash
git checkout main && git pull origin main
git checkout -b feature/api-design
```

Tạo file `docs/api-design.md` gồm:
- Quy ước chung: base URL `/api/v1`, format response chuẩn (success/error/pagination)
- Bảng mã HTTP Status dùng đúng ngữ nghĩa (200/201/204/400/401/403/404/409)
- Endpoint đầy đủ cho: Auth, User, Room Type, Room, Booking, Payment, Dashboard
- State machine Booking: `PENDING → CONFIRMED → CHECKED_IN → CHECKED_OUT`,
  nhánh phụ `CANCELLED` / `NO_SHOW`
- Ghi chú các nhóm API thuộc R2–R4 (Service, Voucher, Review, Report...) chưa
  thiết kế vội

*(Nội dung đầy đủ đã có trong `docs/api-design.md` — giữ nguyên.)*

```bash
git add docs/api-design.md
git commit -m "docs: define REST API contract for R1 (auth, room, booking, payment)"
git push -u origin feature/api-design
# PR → merge → main

git checkout main && git pull origin main
git branch -d feature/api-design
git push origin --delete feature/api-design
```

### Checklist hoàn thành Phase 4
- [ ] Toàn bộ endpoint dùng danh từ số nhiều, có `/api/v1` prefix
- [ ] Mỗi endpoint ghi rõ Public/Protected/Role
- [ ] Response format thống nhất

---

## PHASE 5 — Khởi tạo Backend (Spring Boot)

### Bước 1 — Kiểm tra & cài đúng JDK 21 TRƯỚC KHI làm bất cứ gì

⚠️ **Bài học từ lỗi thực tế**: nếu máy đang có sẵn JDK 23/24/25, Lombok +
Maven compiler sẽ lỗi (`TypeTag :: UNKNOWN`). Dự án dùng **Java 21 LTS**, phải
cài đúng bản này trước khi tạo project.

1. Kiểm tra JDK hiện tại:
   ```powershell
   java -version
   ```
2. Nếu không phải bản `21.x.x`, tải JDK 21 (Eclipse Temurin):
   https://adoptium.net/temurin/releases/?version=21
3. Cấu hình `JAVA_HOME` trỏ đúng JDK 21 (Windows):
   - `Windows + S` → "Environment Variables" → "Edit the system environment variables"
   - **Environment Variables** → **System variables** → sửa/tạo `JAVA_HOME`
     trỏ tới thư mục JDK 21 (vd: `C:\Program Files\Eclipse Adoptium\jdk-21.x.x-hotspot`)
   - Trong biến `Path`, đảm bảo `%JAVA_HOME%\bin` đứng **trước** bất kỳ JDK
     nào khác
   - Đóng hẳn terminal cũ, mở terminal mới
4. Xác nhận lại:
   ```powershell
   java -version   # phải ra 21.x.x
   ```
5. Nếu dùng IntelliJ: `File → Project Structure → SDK` → chọn JDK 21.
   Nếu dùng VS Code: `Ctrl+Shift+P` → `Java: Configure Java Runtime` → trỏ
   JDK 21, đặt làm mặc định.

### Bước 2 — Tạo project qua Spring Initializr

Vào https://start.spring.io với cấu hình:
- Project: **Maven**, Language: **Java**, Spring Boot: **bản mới nhất do
  Initializr đề xuất** (thực tế dự án đang chạy trên **Spring Boot 4.0.7**,
  không phải 3.3.x như dự kiến ban đầu — xem ghi chú version bên dưới)
- Java: **21**
- Group: `com.hotelmanagement`, Artifact: `backend`, Packaging: **Jar**
- Dependencies: Spring Web, Spring Data JPA, Spring Security, Validation,
  MySQL Driver, Lombok, Spring Boot DevTools

Giải nén đè vào thư mục `backend/` đã có sẵn trong repo.

⚠️ **Ghi chú version quan trọng**: Tech Stack ban đầu ghi "Spring Boot 3",
nhưng Spring Initializr tại thời điểm tạo project đã lên **Spring Boot 4.0.7**
(nền Spring Framework 7). Quyết định: **giữ nguyên Spring Boot 4.x**, không cố
ép ngược về 3.x — vì tên nhiều dependency (`spring-boot-starter-webmvc`,
`spring-boot-starter-*-test`...) đã đổi giữa 2 version, hạ cấp gây vỡ hàng loạt.
Thay vào đó, đảm bảo mọi dependency đi kèm đều chọn đúng bản tương thích
Spring Boot 4 (xem bảng lỗi ở cuối file, mục `springdoc-openapi`).

### Bước 3 — Bổ sung dependencies vào `pom.xml`

Thêm MapStruct, JWT (jjwt), Swagger/OpenAPI, Mockito — xem chi tiết đầy đủ
trong lần hướng dẫn Phase 5 gốc (đã áp dụng, không lặp lại ở đây).

⚠️ **Bắt buộc cấu hình đúng thứ tự annotation processor** trong
`maven-compiler-plugin` (Lombok → mapstruct-processor →
lombok-mapstruct-binding), nếu không MapStruct sẽ không sinh code.

Nếu build vẫn lỗi Lombok dù đã đúng JDK 21, nâng Lombok lên bản mới nhất:
```xml
<version>1.18.36</version>
```
(đồng bộ cả trong dependency và trong annotationProcessorPaths).

### Bước 4 — Tạo cấu trúc package

```bash
cd backend/src/main/java/com/hotelmanagement/backend
mkdir -p config controller service/impl repository entity dto/request dto/response mapper exception security util
```

### Bước 5 — Cấu hình file properties

⚠️ **Quy tắc bắt buộc: LUÔN dùng đuôi `.yml`, không dùng `.yaml`.** Đây là
quy ước đã thống nhất và khớp với `.gitignore` — dùng sai đuôi sẽ khiến file
chứa secret **không được git bỏ qua** và bị đẩy lên GitHub công khai (đã xảy
ra thực tế).

Xoá `application.properties` mặc định, tạo:

**`backend/src/main/resources/application.yml`** (an toàn, được commit):
```yaml
spring:
  application:
    name: hotel-management-backend
  profiles:
    active: local
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: true
    properties:
      hibernate:
        format_sql: true
    open-in-view: false

server:
  port: 8080

springdoc:
  swagger-ui:
    path: /swagger-ui.html
  api-docs:
    path: /api-docs

logging:
  level:
    root: INFO
    com.hotelmanagement.backend: DEBUG
```

**`backend/src/main/resources/application-local.yml`** (⚠️ CHỨA SECRET —
KHÔNG COMMIT, phải nằm trong `.gitignore`):

⚠️ **Bài học từ lỗi thực tế**: MySQL 8 mặc định dùng
`caching_sha2_password`, nếu JDBC URL thiếu `allowPublicKeyRetrieval=true`
sẽ báo lỗi `Public Key Retrieval is not allowed`. Phải thêm tham số này ngay
từ đầu để tránh mất công debug lại.

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/hotel_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
    username: root
    password: your_mysql_password_here
    driver-class-name: com.mysql.cj.jdbc.Driver

jwt:
  secret: this-is-a-very-long-secret-key-change-me-in-production-min-256-bit
  access-token-expiration: 3600000
  refresh-token-expiration: 604800000
```

**`backend/src/main/resources/application-local.yml.example`** (an toàn,
được commit — làm mẫu cho người khác clone repo):
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/hotel_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
    username: root
    password: your_password_here

jwt:
  secret: change-this-secret-key
  access-token-expiration: 3600000
  refresh-token-expiration: 604800000
```

### Bước 6 — Kiểm tra `.gitignore` trước khi commit bất cứ gì

**Luôn chạy `git status` trước khi `git add .`** để chắc chắn không có file
`application-local.yml`/`.yaml` nào lọt vào danh sách staged:

```bash
git status
```

Nếu thấy `application-local.yml` xuất hiện trong danh sách sẽ bị commit →
DỪNG LẠI, kiểm tra lại `.gitignore` trước, không add file đó.

### Bước 7 — Chạy thử server

```bash
cd backend
./mvnw clean spring-boot:run
```

Kỳ vọng log:
```
Tomcat started on port 8080
Started BackendApplication in x.xxx seconds
```

Test: mở `http://localhost:8080/swagger-ui.html` — phải load được (dù chưa
có API nào).

### Bước 8 — Commit & merge

```bash
git checkout main && git pull origin main
git checkout -b feature/backend-init

git status   # kiểm tra kỹ, không có file secret trong danh sách
git add backend/
git add backend/src/main/resources/application-local.yml.example
git commit -m "chore(backend): initialize Spring Boot 3 project with base structure"

git push -u origin feature/backend-init
# PR → merge → main

git checkout main && git pull origin main
git branch -d feature/backend-init
git push origin --delete feature/backend-init
```

### 🚨 Nếu lỡ đã push nhầm file chứa secret lên GitHub

1. **Đổi ngay mật khẩu/secret đã lộ** (kể cả chỉ là mật khẩu MySQL local).
2. Gỡ khỏi tracking nhưng giữ file ở máy:
   ```bash
   git rm --cached backend/src/main/resources/application-local.yaml
   ```
3. Đổi tên đúng chuẩn `.yml` nếu bị sai đuôi.
4. Sửa `.gitignore` chặn cả 2 đuôi (xem Phase 1).
5. Commit lại:
   ```bash
   git add .gitignore
   git commit -m "fix(security): remove leaked local config, enforce .yml naming"
   git push origin main
   ```
6. Lưu ý: lịch sử Git cũ vẫn còn lưu nội dung file đã lộ (xem được qua tab
   History trên GitHub). Vì đây chỉ là secret local/test, không bắt buộc xoá
   lịch sử Git ở giai đoạn hiện tại — nhưng đây là bài học để **luôn
   `git status` trước khi `git add .`**.

### Checklist hoàn thành Phase 5
- [ ] `java -version` ra đúng `21.x.x`
- [ ] `./mvnw clean compile` không lỗi
- [ ] Server start thành công, Swagger UI load được
- [ ] `application-local.yml` (đúng đuôi `.yml`) KHÔNG xuất hiện trên GitHub
- [ ] `application-local.yml.example` CÓ xuất hiện trên GitHub

---

## PHASE 6 — Authentication (JWT)

### Chuẩn bị Database

Trước khi code Java, tạo thêm bảng `refresh_tokens` (migration mới, không sửa
`schema-r1.sql` cũ):

```sql
USE hotel_management;

CREATE TABLE refresh_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(500) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    expires_at DATETIME NOT NULL,
    is_revoked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users(id)
);
CREATE INDEX idx_refresh_tokens_user ON refresh_tokens(user_id);
```

Lưu vào `database/schema-r1.1-refresh-tokens.sql`.

⚠️ **Quy tắc bắt buộc rút ra từ lỗi thực tế**: mỗi khi thêm Entity mới ánh xạ
tới bảng mới, **luôn chạy migration SQL trên MySQL TRƯỚC**, rồi mới chạy lại
Spring Boot. Vì cấu hình `ddl-auto: validate` chỉ kiểm tra chứ không tự tạo
bảng — quên bước này sẽ gây lỗi `Schema validation: missing table [...]`.

### Nội dung code chính

- **Entity**: `User`, `Role`, `RefreshToken`
- **Repository**: `UserRepository`, `RoleRepository`, `RefreshTokenRepository`
- **Security**: `JwtUtil` (generate/validate token bằng `jjwt`),
  `CustomUserDetailsService` (kết nối Spring Security với bảng `users`),
  `JwtAuthenticationFilter` (chặn mọi request kiểm tra Bearer token)
- **Config**: `SecurityConfig` (khai `SecurityFilterChain`, CORS, BCrypt),
  `OpenApiConfig` (bắt buộc phải có để Swagger hiện nút **Authorize** — xem
  lỗi bên dưới)
- **DTO**: `RegisterRequest`, `LoginRequest`, `RefreshTokenRequest`,
  `AuthResponse`, `UserResponse`, `ApiResponse<T>` (envelope response dùng
  chung toàn hệ thống: `success/message/data/timestamp`)
- **Exception**: `BusinessException` (kèm `HttpStatus`), `GlobalExceptionHandler`
- **Service**: `AuthServiceImpl` — có `register/login/refreshToken/logout`,
  dùng **Refresh Token Rotation** (mỗi lần refresh sẽ revoke token cũ, phát
  hành token mới) để tăng bảo mật
- **Controller**: `AuthController` — 4 endpoint `/auth/register`, `/login`,
  `/refresh-token`, `/logout`

### Test qua Swagger (theo đúng thứ tự)

1. `POST /auth/register` → nhận `accessToken` + `refreshToken`
2. `POST /auth/login` → nhận token mới
3. `POST /auth/refresh-token` (dùng `refreshToken` ở bước 2) → nhận cặp token mới
4. `POST /auth/logout` (dùng `refreshToken` mới nhất)
5. Gọi lại `refresh-token` với token vừa logout → **phải báo lỗi 401** (xác
   nhận cơ chế thu hồi hoạt động đúng)

### Checklist hoàn thành Phase 6
- [ ] Bảng `refresh_tokens` đã tạo trên MySQL trước khi chạy server
- [ ] Đăng ký/đăng nhập/refresh/logout đều hoạt động đúng qua Swagger
- [ ] Token đã logout không dùng lại được (401)
- [ ] `./mvnw test` pass

---

## PHASE 7 — Room Management (Room Type & Room CRUD)

### Nội dung code chính

- **Entity**: `RoomType` (quan hệ `@OneToMany` với `RoomImage`), `Room`
  (`@ManyToOne` với `RoomType`), `RoomImage`
- **Repository**: `RoomTypeRepository extends JpaSpecificationExecutor` (lọc
  động theo giá/sức chứa/từ khoá không cần viết nhiều method), `RoomRepository`
- **DTO + Mapper (MapStruct)**: `RoomTypeRequest/Response`, `RoomRequest/Response`,
  `PageResponse<T>` (envelope phân trang dùng chung)
- **Service**: `RoomTypeServiceImpl` (search bằng `Specification`, soft
  delete), `RoomServiceImpl`
- **Controller**: `RoomTypeController`, `RoomController`
- **SecurityConfig**: GET `/room-types/**` public, còn lại (POST/PUT/DELETE
  room-types, toàn bộ `/rooms/**`) chỉ ADMIN

⚠️ **Lưu ý khi viết `RoomRepository.findAvailableRooms`**: query này tham
chiếu Entity `BookingDetail`/`Booking` (chưa tồn tại ở Phase 7, sẽ tạo ở Phase
8) → phải **comment lại toàn bộ method** bằng `//`, nếu không server sẽ lỗi
`Could not resolve root entity 'BookingDetail'` ngay lúc khởi động (Hibernate
validate JPQL tại thời điểm build bean, không phải lúc gọi API). Bật lại đúng
method này ở đầu Phase 8 khi đã có đủ Entity.

### Checklist hoàn thành Phase 7
- [ ] CRUD `room-types` hoạt động đủ (Create/Read/Update/Delete mềm)
- [ ] CRUD `rooms` hoạt động đủ
- [ ] Phân trang, lọc, sắp xếp test qua Swagger không lỗi
- [ ] `./mvnw test` pass

---

## PHASE 8 — Booking (atomic transaction)

### Nội dung code chính

- **Entity còn thiếu**: `Customer`, `Booking`, `BookingDetail`
- **Bổ sung `AuthServiceImpl.register()`**: tự động tạo `Customer` gắn với
  `User` mới — vì tài khoản tạo ở Phase 6 (trước khi có đoạn code này) sẽ
  thiếu `Customer`, cần insert tay 1 lần:
  ```sql
  INSERT INTO customers (user_id) SELECT id FROM users WHERE email = '...';
  ```
- **`RoomRepository`**: bật lại `findAvailableRooms` đã comment ở Phase 7,
  thêm `findByIdsForUpdate` dùng **Pessimistic Lock**
  (`@Lock(LockModeType.PESSIMISTIC_WRITE)`) để chống race condition khi 2
  người đặt trùng phòng cùng lúc
- **`BookingServiceImpl.create()`** — logic quan trọng nhất dự án:
  1. Khoá các phòng được chọn (Pessimistic Lock)
  2. Kiểm tra lại phòng trống **ngay trong transaction** (không tin kết quả
     đã xem trước đó — giữa lúc khách xem và lúc đặt có thể người khác đã
     đặt mất)
  3. Snapshot giá phòng vào `BookingDetail.unitPrice`
  4. Tính `totalAmount`, lưu `Booking` + `BookingDetail` cùng 1 transaction
     (`@Transactional`) — atomic, rollback toàn bộ nếu có lỗi ở bất kỳ bước nào
- **Đồng bộ `publicId` (UUID) thay vì lộ khoá chính nội bộ qua URL** — đúng
  nguyên tắc bảo mật đặt ra từ Phase 2. Controller nhận `@PathVariable String
  id` (là `publicId`), Service tra theo `findByPublicId`, không dùng `Long id`
  trực tiếp.

### Test qua Swagger (theo đúng thứ tự)

1. Tạo booking (`POST /bookings`) với 1 khoảng ngày cụ thể → `201`
2. Tạo lại booking **trùng phòng, trùng/chồng lấn ngày** → phải báo `409`
   (xác nhận chống race condition)
3. Tạo booking **khác ngày, không chồng lấn** → vẫn `201` bình thường
4. Huỷ booking (`PATCH /bookings/{publicId}/cancel`) → `200`, status
   chuyển `CANCELLED`
5. Đổi trạng thái bằng ADMIN (`PATCH /bookings/{publicId}/status`) → `200`

### Checklist hoàn thành Phase 8
- [ ] Đặt phòng thành công, tính đúng `totalAmount`
- [ ] Đặt trùng phòng/trùng ngày bị chặn (409)
- [ ] Huỷ booking, đổi trạng thái hoạt động đúng
- [ ] URL dùng `publicId` (UUID), không lộ khoá chính nội bộ
- [ ] `./mvnw test` pass

---

## PHASE 9 — Payment & Invoice cơ bản

### Nội dung code chính

- **Entity**: `Payment`
- **Strategy Pattern cho cổng thanh toán** (đúng cam kết từ Phase 1):
  - `PaymentGateway` (interface) — `process()`, `getMethodName()`
  - `MockPaymentGateway implements PaymentGateway` — giả lập luôn thành công
  - Sau này thêm VNPay/MoMo chỉ cần tạo thêm class `implements PaymentGateway`
    mới, không sửa `PaymentService` (Open/Closed Principle)
- **`PaymentServiceImpl.pay()`**:
  - Chặn thanh toán nếu booking đã `CANCELLED`
  - Chặn thanh toán trùng nếu đã có `Payment` với `status = SUCCESS`
  - Gọi đúng `PaymentGateway` theo `method` (tra qua `Map<String, PaymentGateway>`
    build từ danh sách bean `List<PaymentGateway>` — Spring tự inject tất cả
    implementation)
  - Thanh toán `SUCCESS` → tự động chuyển `booking.status` sang `CONFIRMED`
    trong cùng transaction

### Test qua Swagger (theo đúng thứ tự)

1. `POST /payments` với `bookingId` (publicId) đang `PENDING` → `201`,
   `status: SUCCESS`
2. `GET /bookings/{id}` → xác nhận status đã tự chuyển `CONFIRMED`
3. `POST /payments` lại với **cùng `bookingId`** → phải báo `409` (chặn thanh
   toán trùng)

### 🎉 Release 1 (MVP) hoàn thành

Đánh dấu bằng Git tag:
```bash
git tag -a v1.0.0-mvp -m "Release 1: Core MVP - Auth, Room, Booking, Mock Payment"
git push origin v1.0.0-mvp
```

### Checklist hoàn thành Phase 9
- [ ] Thanh toán thành công, booking tự chuyển `CONFIRMED`
- [ ] Thanh toán trùng bị chặn (409)
- [ ] Thanh toán booking đã huỷ bị chặn
- [ ] `./mvnw test` pass
- [ ] Git tag `v1.0.0-mvp` đã tạo

---

## PHASE 10 — Frontend Init (React + TypeScript + Vite)

### Nội dung chính

- Khởi tạo qua `npm create vite@latest . -- --template react-ts`, chọn
  **ESLint** (không chọn Oxlint — ESLint phổ biến hơn, quen thuộc với nhà
  tuyển dụng).
- ⚠️ **Tailwind CSS v4 đã đổi hoàn toàn cách khởi tạo** so với v3 mà phần
  lớn tài liệu cũ mô tả:
  - Không còn lệnh `tailwindcss init -p` (bản v4 **bỏ hẳn** lệnh `init`,
    chạy sẽ báo `could not determine executable to run`).
  - Cài đúng: `npm install tailwindcss @tailwindcss/vite` (không cần
    `postcss`, `autoprefixer` nữa).
  - Cấu hình qua plugin trong `vite.config.ts`:
    ```ts
    import tailwindcss from '@tailwindcss/vite'
    export default defineConfig({ plugins: [react(), tailwindcss()] })
    ```
  - `src/index.css` chỉ cần 1 dòng: `@import "tailwindcss";` (không phải 3
    dòng `@tailwind base/components/utilities` như v3).
  - Không bắt buộc phải có `tailwind.config.js`.
- Cấu trúc thư mục **Feature-based**: `features/{auth,rooms,booking}/{api,components,types}`,
  cộng với `components/{ui,layout}`, `lib/`, `hooks/`, `routes/`, `types/`.
- `lib/axios.ts`: 1 Axios instance dùng chung, có interceptor tự gắn
  `Authorization: Bearer <token>` vào mọi request.
- PowerShell không hiểu cú pháp `mkdir -p a/b, a/c` kiểu Bash — dùng
  `mkdir a/b, a/c` (phẩy, không `-p`) hoặc `New-Item -ItemType Directory -Force`.

### Checklist hoàn thành Phase 10
- [ ] `npm run dev` chạy, Tailwind hoạt động (test bằng 1 class màu)
- [ ] Cấu trúc thư mục Feature-based đã tạo đủ

---

## PHASE 11 — Frontend Authentication

### Nội dung chính

- `AuthContext.tsx`: Context lưu `user`, `accessToken` (chỉ trong memory —
  **không** lưu `localStorage` để tránh rủi ro XSS), `refreshToken` + `user`
  lưu `localStorage` để giữ đăng nhập qua lần F5.
- ⚠️ **Thiếu sót ban đầu, phải vá lại**: nếu chỉ lưu `accessToken` trong
  memory mà không tự động khôi phục, **F5 trang sẽ làm mất token**, mọi
  request sau đó trả về `403` dù `refreshToken` vẫn còn hợp lệ. Fix bằng
  cách gọi `refresh-token` API ngay trong `useEffect` khi `AuthProvider`
  khởi tạo, nếu phát hiện có `refreshToken` cũ trong `localStorage`.
- `RegisterPage.tsx`, `LoginPage.tsx`: form cơ bản, gọi API, `login()` xong
  điều hướng về `/`.

### Checklist hoàn thành Phase 11
- [ ] Đăng ký/đăng nhập qua giao diện thật, kết nối đúng Backend
- [ ] F5 giữa chừng KHÔNG làm mất đăng nhập (tự động refresh token)

---

## PHASE 12 — Trang chủ & Danh sách phòng

### Nội dung chính

- `RoomTypeList.tsx`: dùng TanStack Query (`useQuery`) gọi `GET /room-types`,
  hiển thị dạng lưới (grid) card.
- Không cần đăng nhập vẫn xem được (đúng thiết kế API public).

### Checklist hoàn thành Phase 12
- [ ] Trang chủ hiện đúng danh sách phòng lấy từ Backend thật

---

## PHASE 13 — Chi tiết phòng & Đặt phòng

### Nội dung chính

- ⚠️ **API `GET /rooms/available` thiết kế từ Phase 4 nhưng chưa từng có
  Controller thật** — phải bổ sung ở Backend trước khi Frontend gọi được:
  thêm `getAvailableRooms()` vào `RoomService`/`RoomServiceImpl`, thêm route
  `GET /rooms/available` vào `RoomController`, và mở public trong
  `SecurityConfig` (`.requestMatchers(HttpMethod.GET, "/api/v1/rooms/available").permitAll()`).
- `RoomDetailPage.tsx`: form chọn ngày → `useMutation` gọi tìm phòng trống →
  chọn phòng (radio) → `useMutation` gọi tạo booking. Nếu chưa đăng nhập,
  bấm "Đặt phòng" sẽ điều hướng sang `/login`.
- Card phòng ở trang chủ bọc trong `<Link to={/rooms/${id}}>` để bấm vào
  được.

### Checklist hoàn thành Phase 13
- [ ] Tìm phòng trống hoạt động đúng qua giao diện
- [ ] Đặt phòng thành công, nhận được mã booking (UUID)

---

## PHASE 14 — Thanh toán & Lịch sử đặt phòng

### Nội dung chính

- `MyBookingsPage.tsx`: `GET /bookings/my-bookings`, hiển thị trạng thái
  từng booking (map màu theo status), nút "Thanh toán ngay" chỉ hiện khi
  `status === 'PENDING'`.
- `Header.tsx`: menu điều hướng (Trang chủ / Booking của tôi / Đăng
  nhập-Đăng ký hoặc Xin chào + Đăng xuất tuỳ `isAuthenticated`), gắn vào
  `App.tsx` để hiện ở mọi trang.
- Thanh toán xong dùng `queryClient.invalidateQueries` để tự động refetch
  danh sách booking, cập nhật trạng thái ngay không cần F5.

### Checklist hoàn thành Phase 14
- [ ] Luồng end-to-end hoàn chỉnh: đăng ký → tìm phòng → đặt → xem lịch sử
      → thanh toán → trạng thái tự chuyển "Đã xác nhận"

### 🎉 Full-stack MVP hoàn thành (Phase 1-14)
```bash
git tag -a v1.0.0-fullstack-mvp -m "Full-stack MVP complete: Auth, Room, Booking, Payment (Backend + Frontend)"
git push origin v1.0.0-fullstack-mvp
```

---

## PHASE 15 — Deploy Production

### Lựa chọn nền tảng (miễn phí, đã kiểm chứng thực tế)

| Thành phần | Nền tảng | Ghi chú |
|---|---|---|
| Database MySQL | **Aiven** | Free vĩnh viễn, không cần thẻ. Render chỉ free PostgreSQL, **không có MySQL free** |
| Backend | **Render** | Free web service, tự deploy khi push `main`. Ngủ sau 15 phút không traffic, lần đầu load lại chậm 30-60s |
| Frontend | **Vercel** | Free, build nhanh (1-3 phút), tối ưu cho SPA |

### Bước 1 — Database (Aiven)

1. Tạo service MySQL trên Aiven (Free plan), đợi status **Running**.
2. Lấy `Host`, `Port`, `Database name`, `User`, `Password` từ tab Connection
   information.
3. Kết nối MySQL Workbench tới Aiven (cần bật SSL: tab SSL → "Use SSL if
   available").
4. Chạy `schema-r1.sql` (giữ nguyên `CREATE DATABASE hotel_management`,
   không cần đổi tên database) và `schema-r1.1-refresh-tokens.sql`.

### Bước 2 — Chuẩn bị code Backend cho deploy

**`application.yml`** — đổi từ giá trị cứng sang đọc biến môi trường:
```yaml
spring:
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:local}
  datasource:
    url: ${DB_URL:}
    username: ${DB_USERNAME:}
    password: ${DB_PASSWORD:}
server:
  port: ${PORT:8080}
jwt:
  secret: ${JWT_SECRET:...}
  access-token-expiration: ${JWT_ACCESS_EXPIRATION:3600000}
  refresh-token-expiration: ${JWT_REFRESH_EXPIRATION:604800000}
```
`application-local.yml` (giá trị cứng cho local) vẫn giữ nguyên — Spring tự
ưu tiên nạp đè lên khi `profiles.active=local` (mặc định), nên chạy local
không bị ảnh hưởng.

**`Dockerfile`** (đặt trong `backend/`, multi-stage build để đảm bảo đúng
JDK 21 dù Render mặc định bản khác):
```dockerfile
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Bước 3 — Deploy Backend (Render)

1. New Web Service → connect repo GitHub → **Root Directory: `backend`**
   (bắt buộc, vì Dockerfile nằm trong đó) → Runtime: **Docker** → Free.
2. Environment Variables cần set đủ 7 biến:
   `SPRING_PROFILES_ACTIVE=prod`, `DB_URL` (JDBC URL trỏ Aiven, dùng
   `useSSL=true&requireSSL=true` — khác local dùng `useSSL=false`),
   `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, `JWT_ACCESS_EXPIRATION`,
   `JWT_REFRESH_EXPIRATION`.
3. Create Web Service, đợi build (~5-10 phút lần đầu do tải Maven).
4. Mỗi lần `git push`/merge PR vào `main`, Render **tự động build lại**
   (không cần thao tác thủ công). Muốn ép build lại tay: nút **Manual
   Deploy → Deploy latest commit**.

### Bước 4 — Deploy Frontend (Vercel)

1. Add New Project → import repo → **Root Directory: `frontend`** →
   Framework tự nhận **Vite**.
2. Environment Variable: `VITE_API_BASE_URL` = URL Backend thật trên
   Render + `/api/v1` (ví dụ
   `https://hotel-management-backend-xxxx.onrender.com/api/v1`).
3. Deploy — nhanh hơn Backend nhiều vì chỉ build static.

### Bước 5 — Tạo dữ liệu mẫu trên production

Database Aiven ban đầu **trống trơn** (chỉ có `roles`/`permissions` từ
seed data) — khác hẳn database local đã có sẵn dữ liệu test. Phải:
1. Đăng ký 1 tài khoản qua Frontend production → nâng role lên ADMIN bằng
   SQL trực tiếp trên Aiven.
2. Đăng nhập lấy token ADMIN, dùng Swagger production (`<backend-url>/swagger-ui.html`)
   để tạo `room-types` và `rooms` mẫu — y hệt cách đã làm ở Phase 7 cho
   local, chỉ khác là gọi vào URL production.
3. Đăng ký thêm 1 tài khoản CUSTOMER riêng để test luồng đặt phòng (không
   dùng chung tài khoản ADMIN).

### ⚠️ 4 lỗi lớn nhất khi deploy — đọc kỹ trước khi làm lại

1. **Build Docker fail vì lỗi code cũ chưa lộ ở local** — máy local build
   kiểu incremental (không xoá cache), có thể "che" mất 1 method bị thiếu
   trong class implement interface. Docker build luôn sạch từ đầu nên lỗi
   lộ ra ngay (`class ... is not abstract and does not override abstract
   method ...`). Luôn `./mvnw clean spring-boot:run` (có `clean`) ở local
   trước khi push, không chỉ chạy suông.
2. **`sort=["string"]` lỗi 500 lặp lại y hệt trên production** dù đã fix
   1 Controller (`RoomTypeController`) — vì mỗi Controller có tham số
   `Pageable` phải tự áp dụng cách né `sort` riêng (dùng `page`/`size` +
   tự dựng `PageRequest`, không nhận `Pageable` trần), sửa 1 chỗ không tự
   động fix chỗ khác.
3. **CORS chặn Frontend production gọi Backend production** — status
   `403 Forbidden` khi gọi từ domain Vercel. `SecurityConfig` chỉ khai
   `http://localhost:5173` trong `allowedOrigins` — phải thêm domain
   Vercel thật vào danh sách:
   ```java
   config.setAllowedOrigins(List.of(
       "http://localhost:5173",
       "https://<your-frontend>.vercel.app"
   ));
   ```
4. **Trang chủ production hiện "Chưa có phòng"** — không phải bug, chỉ vì
   database Aiven là môi trường **hoàn toàn tách biệt** với MySQL local,
   dữ liệu test ở local không tự động có trên production. Phải tạo lại dữ
   liệu mẫu qua Swagger production (xem Bước 5).

### Checklist hoàn thành Phase 15
- [ ] Database Aiven có đủ schema, ở trạng thái Running
- [ ] Backend Render trạng thái "Live", Swagger production load được
- [ ] Frontend Vercel trạng thái "Ready", gọi đúng tới Backend Render (kiểm
      tra qua tab Network, không phải `localhost`)
- [ ] CORS đã thêm domain Vercel, không còn lỗi 403 khi gọi cross-origin
- [ ] Có dữ liệu mẫu (room-types, rooms) trên production
- [ ] Test trọn vẹn luồng end-to-end trên URL production bằng tài khoản
      CUSTOMER: đăng ký → đặt phòng → xem lịch sử → thanh toán

---

## Bảng tổng hợp lỗi thường gặp & cách fix (tra cứu nhanh)

| Lỗi | Nguyên nhân | Cách fix |
|---|---|---|
| `TypeTag :: UNKNOWN` khi compile | JDK quá mới (23+) không tương thích Lombok | Cài & trỏ project dùng JDK 21 LTS |
| `java -version` vẫn ra bản cũ sau khi cài JDK mới | Biến môi trường `JAVA_HOME`/`Path` chưa cập nhật | Sửa `JAVA_HOME` trong System Environment Variables, mở terminal mới |
| `Public Key Retrieval is not allowed` | MySQL 8 dùng `caching_sha2_password`, thiếu tham số JDBC | Thêm `&allowPublicKeyRetrieval=true` vào JDBC URL (chỉ dùng local) |
| File `application-local.yaml` bị lộ lên GitHub | Gõ nhầm `.yaml` thay vì `.yml`, `.gitignore` chỉ khai 1 đuôi | Đổi tên đúng `.yml`, sửa `.gitignore` chặn cả 2 đuôi, đổi secret đã lộ |
| Thư mục tạo xong nhưng không thấy trên GitHub | Git không track thư mục rỗng | Thêm file `.gitkeep` hoặc file thật vào thư mục |
| `NoSuchMethodError: ControllerAdviceBean.<init>`, Swagger `/api-docs` trả 500 | `springdoc-openapi` bản 2.x không tương thích Spring Boot 4.x | Dùng `springdoc-openapi-starter-webmvc-ui` bản **3.0.3+**, giữ nguyên Spring Boot 4.x (không hạ về 3.x vì tên nhiều starter đã đổi) |
| Maven báo `'dependencies.dependency.version' ... is missing` khi hạ `<parent>` version | pom.xml dùng tên dependency kiểu Spring Boot 4 (`spring-boot-starter-webmvc`, `-test` suffix mới) nhưng khai `<parent>` version 3.x | Không hạ cấp `<parent>`; giữ đúng version Spring Boot mà Initializr đã sinh ra ban đầu |
| Khởi động lỗi `Schema validation: missing table [xxx]` | Entity mới ánh xạ bảng chưa có trên MySQL (do `ddl-auto: validate` không tự tạo bảng) | Chạy file migration SQL tạo bảng đó **trước**, rồi mới chạy lại server |
| `Could not resolve root entity 'BookingDetail'` (hoặc Entity khác) khi start server | Repository có `@Query` JPQL tham chiếu Entity chưa được tạo | Comment lại toàn bộ method `@Query` đó cho tới khi Entity liên quan tồn tại |
| Swagger không có nút "Authorize" | Chưa cấu hình `SecurityScheme` cho OpenAPI | Tạo `config/OpenApiConfig.java` khai `SecurityScheme` kiểu `bearer`/`JWT` |
| `Sort expression '[...]: ASC' must only contain property references...` (500) khi gọi API có phân trang | Swagger UI tự động điền `sort=string` (hoặc `sort=[]`) vào tham số phân trang, ghi đè giá trị mặc định | Trong Controller, không nhận `Pageable` trần — dùng `@RequestParam int page/size` rồi tự dựng `PageRequest.of(page, size, Sort.by(...))`, bỏ qua hoàn toàn `sort` do client gửi |
| `Cannot lazily initialize collection/proxy ... (no session)` khi gọi API trả về Entity có quan hệ | Quan hệ `@ManyToOne`/`@OneToMany` mặc định `FetchType.LAZY`, session Hibernate đã đóng trước khi Mapper đọc dữ liệu quan hệ | Thêm `@Transactional(readOnly = true)` vào **mọi** method Service trả về dữ liệu có quan hệ LAZY |
| `403 Forbidden` khi gọi API dù đã đăng nhập | Token đang dùng thuộc tài khoản sai role (VD: dùng tài khoản ADMIN gọi API chỉ dành cho CUSTOMER hoặc ngược lại) | Dùng đúng tài khoản test theo role — xem bảng tài khoản test ở đầu file; đăng nhập lại lấy token đúng role trước khi Authorize lại trên Swagger |
| `409 Conflict` khi tạo mới (email/số phòng/...) dù nghĩ là dữ liệu mới | Dữ liệu đó đã được tạo ở lần test trước đó (không phải bug) | Kiểm tra lại bằng `SELECT` trong MySQL trước khi đoán mò giá trị mới |
| `could not determine executable to run` khi chạy `npx tailwindcss init -p` | Tailwind CSS v4 đã bỏ hẳn lệnh `init` | Cài `tailwindcss @tailwindcss/vite`, cấu hình qua plugin trong `vite.config.ts`, dùng `@import "tailwindcss";` trong CSS thay vì 3 dòng `@tailwind` |
| `mkdir : A positional parameter cannot be found...` trên Windows | Cú pháp `mkdir -p a/b a/c` là Bash, PowerShell không hiểu | Dùng `mkdir a/b, a/c` (phẩy) hoặc `New-Item -ItemType Directory -Force -Path ...` |
| Đăng nhập thất bại / trang chủ kẹt mãi "Đang tải..." khi test Frontend | Backend đang không chạy (terminal đã dừng) | Kiểm tra Swagger `<backend-url>/swagger-ui.html` có load được không; nếu không, khởi động lại Backend |
| Đặt phòng báo lỗi 403 dù vừa đăng nhập xong | `accessToken` chỉ lưu trong memory, bị mất khi F5 trang giữa chừng lúc test | Thêm cơ chế tự động gọi `refresh-token` khi `AuthProvider` khởi tạo (đọc `refreshToken` từ `localStorage`) |
| Build Docker fail trên Render dù local chạy được (`class ... does not override abstract method ...`) | Máy local build kiểu incremental, "che" mất lỗi thiếu method; Docker build sạch từ đầu nên lộ ra | Luôn `./mvnw clean ...` (có `clean`) ở local trước khi push để bắt lỗi sớm, giống hệt môi trường build sạch của Docker |
| API `sort=["string"]` vẫn lỗi 500 trên production dù đã fix 1 Controller khác | Mỗi Controller có `Pageable` là 1 chỗ lỗi độc lập, phải tự sửa riêng | Rà soát toàn bộ Controller có tham số `Pageable`, áp dụng cùng cách né `sort` (dùng `page`/`size` + `PageRequest.of(...)`) cho từng cái |
| `403 Forbidden` khi Frontend production gọi Backend production (nhưng local vẫn OK) | CORS `allowedOrigins` trong `SecurityConfig` chỉ khai `localhost:5173`, chưa có domain Vercel | Thêm domain Frontend production thật vào `config.setAllowedOrigins(List.of(...))` |
| Trang chủ production hiện "Chưa có phòng nào" dù local có đủ dữ liệu | Database Aiven (production) và MySQL local là 2 database hoàn toàn tách biệt | Tạo lại dữ liệu mẫu qua Swagger production (đăng ký ADMIN, tạo room-types/rooms) — không tự đồng bộ từ local |

---

## Tiếp theo: Phase 16 trở đi

Full-stack MVP (Phase 1-14) đã hoàn thành và **đã deploy production**
(Phase 15) — có link demo sống, database thật, CI/CD tự động khi push
`main`. Ba hướng có thể đi tiếp, theo đúng thứ tự ưu tiên đã thống nhất:

1. **README chuyên nghiệp** — đã hoàn thành, có link demo, badge, kiến
   trúc, hướng dẫn chạy local.
2. **Polish giao diện (UI/UX)** — nâng cấp trang chủ có banner, ảnh phòng
   thật (tích hợp Cloudinary), responsive tốt hơn, loading skeleton thay
   vì chữ "Đang tải...", trang Admin quản trị phòng/booking ngay trên
   Frontend (hiện vẫn phải dùng Swagger để tạo dữ liệu).
3. **Release 2 (Backend)**: Receptionist/Manager role, Service (Spa,
   Breakfast...), Voucher, Dashboard thống kê — mở rộng theo đúng roadmap
   đã đặt ra ở `docs/REQUIREMENTS.md`.