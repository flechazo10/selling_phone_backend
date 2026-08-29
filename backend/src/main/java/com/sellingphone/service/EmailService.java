package com.sellingphone.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.security.SecureRandom;
import java.time.Duration;

/**
 * Service xử lý toàn bộ logic liên quan đến email:
 *  - Gửi mail bất đồng bộ (không block request của người dùng)
 *  - Quản lý OTP trong Redis (generate / validate / delete)
 *
 * Nội dung HTML mail được render từ Thymeleaf template (resources/templates/email/).
 *
 * OTP key pattern : "otp:<email>"
 * OTP TTL         : 5 phút
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private static final String OTP_PREFIX      = "otp:";
    private static final long   OTP_TTL_MINUTES = 5;
    private static final int    OTP_LENGTH      = 6;

    private final JavaMailSender                mailSender;
    private final RedisTemplate<String, String>  redisTemplate;
    private final TemplateEngine                 templateEngine;

    // =========================================================================
    // Gửi mail (bất đồng bộ)
    // =========================================================================

    /**
     * Tạo OTP, lưu Redis rồi gửi mail chứa OTP đến người dùng.
     * Toàn bộ quá trình chạy bất đồng bộ — request trả về ngay lập tức.
     *
     * @param toEmail email người nhận
     */
    @Async
    public void sendOtpEmail(String toEmail) {
        String otp = generateAndSaveOtp(toEmail);

        Context context = new Context();
        context.setVariable("otp", otp);
        context.setVariable("otpTtlMinutes", OTP_TTL_MINUTES);
        String htmlBody = templateEngine.process("email/otp-email", context);

        sendHtmlEmail(toEmail, "Mã xác nhận của bạn", htmlBody);
    }

    /**
     * Gửi mail HTML tuỳ chỉnh (subject + nội dung tự thiết lập).
     * Chạy bất đồng bộ — không block luồng chính.
     *
     * @param toEmail  email người nhận
     * @param subject  tiêu đề mail
     * @param htmlBody nội dung HTML của mail
     */
    @Async
    public void sendHtmlEmail(String toEmail, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
            log.info("[EmailService] Đã gửi mail đến: {}", toEmail);
        } catch (MessagingException e) {
            log.error("[EmailService] Lỗi khi gửi mail đến {}: {}", toEmail, e.getMessage());
        }
    }

    // =========================================================================
    // Quản lý OTP trong Redis
    // =========================================================================

    /**
     * Tạo OTP mới, lưu vào Redis (TTL = 5 phút).
     * OTP cũ (nếu có) bị ghi đè.
     *
     * @param email email người dùng
     * @return chuỗi OTP 6 chữ số
     */
    public String generateAndSaveOtp(String email) {
        String otp = generateOtp();
        redisTemplate.opsForValue().set(buildKey(email), otp, Duration.ofMinutes(OTP_TTL_MINUTES));
        return otp;
    }

    /**
     * Xác thực OTP do người dùng nhập.
     * Nếu đúng, OTP bị xóa ngay khỏi Redis (one-time use).
     *
     * @param email email người dùng
     * @param otp   OTP người dùng nhập vào
     * @return {@code true} nếu OTP hợp lệ và chưa hết hạn
     */
    public boolean validateOtp(String email, String otp) {
        String stored = redisTemplate.opsForValue().get(buildKey(email));
        if (stored != null && stored.equals(otp)) {
            redisTemplate.delete(buildKey(email));
            return true;
        }
        return false;
    }

    /**
     * Xóa OTP khỏi Redis (dùng khi hủy luồng, hoặc sau khi đổi mật khẩu thành công).
     *
     * @param email email người dùng
     */
    public void deleteOtp(String email) {
        redisTemplate.delete(buildKey(email));
    }

    // =========================================================================
    // Private helpers
    // =========================================================================

    private String generateOtp() {
        SecureRandom random = new SecureRandom();
        int bound = (int) Math.pow(10, OTP_LENGTH);
        return String.format("%0" + OTP_LENGTH + "d", random.nextInt(bound));
    }

    private String buildKey(String email) {
        return OTP_PREFIX + email;
    }
}
