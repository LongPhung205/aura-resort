package com.phungvanlong.booking_hotel.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.phungvanlong.booking_hotel.dto.response.ApiResponse;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PessimisticLockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException ex) {
        HttpStatus status = ex.getStatus() != null ? ex.getStatus() : HttpStatus.BAD_REQUEST;
        log.warn("Business Exception: {} [code={}, status={}]", ex.getMessage(), ex.getErrorCode(), status);
        return ResponseEntity.status(status)
                .body(ApiResponse.error(ex.getMessage(), ex.getErrorCode()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.warn("Resource Not Found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage(), "RESOURCE_NOT_FOUND"));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentialsException(BadCredentialsException ex) {
        log.warn("Authentication failed (Bad Credentials): {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Sai email hoặc mật khẩu", "BAD_CREDENTIALS"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("Access Denied: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("Bạn không có quyền thực hiện thao tác này", "ACCESS_DENIED"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    if (error instanceof FieldError fieldError) {
                        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
                    }
                    return error.getDefaultMessage();
                })
                .collect(Collectors.joining(", "));

        log.warn("Validation Error: {}", errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(errorMessage, "VALIDATION_ERROR"));
    }

    @ExceptionHandler({
        ObjectOptimisticLockingFailureException.class,
        OptimisticLockException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleOptimisticLockException(Exception ex) {
        log.warn("Optimistic lock conflict: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error("Dữ liệu đã bị thay đổi bởi giao dịch khác trong cùng thời điểm. Vui lòng tải lại và thử lại!", "OPTIMISTIC_LOCK_CONFLICT"));
    }

    @ExceptionHandler({
        PessimisticLockingFailureException.class,
        PessimisticLockException.class
    })
    public ResponseEntity<ApiResponse<Void>> handlePessimisticLockException(Exception ex) {
        log.warn("Pessimistic lock conflict: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error("Hệ thống đang xử lý yêu cầu đặt phòng khác cho căn này. Vui lòng thử lại sau giây lát!", "CONCURRENT_REQUEST_CONFLICT"));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException ife && ife.getTargetType() != null && ife.getTargetType().isEnum()) {
            String fieldName = ife.getPath().isEmpty() ? "trường dữ liệu" : ife.getPath().get(ife.getPath().size() - 1).getFieldName();
            Object[] acceptedValues = ife.getTargetType().getEnumConstants();
            String message = String.format("Giá trị '%s' của trường '%s' không hợp lệ. Các giá trị được chấp nhận: %s",
                    ife.getValue(), fieldName, Arrays.toString(acceptedValues));
            log.warn("Invalid Enum Value: {}", message);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(message, "INVALID_ENUM_VALUE"));
        }
        log.warn("Malformed JSON request: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Dữ liệu gửi lên không đúng định dạng JSON hoặc kiểu dữ liệu", "MALFORMED_JSON"));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        Class<?> requiredType = ex.getRequiredType();
        if (requiredType != null && requiredType.isEnum()) {
            String message = String.format("Tham số '%s' nhận giá trị '%s' không hợp lệ. Các giá trị được chấp nhận: %s",
                    ex.getName(), ex.getValue(), Arrays.toString(requiredType.getEnumConstants()));
            log.warn("Parameter Type Mismatch (Enum): {}", message);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(message, "INVALID_ENUM_VALUE"));
        }
        String message = String.format("Tham số '%s' không đúng định dạng yêu cầu", ex.getName());
        log.warn("Parameter Type Mismatch: {}", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(message, "TYPE_MISMATCH"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGlobalException(Exception ex) {
        // Ghi log ERROR kèm đầy đủ Stack Trace để debug chính xác dòng code gây lỗi 500
        log.error("Unhandled Internal Server Error (500): {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Lỗi hệ thống: " + ex.getMessage(), "INTERNAL_SERVER_ERROR"));
    }
}
