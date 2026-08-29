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

**Seed dữ liệu Role (chạy 1 lần trong MySQL):**
```sql
CREATE DATABASE IF NOT EXISTS sellingphone_db;
USE sellingphone_db;

CREATE TABLE roles (
    role_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    description VARCHAR(255)
);

CREATE TABLE permissions (
    permission_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    description VARCHAR(255)
);

CREATE TABLE role_permission (
    role_id INT,
    permission_id INT,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(role_id),
    FOREIGN KEY (permission_id) REFERENCES permissions(permission_id)
);

CREATE TABLE user (
    UserID INT AUTO_INCREMENT PRIMARY KEY,
    role_id INT,
    username VARCHAR(255),
    Password VARCHAR(255),
    email VARCHAR(255),
    PhoneNumber VARCHAR(255),
    gender VARCHAR(255),
    Address TEXT,
    CreatedAt TIMESTAMP,
    UpdatedAt TIMESTAMP,
    Avatar VARCHAR(255),
    FullName VARCHAR(255),
    status TINYINT(1) DEFAULT 1,
    FOREIGN KEY (role_id) REFERENCES roles(role_id)
);

CREATE TABLE address (
    addressID INT AUTO_INCREMENT PRIMARY KEY,
    street VARCHAR(255),
    district VARCHAR(255),
    city VARCHAR(255),
    is_default TINYINT(1),
    UserID_FK INT,
    FOREIGN KEY (UserID_FK) REFERENCES user(UserID)
);

CREATE TABLE categories (
    CategoryID INT AUTO_INCREMENT PRIMARY KEY,
    CategoryName VARCHAR(255)
);

CREATE TABLE brands (
    BrandID INT AUTO_INCREMENT PRIMARY KEY,
    BrandName VARCHAR(255),
    BrandLogo VARCHAR(255)
);

CREATE TABLE product (
    ProductID INT AUTO_INCREMENT PRIMARY KEY,
    BrandID_FK INT,
    CategoryID_FK INT,
    ProductName VARCHAR(255),
    description VARCHAR(255),
    Image VARCHAR(255),
    status TINYINT(1) DEFAULT 1,
    FOREIGN KEY (BrandID_FK) REFERENCES brands(BrandID),
    FOREIGN KEY (CategoryID_FK) REFERENCES categories(CategoryID)
);

CREATE TABLE product_specification (
    product_id INT PRIMARY KEY,
    screen_size VARCHAR(255),
    screen_tech VARCHAR(255),
    rear_camera TEXT,
    front_camera VARCHAR(255),
    chipset VARCHAR(255),
    ram VARCHAR(255),
    rom VARCHAR(255),
    battery VARCHAR(255),
    os VARCHAR(255),
    screen_features TEXT,
    FOREIGN KEY (product_id) REFERENCES product(ProductID)
);

CREATE TABLE product_images (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT,
    image_url VARCHAR(255),
    FOREIGN KEY (product_id) REFERENCES product(ProductID)
);

CREATE TABLE version (
    VersionID INT AUTO_INCREMENT PRIMARY KEY,
    ProductID_FK INT,
    colour VARCHAR(255),
    storage VARCHAR(255),
    material VARCHAR(255),
    Price DECIMAL(15,2),
    Stock INT,
    ImageURL VARCHAR(255),
    FOREIGN KEY (ProductID_FK) REFERENCES product(ProductID)
);

CREATE TABLE review (
    ReviewID INT AUTO_INCREMENT PRIMARY KEY,
    ProductID_FK INT,
    UserID_FK INT,
    Rating INT,
    Comment TEXT,
    CreatedAt DATETIME,
    FOREIGN KEY (ProductID_FK) REFERENCES product(ProductID),
    FOREIGN KEY (UserID_FK) REFERENCES user(UserID)
);

CREATE TABLE cart (
    CartID INT AUTO_INCREMENT PRIMARY KEY,
    UserID_FK INT,
    FOREIGN KEY (UserID_FK) REFERENCES user(UserID)
);

CREATE TABLE cartdetail (
    CartID_FK INT,
    VersionID_FK INT,
    Quantity INT,
    PRIMARY KEY (CartID_FK, VersionID_FK),
    FOREIGN KEY (CartID_FK) REFERENCES cart(CartID),
    FOREIGN KEY (VersionID_FK) REFERENCES version(VersionID)
);

CREATE TABLE `order` (
    OrderID INT AUTO_INCREMENT PRIMARY KEY,
    UserID_FK INT,
    Total DECIMAL(15,2),
    status VARCHAR(255),
    CreatedAt TIMESTAMP,
    ReceiverName VARCHAR(255),
    PhoneNumber VARCHAR(255),
    ShippingAddress TEXT,
    Note TEXT,
    FOREIGN KEY (UserID_FK) REFERENCES user(UserID)
);

CREATE TABLE orderdetail (
    OrderID_FK INT,
    VersionID_FK INT,
    Quantity INT,
    Price DECIMAL(15,2),
    PRIMARY KEY (OrderID_FK, VersionID_FK),
    FOREIGN KEY (OrderID_FK) REFERENCES `order`(OrderID),
    FOREIGN KEY (VersionID_FK) REFERENCES version(VersionID)
);

CREATE TABLE banners (
    id INT AUTO_INCREMENT PRIMARY KEY,
    createdAt DATETIME(6),
    imageUrl VARCHAR(255),
    isActive BIT(1),
    linkUrl VARCHAR(1000),
    updatedAt DATETIME(6)
);

INSERT INTO roles (name, description) VALUES ('USER', 'Khách hàng');
INSERT INTO roles (name, description) VALUES ('ADMIN', 'Quản trị viên');
```

**Khởi động backend:**
```bash
mvn spring-boot:run
```
Hoặc mở IntelliJ → Run `SellingPhoneApplication`.

Server chạy tại: `http://localhost:8080`
