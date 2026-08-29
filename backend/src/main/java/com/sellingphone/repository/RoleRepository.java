package com.sellingphone.repository;

import com.sellingphone.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Tầng truy cập dữ liệu (Repository) cho entity {@link Role}.
 *
 * Dùng khi đăng ký tài khoản mới: gán role mặc định "USER" cho khách hàng.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    /** Tìm role theo tên (ví dụ: "USER", "ADMIN"). */
    Optional<Role> findByName(String name);
}
