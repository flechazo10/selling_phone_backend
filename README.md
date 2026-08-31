## ⚙️ Cấu hình môi trường

File `application.properties` bị ẩn khỏi Git vì chứa thông tin nhạy cảm.  
Sau khi clone về, tạo file tại:

```
src/main/resources/application.properties
```

Nội dung mẫu:

```properties
# Server
server.port=8080

# Database (MySQL)
spring.datasource.url=jdbc:mysql://localhost:3306/sellingphone?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# Redis
spring.data.redis.host=localhost
spring.data.redis.port=6379

# Mail (Gmail SMTP)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=YOUR_GMAIL@gmail.com
spring.mail.password=YOUR_GMAIL_APP_PASSWORD
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# JWT
jwt.secret=YOUR_BASE64_SECRET_KEY
jwt.expiration-ms=900000
jwt.refresh-expiration-ms=604800000
```

---

### 🔧 Hướng dẫn lấy từng giá trị

**1. `spring.datasource.password`**  
→ Mật khẩu MySQL của bạn (tài khoản `root` hoặc user bạn tạo).

**2. `spring.mail.username` + `spring.mail.password`**  
→ Dùng để gửi OTP qua Gmail. Làm theo các bước sau:
- Vào [Google Account](https://myaccount.google.com) → **Security**
- Bật **2-Step Verification** (nếu chưa bật)
- Tìm mục **App passwords** → Tạo mới → Chọn app: **Mail** → Chọn device: **Other**
- Google sẽ cấp mật khẩu **16 ký tự** → dùng mật khẩu đó cho `spring.mail.password`

**3. `jwt.secret`**  
→ Chuỗi Base64 dài ≥ 32 ký tự. Tự tạo bằng lệnh:
```bash
# Trên Linux/macOS
openssl rand -base64 32

# Trên Windows (PowerShell)
[Convert]::ToBase64String((1..32 | ForEach-Object { Get-Random -Maximum 256 }) -as [byte[]])
```

---

### 🐳 Yêu cầu hệ thống

| Công cụ | Phiên bản |
|---------|-----------|
| Java | 17+ |
| Maven | 3.8+ |
| MySQL | 8.0+ |
| Redis | 6+ (khuyên dùng Docker) |

**Chạy Redis bằng Docker:**
```bash
docker run -d --name redis -p 6379:6379 redis:alpine
# Lần sau chỉ cần:
docker start redis
```

**Khởi động backend:**
```bash
mvn spring-boot:run
```
Hoặc mở IntelliJ → Run `SellingPhoneApplication`.

Server chạy tại: `http://localhost:8080` 

**Tạm thời test dữ liệu với :**
USE sellingphone_db;

INSERT INTO categories (CategoryName) VALUES 
('Điện thoại thông minh'),
('Máy tính bảng');

INSERT INTO brands (BrandName, BrandLogo) VALUES 
('Apple', 'apple_logo.jpg'),
('Samsung', 'samsung_logo.jpg'),
('Xiaomi', 'xiaomi_logo.jpg');

INSERT INTO product (BrandID_FK, CategoryID_FK, ProductName, description, Image, status) VALUES 
(1, 1, 'iPhone 15 Pro Max', 'Màn hình 6.7 inch, chip A17 Pro siêu mạnh mẽ.', 'ip15pm_main.jpg', 1),
(2, 1, 'Samsung Galaxy S24 Ultra', 'Màn hình 6.8 inch, camera 200MP, tích hợp Galaxy AI.', 's24ultra_main.jpg', 1);

INSERT INTO product_specification (product_id, screen_size, screen_tech, rear_camera, front_camera, chipset, ram, rom, battery, os, screen_features) VALUES 
(1, '6.7 inch', 'Super Retina XDR OLED', '48MP + 12MP + 12MP', '12MP', 'Apple A17 Pro 6 nhân', '8GB', '256GB', '4422 mAh', 'iOS 17', '120Hz ProMotion, Dynamic Island'),
(2, '6.8 inch', 'Dynamic AMOLED 2X', '200MP + 50MP + 12MP + 10MP', '12MP', 'Snapdragon 8 Gen 3', '12GB', '256GB', '5000 mAh', 'Android 14', '120Hz, HDR10+, Độ sáng 2600 nits');

INSERT INTO product_images (product_id, image_url) VALUES 
(1, 'ip15pm_goc_truoc.jpg'),
(1, 'ip15pm_goc_sau.jpg'),
(1, 'ip15pm_cung_hop.jpg'),
(2, 's24ultra_goc_truoc.jpg'),
(2, 's24ultra_mat_sau.jpg'),
(2, 's24ultra_spen.jpg');

INSERT INTO version (ProductID_FK, colour, storage, material, Price, Stock, ImageURL) VALUES 
(1, 'Titan Tự nhiên', '256GB', 'Khung Titan, Mặt lưng kính', 29990000.00, 50, 'ip15pm_natural_256.jpg'),
(1, 'Titan Đen', '256GB', 'Khung Titan, Mặt lưng kính', 29500000.00, 30, 'ip15pm_black_256.jpg'),
(1, 'Titan Tự nhiên', '512GB', 'Khung Titan, Mặt lưng kính', 34990000.00, 15, 'ip15pm_natural_512.jpg'),
(2, 'Xám Titan', '256GB', 'Khung Titan, Mặt lưng kính', 31990000.00, 60, 's24_gray_256.jpg'),
(2, 'Đen Titan', '512GB', 'Khung Titan, Mặt lưng kính', 36990000.00, 25, 's24_black_512.jpg');

