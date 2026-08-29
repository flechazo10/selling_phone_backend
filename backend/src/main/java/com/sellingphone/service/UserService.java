package com.sellingphone.service;

import com.sellingphone.config.JwtService;
import com.sellingphone.dto.request.ForgotPasswordRequest;
import com.sellingphone.dto.request.LoginRequest;
import com.sellingphone.dto.request.OtpVerifyRequest;
import com.sellingphone.dto.request.RefreshTokenRequest;
import com.sellingphone.dto.request.RegisterRequest;
import com.sellingphone.dto.request.ResetPasswordRequest;
import com.sellingphone.dto.response.AuthResponse;
import com.sellingphone.dto.response.UserResponse;
import com.sellingphone.entity.Role;
import com.sellingphone.entity.User;
import com.sellingphone.exception.AppException;
import com.sellingphone.exception.ErrorCode;
import com.sellingphone.mapper.UserMapper;
import com.sellingphone.repository.RoleRepository;
import com.sellingphone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository        userRepository;
    private final RoleRepository        roleRepository;
    private final PasswordEncoder       passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService            jwtService;
    private final UserDetailsService    userDetailsService;
    private final EmailService          emailService;
    private final UserMapper            userMapper;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    private static final String REFRESH_PREFIX    = "refresh:";
    private static final String RESET_PREFIX      = "reset:";
    private static final long   RESET_TTL_MINUTES = 10;
    private static final String DEFAULT_ROLE      = "USER";

    // -----------------------------------------------
    // 1. Đăng ký
    // -----------------------------------------------
    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        Role userRole = roleRepository.findByName(DEFAULT_ROLE)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        Timestamp now = Timestamp.from(Instant.now());

        User newUser = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(userRole)
                .status((byte) 1)
                .createdAt(now)
                .updatedAt(now)
                .build();

        userRepository.save(newUser);
        log.info("[UserService] Tài khoản mới: {}", request.getUsername());
    }

    // -----------------------------------------------
    // 2. Đăng nhập → trả về JWT
    // -----------------------------------------------
    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (BadCredentialsException |
                 org.springframework.security.core.userdetails.UsernameNotFoundException e) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String accessToken  = jwtService.generateAccessToken(userDetails);
        String refreshToken = generateAndSaveRefreshToken(request.getUsername());

        log.info("[UserService] Đăng nhập: {}", request.getUsername());

        return AuthResponse.builder()
                .tokenType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtService.getExpirationMs())
                .build();
    }

    // -----------------------------------------------
    // 3. Quên mật khẩu — gửi OTP qua Gmail
    // -----------------------------------------------
    public void forgotPassword(ForgotPasswordRequest request) {
        if (!userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_NOT_FOUND);
        }
        emailService.sendOtpEmail(request.getEmail());
        log.info("[UserService] Gửi OTP đến: {}", request.getEmail());
    }

    // -----------------------------------------------
    // 4. Xác thực OTP — trả về resetToken (10 phút)
    // -----------------------------------------------
    public String verifyOtp(OtpVerifyRequest request) {
        if (!emailService.validateOtp(request.getEmail(), request.getOtp())) {
            throw new AppException(ErrorCode.INVALID_OTP);
        }

        String resetToken = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(
                RESET_PREFIX + request.getEmail(),
                resetToken,
                Duration.ofMinutes(RESET_TTL_MINUTES)
        );
        log.info("[UserService] OTP hợp lệ, cấp resetToken cho: {}", request.getEmail());
        return resetToken;
    }

    // -----------------------------------------------
    // 5. Đặt lại mật khẩu
    // -----------------------------------------------
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        String storedToken = redisTemplate.opsForValue().get(RESET_PREFIX + request.getEmail());
        if (storedToken == null || !storedToken.equals(request.getResetToken())) {
            throw new AppException(ErrorCode.INVALID_RESET_TOKEN);
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_FOUND));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(Timestamp.from(Instant.now()));
        userRepository.save(user);

        redisTemplate.delete(RESET_PREFIX + request.getEmail());
        log.info("[UserService] Đặt lại mật khẩu: {}", request.getEmail());
    }

    // -----------------------------------------------
    // 6. Refresh access token
    // -----------------------------------------------
    public AuthResponse refresh(RefreshTokenRequest request) {
        String username = redisTemplate.opsForValue()
                .get(REFRESH_PREFIX + request.getRefreshToken());
        if (username == null) {
            throw new AppException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String newAccessToken   = jwtService.generateAccessToken(userDetails);

        return AuthResponse.builder()
                .tokenType("Bearer")
                .accessToken(newAccessToken)
                .refreshToken(request.getRefreshToken())
                .expiresIn(jwtService.getExpirationMs())
                .build();
    }

    // -----------------------------------------------
    // 7. Đăng xuất
    // -----------------------------------------------
    public void logout(RefreshTokenRequest request) {
        redisTemplate.delete(REFRESH_PREFIX + request.getRefreshToken());
        log.info("[UserService] Đăng xuất: refresh token đã hủy");
    }

    // -----------------------------------------------
    // 8. Lấy thông tin profile
    // -----------------------------------------------
    public UserResponse getProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toUserResponse(user);
    }

    // --- Helper ---
    private String generateAndSaveRefreshToken(String username) {
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(
                REFRESH_PREFIX + token,
                username,
                Duration.ofMillis(refreshExpirationMs)
        );
        return token;
    }
}
