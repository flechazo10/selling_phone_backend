package com.sellingphone.repository;

import com.sellingphone.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Tầng truy cập dữ liệu (Repository) cho entity {@link User}.
 *
 * Chỉ khai báo các query method cần thiết cho luồng Auth.
 * JPA tự sinh câu SQL từ tên phương thức — không cần @Query.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    /** Tìm user theo username (dùng cho đăng nhập + Spring Security). */
    Optional<User> findByUsername(String username);

    /** Tìm user theo email (dùng cho luồng quên mật khẩu). */
    Optional<User> findByEmail(String email);

    /** Kiểm tra username đã tồn tại chưa (dùng khi đăng ký). */
    boolean existsByUsername(String username);

    /** Kiểm tra email đã tồn tại chưa (dùng khi đăng ký). */
    boolean existsByEmail(String email);
}
