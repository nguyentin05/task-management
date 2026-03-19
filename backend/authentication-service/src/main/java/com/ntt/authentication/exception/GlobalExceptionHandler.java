package com.ntt.authentication.exception;

import java.util.Map;
import java.util.Objects;

import jakarta.validation.ConstraintViolation;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.ntt.authentication.dto.response.ApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse<?>> handlingAccessDeniedException(AccessDeniedException exception) {
        return buildResponse(ErrorCode.ACCESS_DENIED);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<?>> handlingValidationException(MethodArgumentNotValidException exception) {
        FieldError fieldError = exception.getFieldError();
        String enumKey = fieldError.getDefaultMessage();
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        Map<String, Object> attributes = null;

        try {
            errorCode = ErrorCode.valueOf(enumKey);

            var constraintViolation =
                    exception.getBindingResult().getAllErrors().getFirst().unwrap(ConstraintViolation.class);

            attributes = constraintViolation.getConstraintDescriptor().getAttributes();

        } catch (IllegalArgumentException e) {
            log.warn("[Auth][Controller]Không tìm thấy mã lỗi tương ứng: {}", enumKey);
        }

        String message = errorCode.getMessage();

        String fieldName = fieldError.getField();

        message = message.replace("{field}", fieldName);

        if (Objects.nonNull(attributes)) {
            message = mapAttribute(message, attributes);
        }

        return buildResponse(errorCode, message);
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    ResponseEntity<ApiResponse<?>> handlingJsonException(HttpMessageNotReadableException exception) {
        return buildResponse(ErrorCode.INVALID_JSON);
    }

    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    ResponseEntity<ApiResponse<?>> handlingTypeMismatchException(MethodArgumentTypeMismatchException exception) {
        String message = ErrorCode.TYPE_MISMATCH.getMessage().replace("{field}", exception.getName());
        return buildResponse(ErrorCode.TYPE_MISMATCH, message);
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse<?>> handlingAppException(AppException exception) {
        return buildResponse(exception.getErrorCode());
    }

    @ExceptionHandler(value = DataIntegrityViolationException.class)
    ResponseEntity<ApiResponse<?>> handlingDbException(DataIntegrityViolationException exception) {
        log.error(
                "[Auth][DB] Lỗi ràng buộc dữ liệu: {}",
                exception.getMostSpecificCause().getMessage());
        return buildResponse(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = DataAccessException.class)
    ResponseEntity<ApiResponse<?>> handlingDataAccessException(DataAccessException exception) {
        log.error("[Auth][INFRA] Lỗi không thể kết nối db: {}", exception.getMessage(), exception);
        return buildResponse(ErrorCode.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(value = NoResourceFoundException.class)
    ResponseEntity<ApiResponse<?>> handlingNotFoundException(NoResourceFoundException exception) {
        return buildResponse(ErrorCode.ENDPOINT_NOT_FOUND);
    }

    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    ResponseEntity<ApiResponse<?>> handlingMethodNotSupported(HttpRequestMethodNotSupportedException exception) {
        return buildResponse(ErrorCode.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse<?>> handlingException(Exception exception) {
        log.error("[Auth][Global]Lỗi chưa được phân loại: {}", exception.getMessage());
        return buildResponse(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    private String mapAttribute(String message, Map<String, Object> attributes) {
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            String key = entry.getKey();
            if (key.equals("groups") || key.equals("payload") || key.equals("message")) continue;

            String value = String.valueOf(entry.getValue());
            message = message.replace("{" + key + "}", value);
        }

        if (attributes.containsKey("value")) {
            String val = String.valueOf(attributes.get("value"));
            message = message.replace("{min}", val);
            message = message.replace("{max}", val);
        }
        return message;
    }

    private ResponseEntity<ApiResponse<?>> buildResponse(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getStatusCode())
                .body(ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }

    private ResponseEntity<ApiResponse<?>> buildResponse(ErrorCode errorCode, String message) {
        return ResponseEntity.status(errorCode.getStatusCode())
                .body(ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(message)
                        .build());
    }
}
