package com.sellingphone.exception;

import com.sellingphone.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * Bắt mọi exception từ Controller → chuyển thành JSON response nhất quán.
 *
 * Thứ tự ưu tiên bắt:
 *  1. {@link MethodArgumentNotValidException} — lỗi @Valid trên DTO (400)
 *  2. {@link AppException}                    — lỗi nghiệp vụ có mã lỗi
 *  3. {@link Exception}                        — lỗi không lường trước (500)
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Xử lý lỗi validation từ @Valid / @Validated trên request body.
     * Gộp tất cả lỗi của các field thành một chuỗi thông báo.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(errorMessage));
    }

    /**
     * Xử lý lỗi nghiệp vụ được ném ra từ Service.
     * HTTP status lấy từ {@link ErrorCode#getHttpStatus()}.
     */
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<Void>> handleAppException(AppException ex) {
        log.warn("[AppException] {} — {}", ex.getErrorCode(), ex.getMessage());
        return ResponseEntity
                .status(ex.getErrorCode().getHttpStatus())
                .body(ApiResponse.fail(ex.getMessage()));
    }

    /**
     * Bắt mọi lỗi không lường trước — trả về 500 không lộ chi tiết nội bộ.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneral(Exception ex) {
        log.error("[UnhandledException]", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail(ErrorCode.INTERNAL_ERROR.getMessage()));
    }
}
