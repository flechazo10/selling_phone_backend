package com.sellingphone.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // Auth
    USERNAME_ALREADY_EXISTS (HttpStatus.CONFLICT,              "Tên đăng nhập đã được sử dụng"),
    EMAIL_ALREADY_EXISTS    (HttpStatus.CONFLICT,              "Email đã được sử dụng"),
    INVALID_CREDENTIALS     (HttpStatus.UNAUTHORIZED,          "Tên đăng nhập hoặc mật khẩu không đúng"),
    INVALID_OTP             (HttpStatus.BAD_REQUEST,           "Mã OTP không đúng hoặc đã hết hạn"),
    INVALID_RESET_TOKEN     (HttpStatus.BAD_REQUEST,           "Token đặt lại mật khẩu không hợp lệ hoặc đã hết hạn"),
    INVALID_REFRESH_TOKEN   (HttpStatus.UNAUTHORIZED,          "Refresh token không hợp lệ hoặc đã hết hạn"),

    // User
    USER_NOT_FOUND          (HttpStatus.NOT_FOUND,             "Không tìm thấy người dùng"),
    EMAIL_NOT_FOUND         (HttpStatus.NOT_FOUND,             "Không tìm thấy tài khoản với email này"),

    // System
    ROLE_NOT_FOUND          (HttpStatus.INTERNAL_SERVER_ERROR, "Vai trò mặc định chưa được khởi tạo trong hệ thống"),
    INTERNAL_ERROR          (HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi hệ thống, vui lòng thử lại sau");

    private final HttpStatus httpStatus;
    private final String     message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message    = message;
    }
}
