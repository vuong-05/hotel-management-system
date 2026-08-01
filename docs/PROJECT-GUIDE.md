# Hotel Management System — Hướng dẫn tổng hợp (Phase 1 → 5)

> File này tổng hợp lại toàn bộ những gì đã làm từ Phase 1 đến Phase 5, đã gộp
> sẵn các lỗi thực tế gặp phải và cách fix, để làm lại (hoặc đối chiếu) một lần
> là đúng, không phải sửa qua sửa lại nhiều lần.

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
- Project: **Maven**, Language: **Java**, Spring Boot: **3.3.x**
- Java: **21**
- Group: `com.hotelmanagement`, Artifact: `backend`, Packaging: **Jar**
- Dependencies: Spring Web, Spring Data JPA, Spring Security, Validation,
  MySQL Driver, Lombok, Spring Boot DevTools

Giải nén đè vào thư mục `backend/` đã có sẵn trong repo.

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

## Bảng tổng hợp lỗi thường gặp & cách fix (tra cứu nhanh)

| Lỗi | Nguyên nhân | Cách fix |
|---|---|---|
| `TypeTag :: UNKNOWN` khi compile | JDK quá mới (23+) không tương thích Lombok | Cài & trỏ project dùng JDK 21 LTS |
| `java -version` vẫn ra bản cũ sau khi cài JDK mới | Biến môi trường `JAVA_HOME`/`Path` chưa cập nhật | Sửa `JAVA_HOME` trong System Environment Variables, mở terminal mới |
| `Public Key Retrieval is not allowed` | MySQL 8 dùng `caching_sha2_password`, thiếu tham số JDBC | Thêm `&allowPublicKeyRetrieval=true` vào JDBC URL (chỉ dùng local) |
| File `application-local.yaml` bị lộ lên GitHub | Gõ nhầm `.yaml` thay vì `.yml`, `.gitignore` chỉ khai 1 đuôi | Đổi tên đúng `.yml`, sửa `.gitignore` chặn cả 2 đuôi, đổi secret đã lộ |
| Thư mục tạo xong nhưng không thấy trên GitHub | Git không track thư mục rỗng | Thêm file `.gitkeep` hoặc file thật vào thư mục |

---

## Tiếp theo: Phase 6 — Authentication (JWT)

Sẽ triển khai: Entity `User`, `Role`; Spring Security config; JWT
generate/validate; API `/auth/register`, `/auth/login`,
`/auth/refresh-token`, `/auth/logout`.