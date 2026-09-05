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
```sql
use sellingphone_db;

INSERT INTO categories (CategoryName) VALUES 
('Điện thoại thông minh');

INSERT INTO brands (BrandName, BrandLogo) VALUES 
('Apple', 'https://res.cloudinary.com/iukp3opy/image/upload/v1788616886/Apple_logo_black.svg.webp'),
('Samsung', 'https://res.cloudinary.com/iukp3opy/image/upload/v1788616891/Samsung_old_logo_before_year_2015.svg.webp');

INSERT INTO product (BrandID_FK, CategoryID_FK, ProductName, description, Image, status) VALUES 
(1, 1, 'iPhone 17 Pro Max', 'Siêu phẩm Apple 2026 với màn hình lớn hơn, chip A19 Pro và hệ thống camera nâng cấp toàn diện.', 'https://res.cloudinary.com/iukp3opy/image/upload/v1788615024/iphone-17-pro-max_3.jpg', 1),
(2, 1, 'Samsung Galaxy S26', 'Flagship cao cấp từ Samsung mang lại trải nghiệm mạnh mẽ với Galaxy AI thế hệ mới.', 'https://res.cloudinary.com/iukp3opy/image/upload/v1788616002/samsung-galaxy-s26-1.webp', 1);

INSERT INTO product_specification (product_id, screen_size, screen_tech, rear_camera, front_camera, chipset, ram, rom, battery, os, screen_features) VALUES 
(1, '6.9 inch', 'LTPO Super Retina XDR OLED', '48MP + 48MP + 48MP', '24MP', 'Apple A19 Pro', '12GB', 'Lên đến 1TB', '4676 mAh', 'iOS 19', '120Hz ProMotion, Always-On display, Dynamic Island'),
(2, '6.2 inch', 'Dynamic AMOLED 2X', '50MP + 12MP + 10MP', '12MP', 'Snapdragon 8 Gen 5 for Galaxy', '12GB', 'Lên đến 512GB', '4000 mAh', 'Android 16', '120Hz, HDR10+, Độ sáng 3000 nits');

INSERT INTO product_images (product_id, image_url) VALUES 
(1, 'https://res.cloudinary.com/iukp3opy/image/upload/v1788615024/iphone-17-pro-max_3.jpg'),
(1, 'https://res.cloudinary.com/iukp3opy/image/upload/v1788615033/iphone-17-pro-max_1_3.webp'),
(1, 'https://res.cloudinary.com/iukp3opy/image/upload/v1788615042/iphone-17-pro-max-1_4.jpg'),
(1, 'https://res.cloudinary.com/iukp3opy/image/upload/v1788615051/iphone-17-pro-max-2_1_1.webp'),
(1, 'https://res.cloudinary.com/iukp3opy/image/upload/v1788615059/iphone-17-pro-max-3.webp'),
(1, 'https://res.cloudinary.com/iukp3opy/image/upload/v1788615067/iphone-17-pro-max-4.jpg'),
(1, 'https://res.cloudinary.com/iukp3opy/image/upload/v1788615091/iphone-17-pro-max-5.jpg'),
(2, 'https://res.cloudinary.com/iukp3opy/image/upload/v1788616002/samsung-galaxy-s26-1.webp'),
(2, 'https://res.cloudinary.com/iukp3opy/image/upload/v1788616007/samsung-galaxy-s26-2.webp'),
(2, 'https://res.cloudinary.com/iukp3opy/image/upload/v1788616013/samsung-galaxy-s26-4.webp'),
(2, 'https://res.cloudinary.com/iukp3opy/image/upload/v1788616019/samsung-galaxy-s26-8.webp'),
(2, 'https://res.cloudinary.com/iukp3opy/image/upload/v1788616023/samsung-galaxy-s26-9.webp'),
(2, 'https://res.cloudinary.com/iukp3opy/image/upload/v1788616027/slider-samsung-galaxy-s26-12gb-256gb.webp'),
(2, 'https://res.cloudinary.com/iukp3opy/image/upload/v1788616031/slider-samsung-galaxy-s26-12gb-256gb-1.webp'),
(2, 'https://res.cloudinary.com/iukp3opy/image/upload/v1788616037/slider-samsung-galaxy-s26-plus.webp'),
(2, 'https://res.cloudinary.com/iukp3opy/image/upload/v1788616045/slider-samsung-galaxy-s26-plus-1.jpg');

INSERT INTO version (ProductID_FK, colour, storage, material, Price, Stock, ImageURL) VALUES 
(1, 'Xanh đậm', '256GB', 'Khung Titanium, Mặt lưng kính nhám', 34990000.00, 50, 'https://res.cloudinary.com/iukp3opy/image/upload/v1788615101/iphone-17-pro-max-1.webp'),
(1, 'Bạc', '256GB', 'Khung Titanium, Mặt lưng kính nhám', 34990000.00, 50, 'https://res.cloudinary.com/iukp3opy/image/upload/v1788615109/iphone-17-pro-max-2.webp'),
(1, 'Cam vũ trụ', '256GB', 'Khung Titanium, Mặt lưng kính nhám', 34990000.00, 50, 'https://res.cloudinary.com/iukp3opy/image/upload/v1788615141/iphone-17-pro-256-gb.png'),
(1, 'Xanh đậm', '512GB', 'Khung Titanium, Mặt lưng kính nhám', 39990000.00, 30, 'https://res.cloudinary.com/iukp3opy/image/upload/v1788615101/iphone-17-pro-max-1.webp'),
(1, 'Bạc', '512GB', 'Khung Titanium, Mặt lưng kính nhám', 39990000.00, 30, 'https://res.cloudinary.com/iukp3opy/image/upload/v1788615109/iphone-17-pro-max-2.webp'),
(1, 'Cam vũ trụ', '512GB', 'Khung Titanium, Mặt lưng kính nhám', 39990000.00, 30, 'https://res.cloudinary.com/iukp3opy/image/upload/v1788615141/iphone-17-pro-256-gb.png'),
(1, 'Xanh đậm', '1TB', 'Khung Titanium, Mặt lưng kính nhám', 45990000.00, 20, 'https://res.cloudinary.com/iukp3opy/image/upload/v1788615101/iphone-17-pro-max-1.webp'),
(1, 'Bạc', '1TB', 'Khung Titanium, Mặt lưng kính nhám', 45990000.00, 20, 'https://res.cloudinary.com/iukp3opy/image/upload/v1788615109/iphone-17-pro-max-2.webp'),
(1, 'Cam vũ trụ', '1TB', 'Khung Titanium, Mặt lưng kính nhám', 45990000.00, 20, 'https://res.cloudinary.com/iukp3opy/image/upload/v1788615141/iphone-17-pro-256-gb.png'),
(2, 'Tím Cobalt', '256GB', 'Khung Nhôm Armor, Mặt lưng kính', 22990000.00, 60, 'https://res.cloudinary.com/iukp3opy/image/upload/v1788616066/t%C3%ADm.webp'),
(2, 'Đen Classic', '256GB', 'Khung Nhôm Armor, Mặt lưng kính', 22990000.00, 60, 'https://res.cloudinary.com/iukp3opy/image/upload/v1788616060/%C4%91en.webp'),
(2, 'Xanh Sky Blue', '256GB', 'Khung Nhôm Armor, Mặt lưng kính', 22990000.00, 60, 'https://res.cloudinary.com/iukp3opy/image/upload/v1788616075/xanh_sky.webp'),
(2, 'Trắng Classic', '256GB', 'Khung Nhôm Armor, Mặt lưng kính', 22990000.00, 60, 'https://res.cloudinary.com/iukp3opy/image/upload/v1788616070/tr%E1%BA%AFng.webp'),
(2, 'Tím Cobalt', '512GB', 'Khung Nhôm Armor, Mặt lưng kính', 26990000.00, 40, 'https://res.cloudinary.com/iukp3opy/image/upload/v1788616066/t%C3%ADm.webp'),
(2, 'Đen Classic', '512GB', 'Khung Nhôm Armor, Mặt lưng kính', 26990000.00, 40, 'https://res.cloudinary.com/iukp3opy/image/upload/v1788616060/%C4%91en.webp'),
(2, 'Xanh Sky Blue', '512GB', 'Khung Nhôm Armor, Mặt lưng kính', 26990000.00, 40, 'https://res.cloudinary.com/iukp3opy/image/upload/v1788616075/xanh_sky.webp'),
(2, 'Trắng Classic', '512GB', 'Khung Nhôm Armor, Mặt lưng kính', 26990000.00, 40, 'https://res.cloudinary.com/iukp3opy/image/upload/v1788616070/tr%E1%BA%AFng.webp');
