package com.sellingphone.config;

import com.sellingphone.entity.Role;
import com.sellingphone.entity.User;
import com.sellingphone.repository.RoleRepository;
import com.sellingphone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final RoleRepository  roleRepository;
    private final UserRepository  userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        // 1. Seed roles
        seedRole("USER",  "Khach hang");
        seedRole("ADMIN", "Quan tri vien");

        // 2. Seed tai khoan admin
        seedAdmin();
    }

    private void seedRole(String name, String description) {
        if (roleRepository.findByName(name).isEmpty()) {
            Role role = new Role();
            role.setName(name);
            role.setDescription(description);
            roleRepository.save(role);
            log.info("[DataInitializer] Da tao role: {}", name);
        }
    }

    private void seedAdmin() {
        if (userRepository.existsByUsername("admin")) {
            log.info("[DataInitializer] Tai khoan admin da ton tai");
            return;
        }

        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new RuntimeException("Role ADMIN chua duoc tao"));

        Timestamp now = Timestamp.from(Instant.now());

        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@sellingphone.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setFullName("Administrator");
        admin.setRole(adminRole);
        admin.setStatus((byte) 1);
        admin.setCreatedAt(now);
        admin.setUpdatedAt(now);

        userRepository.save(admin);
        log.info("[DataInitializer] Da tao tai khoan admin (username: admin, password: admin123)");
    }
}