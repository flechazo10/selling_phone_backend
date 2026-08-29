package com.sellingphone.config;

import com.sellingphone.entity.Permission;
import com.sellingphone.entity.User;
import com.sellingphone.exception.AppException;
import com.sellingphone.exception.ErrorCode;
import com.sellingphone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Cầu nối giữa Spring Security và database.
 *
 * Spring Security gọi {@code loadUserByUsername} khi xác thực đăng nhập.
 * Method này load User từ DB và map quyền (Permission) thành GrantedAuthority
 * để Spring Security có thể phân quyền endpoint.
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Load user theo username, map permissions thành authorities.
     *
     * @throws UsernameNotFoundException nếu không tìm thấy user
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Lấy danh sách quyền từ Role → Permission
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        if (user.getRole() != null && user.getRole().getPermissions() != null) {
            for (Permission permission : user.getRole().getPermissions()) {
                authorities.add(new SimpleGrantedAuthority(permission.getName()));
            }
        }

        // Thêm role dạng "ROLE_XXX" để dùng với @PreAuthorize("hasRole('...')")
        if (user.getRole() != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName()));
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                // status = 0 → tài khoản bị vô hiệu hóa
                .disabled(user.getStatus() != null && user.getStatus() == 0)
                .build();
    }
}
