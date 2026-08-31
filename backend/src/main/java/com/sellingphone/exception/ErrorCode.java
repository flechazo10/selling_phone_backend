package com.sellingphone.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // Auth
    USERNAME_ALREADY_EXISTS (HttpStatus.CONFLICT,              "TÃªn Ä‘Äƒng nháº­p Ä‘Ã£ Ä‘Æ°á»£c sá»­ dá»¥ng"),
    EMAIL_ALREADY_EXISTS    (HttpStatus.CONFLICT,              "Email Ä‘Ã£ Ä‘Æ°á»£c sá»­ dá»¥ng"),
    INVALID_CREDENTIALS     (HttpStatus.UNAUTHORIZED,          "TÃªn Ä‘Äƒng nháº­p hoáº·c máº­t kháº©u khÃ´ng Ä‘Ãºng"),
    INVALID_OTP             (HttpStatus.BAD_REQUEST,           "MÃ£ OTP khÃ´ng Ä‘Ãºng hoáº·c Ä‘Ã£ háº¿t háº¡n"),
    INVALID_RESET_TOKEN     (HttpStatus.BAD_REQUEST,           "Token Ä‘áº·t láº¡i máº­t kháº©u khÃ´ng há»£p lá»‡ hoáº·c Ä‘Ã£ háº¿t háº¡n"),
    INVALID_REFRESH_TOKEN   (HttpStatus.UNAUTHORIZED,          "Refresh token khÃ´ng há»£p lá»‡ hoáº·c Ä‘Ã£ háº¿t háº¡n"),

    // Product
    PRODUCT_NOT_FOUND       (HttpStatus.NOT_FOUND,             "Khong tim thay san pham"),

    // User
    USER_NOT_FOUND          (HttpStatus.NOT_FOUND,             "KhÃ´ng tÃ¬m tháº¥y ngÆ°á»i dÃ¹ng"),
    EMAIL_NOT_FOUND         (HttpStatus.NOT_FOUND,             "KhÃ´ng tÃ¬m tháº¥y tÃ i khoáº£n vá»›i email nÃ y"),

    // System
    ROLE_NOT_FOUND          (HttpStatus.INTERNAL_SERVER_ERROR, "Vai trÃ² máº·c Ä‘á»‹nh chÆ°a Ä‘Æ°á»£c khá»Ÿi táº¡o trong há»‡ thá»‘ng"),
    INTERNAL_ERROR          (HttpStatus.INTERNAL_SERVER_ERROR, "Lá»—i há»‡ thá»‘ng, vui lÃ²ng thá»­ láº¡i sau");

    private final HttpStatus httpStatus;
    private final String     message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message    = message;
    }
}
